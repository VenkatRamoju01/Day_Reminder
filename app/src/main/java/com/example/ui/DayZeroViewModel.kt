package com.example.ui

import android.app.Application
import android.graphics.Bitmap
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.GoalRepository
import com.example.data.TestModeState
import com.example.model.CountdownCalculator
import com.example.model.CountdownResult
import com.example.model.Goal
import com.example.model.LayoutPosition
import com.example.model.WallpaperTarget
import com.example.model.WallpaperTheme
import com.example.wallpaper.DayZeroWallpaperManager
import com.example.wallpaper.WallpaperApplyResult
import com.example.wallpaper.WallpaperGenerator
import com.example.worker.WorkManagerScheduler
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.time.LocalDate

class DayZeroViewModel(application: Application) : AndroidViewModel(application) {

    private val repository = GoalRepository.getInstance(application)
    private val wallpaperManager = DayZeroWallpaperManager(application)

    val goal: StateFlow<Goal> = repository.goalFlow
    val testMode: StateFlow<TestModeState> = repository.testModeFlow

    // Combined countdown state
    val countdown: StateFlow<CountdownResult> = combine(goal, testMode) { currentGoal, currentTestMode ->
        val effectiveDate = currentTestMode.getEffectiveCurrentDate()
        CountdownCalculator.calculate(currentGoal.targetDate, effectiveDate)
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.Eagerly,
        initialValue = CountdownCalculator.calculate(repository.getGoal().targetDate)
    )

    private val _previewBitmap = MutableStateFlow<Bitmap?>(null)
    val previewBitmap: StateFlow<Bitmap?> = _previewBitmap.asStateFlow()

    private val _isApplying = MutableStateFlow(false)
    val isApplying: StateFlow<Boolean> = _isApplying.asStateFlow()

    private val _statusMessage = MutableStateFlow<String?>(null)
    val statusMessage: StateFlow<String?> = _statusMessage.asStateFlow()

    init {
        // Schedule daily background sync on app launch
        if (goal.value.autoDailyUpdate) {
            WorkManagerScheduler.scheduleDailyUpdate(application)
        }

        // Regenerate preview whenever goal or countdown changes
        viewModelScope.launch {
            combine(goal, countdown) { currentGoal, currentCountdown ->
                Pair(currentGoal, currentCountdown)
            }.collect { (currentGoal, currentCountdown) ->
                generatePreview(currentGoal, currentCountdown)
            }
        }

        // Requirement 9: Refresh the wallpaper when the application opens if days changed
        refreshWallpaperOnOpen()
    }

    private fun generatePreview(currentGoal: Goal, currentCountdown: CountdownResult) {
        viewModelScope.launch(Dispatchers.Default) {
            // High quality preview (half scale for smooth Compose rendering)
            val previewWidth = WallpaperGenerator.PIXEL_9_WIDTH / 2
            val previewHeight = WallpaperGenerator.PIXEL_9_HEIGHT / 2
            val bitmap = WallpaperGenerator.generateWallpaper(
                goal = currentGoal,
                countdown = currentCountdown,
                width = previewWidth,
                height = previewHeight
            )
            withContext(Dispatchers.Main) {
                _previewBitmap.value = bitmap
            }
        }
    }

    fun refreshWallpaperOnOpen() {
        viewModelScope.launch {
            val currentGoal = repository.getGoal()
            if (!currentGoal.autoDailyUpdate) return@launch

            val effectiveDate = testMode.value.getEffectiveCurrentDate()
            val currentCountdown = CountdownCalculator.calculate(currentGoal.targetDate, effectiveDate)

            // If day has changed since last applied wallpaper, sync immediately!
            if (currentGoal.lastAppliedTimestamp > 0L && currentGoal.lastAppliedDays != currentCountdown.daysRemaining) {
                _statusMessage.value = "New calendar day detected. Refreshing Pixel 9 wallpaper..."
                applyWallpaper(target = currentGoal.targetScreen, isAutoSync = true)
            }
        }
    }

    fun updateTitle(title: String) {
        repository.updateGoal(title = title)
    }

    fun updateTargetDate(date: LocalDate) {
        repository.updateGoal(targetDate = date)
    }

    fun updateMotivationalMessage(message: String) {
        repository.updateGoal(motivationalMessage = message)
    }

    fun updateShowTopHeader(show: Boolean) {
        repository.updateGoal(showTopHeader = show)
    }

    fun updateTopHeaderCustomText(text: String) {
        repository.updateGoal(topHeaderCustomText = text)
    }

    fun updateCustomUnitLabel(label: String) {
        repository.updateGoal(customUnitLabel = label)
    }

    fun updateTopHeaderAlignment(align: com.example.model.TextAlignment) {
        repository.updateGoal(topHeaderAlignment = align)
    }

    fun updateTitleAlignment(align: com.example.model.TextAlignment) {
        repository.updateGoal(titleAlignment = align)
    }

    fun updateDateAlignment(align: com.example.model.TextAlignment) {
        repository.updateGoal(dateAlignment = align)
    }

    fun updateNumberAlignment(align: com.example.model.TextAlignment) {
        repository.updateGoal(numberAlignment = align)
    }

    fun updateMessageAlignment(align: com.example.model.TextAlignment) {
        repository.updateGoal(messageAlignment = align)
    }

    fun updateTheme(theme: WallpaperTheme) {
        repository.updateGoal(theme = theme)
    }

    fun updateLayoutPosition(position: LayoutPosition) {
        repository.updateGoal(layoutPosition = position)
    }

    fun updateTargetScreen(target: WallpaperTarget) {
        repository.updateGoal(targetScreen = target)
    }

    fun updateAutoDaily(enabled: Boolean) {
        repository.updateGoal(autoDailyUpdate = enabled)
        if (enabled) {
            WorkManagerScheduler.scheduleDailyUpdate(getApplication())
        } else {
            WorkManagerScheduler.cancelDailyUpdate(getApplication())
        }
    }

    fun applyWallpaper(target: WallpaperTarget = goal.value.targetScreen, isAutoSync: Boolean = false) {
        viewModelScope.launch {
            _isApplying.value = true
            val effectiveDate = testMode.value.getEffectiveCurrentDate()
            val currentGoal = goal.value
            val currentCountdown = countdown.value

            val result = wallpaperManager.applyGoalWallpaper(
                goal = currentGoal,
                targetScreen = target,
                referenceDate = effectiveDate
            )

            _isApplying.value = false
            when (result) {
                is WallpaperApplyResult.Success -> {
                    repository.recordWallpaperApplied(currentCountdown.daysRemaining)
                    _statusMessage.value = if (testMode.value.isEnabled) {
                        "Test Mode: Wallpaper applied with simulated date (${currentCountdown.displayDays} days)."
                    } else {
                        result.message
                    }
                }
                is WallpaperApplyResult.Failure -> {
                    _statusMessage.value = "Failed: ${result.error}"
                }
            }
        }
    }

    fun exportWallpaperToGallery() {
        viewModelScope.launch {
            _isApplying.value = true
            val effectiveDate = testMode.value.getEffectiveCurrentDate()
            val result = wallpaperManager.exportWallpaperToGallery(
                goal = goal.value,
                referenceDate = effectiveDate
            )
            _isApplying.value = false
            result.onSuccess {
                _statusMessage.value = "High-resolution 1080x2424 PNG saved to Pictures/DayZero"
            }.onFailure { e ->
                _statusMessage.value = "Export failed: ${e.localizedMessage}"
            }
        }
    }

    // Simulation / Test Mode Methods
    fun setSimulationOffsetDays(offset: Int) {
        repository.setTestMode(enabled = true, offsetDays = offset)
    }

    fun simulateSpecificDaysRemaining(daysTarget: Long) {
        val currentGoal = goal.value
        val today = LocalDate.now()
        // If we want daysRemaining to be X, we adjust simulation offset so (targetDate - (today + offset)) = X
        // targetDate - today - offset = X => offset = (targetDate - today) - X
        val actualDays = java.time.temporal.ChronoUnit.DAYS.between(today, currentGoal.targetDate)
        val neededOffset = (actualDays - daysTarget).toInt()
        repository.setTestMode(enabled = true, offsetDays = neededOffset)
    }

    fun resetSimulation() {
        repository.resetTestMode()
        _statusMessage.value = "Simulation mode reset to actual system date."
    }

    fun clearStatusMessage() {
        _statusMessage.value = null
    }
}
