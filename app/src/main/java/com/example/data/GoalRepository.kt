package com.example.data

import android.content.Context
import android.content.SharedPreferences
import com.example.model.Goal
import com.example.model.LayoutPosition
import com.example.model.TextAlignment
import com.example.model.WallpaperTarget
import com.example.model.WallpaperTheme
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import java.time.LocalDate

data class TestModeState(
    val isEnabled: Boolean = false,
    val simulatedOffsetDays: Int = 0,
    val simulatedTargetDayOverride: Long? = null
) {
    fun getEffectiveCurrentDate(): LocalDate {
        return if (isEnabled) {
            LocalDate.now().plusDays(simulatedOffsetDays.toLong())
        } else {
            LocalDate.now()
        }
    }
}

class GoalRepository(context: Context) {

    private val prefs: SharedPreferences =
        context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)

    private val _goalFlow = MutableStateFlow(loadGoal())
    val goalFlow: StateFlow<Goal> = _goalFlow.asStateFlow()

    private val _testModeFlow = MutableStateFlow(loadTestMode())
    val testModeFlow: StateFlow<TestModeState> = _testModeFlow.asStateFlow()

    private fun loadGoal(): Goal {
        val title = prefs.getString(KEY_TITLE, "Achieve the Vision") ?: "Achieve the Vision"
        val targetDateStr = prefs.getString(KEY_TARGET_DATE, null)
        val targetDate = if (targetDateStr != null) {
            try {
                LocalDate.parse(targetDateStr)
            } catch (e: Exception) {
                LocalDate.now().plusDays(42)
            }
        } else {
            LocalDate.now().plusDays(42)
        }

        val message = prefs.getString(
            KEY_MOTIVATIONAL_MESSAGE,
            "Freedom. Focus on today."
        ) ?: "Freedom. Focus on today."

        val showTopHeader = prefs.getBoolean(KEY_SHOW_TOP_HEADER, false)
        val topHeaderCustomText = prefs.getString(KEY_TOP_HEADER_CUSTOM, "") ?: ""
        val customUnitLabel = prefs.getString(KEY_CUSTOM_UNIT_LABEL, "Days Remaining") ?: "Days Remaining"

        val topHeaderAlign = parseAlignment(prefs.getString(KEY_TOP_HEADER_ALIGN, TextAlignment.CENTER.name))
        val titleAlign = parseAlignment(prefs.getString(KEY_TITLE_ALIGN, TextAlignment.CENTER.name))
        val dateAlign = parseAlignment(prefs.getString(KEY_DATE_ALIGN, TextAlignment.CENTER.name))
        val numberAlign = parseAlignment(prefs.getString(KEY_NUMBER_ALIGN, TextAlignment.CENTER.name))
        val messageAlign = parseAlignment(prefs.getString(KEY_MESSAGE_ALIGN, TextAlignment.CENTER.name))

        val themeName = prefs.getString(KEY_THEME, WallpaperTheme.PURE_BLACK.name)
        val theme = try {
            WallpaperTheme.valueOf(themeName ?: WallpaperTheme.PURE_BLACK.name)
        } catch (e: Exception) {
            WallpaperTheme.PURE_BLACK
        }

        val positionName = prefs.getString(KEY_POSITION, LayoutPosition.CENTER.name)
        val position = try {
            LayoutPosition.valueOf(positionName ?: LayoutPosition.CENTER.name)
        } catch (e: Exception) {
            LayoutPosition.CENTER
        }

        val targetScreenName = prefs.getString(KEY_TARGET_SCREEN, WallpaperTarget.BOTH.name)
        val targetScreen = try {
            WallpaperTarget.valueOf(targetScreenName ?: WallpaperTarget.BOTH.name)
        } catch (e: Exception) {
            WallpaperTarget.BOTH
        }

        val autoDailyUpdate = prefs.getBoolean(KEY_AUTO_DAILY, true)
        val lastAppliedTimestamp = prefs.getLong(KEY_LAST_APPLIED_TS, 0L)
        val lastAppliedDays = prefs.getLong(KEY_LAST_APPLIED_DAYS, -1L)

        return Goal(
            title = title,
            targetDate = targetDate,
            motivationalMessage = message,
            showTopHeader = showTopHeader,
            topHeaderCustomText = topHeaderCustomText,
            customUnitLabel = customUnitLabel,
            topHeaderAlignment = topHeaderAlign,
            titleAlignment = titleAlign,
            dateAlignment = dateAlign,
            numberAlignment = numberAlign,
            messageAlignment = messageAlign,
            theme = theme,
            layoutPosition = position,
            targetScreen = targetScreen,
            autoDailyUpdate = autoDailyUpdate,
            lastAppliedTimestamp = lastAppliedTimestamp,
            lastAppliedDays = lastAppliedDays
        )
    }

    private fun parseAlignment(name: String?): TextAlignment {
        return try {
            TextAlignment.valueOf(name ?: TextAlignment.CENTER.name)
        } catch (e: Exception) {
            TextAlignment.CENTER
        }
    }

    private fun loadTestMode(): TestModeState {
        val enabled = prefs.getBoolean(KEY_TEST_ENABLED, false)
        val offset = prefs.getInt(KEY_TEST_OFFSET, 0)
        return TestModeState(isEnabled = enabled, simulatedOffsetDays = offset)
    }

    fun getGoal(): Goal = _goalFlow.value

    fun updateGoal(
        title: String? = null,
        targetDate: LocalDate? = null,
        motivationalMessage: String? = null,
        showTopHeader: Boolean? = null,
        topHeaderCustomText: String? = null,
        customUnitLabel: String? = null,
        topHeaderAlignment: TextAlignment? = null,
        titleAlignment: TextAlignment? = null,
        dateAlignment: TextAlignment? = null,
        numberAlignment: TextAlignment? = null,
        messageAlignment: TextAlignment? = null,
        theme: WallpaperTheme? = null,
        layoutPosition: LayoutPosition? = null,
        targetScreen: WallpaperTarget? = null,
        autoDailyUpdate: Boolean? = null
    ) {
        val current = _goalFlow.value
        val updated = current.copy(
            title = title ?: current.title,
            targetDate = targetDate ?: current.targetDate,
            motivationalMessage = motivationalMessage ?: current.motivationalMessage,
            showTopHeader = showTopHeader ?: current.showTopHeader,
            topHeaderCustomText = topHeaderCustomText ?: current.topHeaderCustomText,
            customUnitLabel = customUnitLabel ?: current.customUnitLabel,
            topHeaderAlignment = topHeaderAlignment ?: current.topHeaderAlignment,
            titleAlignment = titleAlignment ?: current.titleAlignment,
            dateAlignment = dateAlignment ?: current.dateAlignment,
            numberAlignment = numberAlignment ?: current.numberAlignment,
            messageAlignment = messageAlignment ?: current.messageAlignment,
            theme = theme ?: current.theme,
            layoutPosition = layoutPosition ?: current.layoutPosition,
            targetScreen = targetScreen ?: current.targetScreen,
            autoDailyUpdate = autoDailyUpdate ?: current.autoDailyUpdate
        )

        prefs.edit().apply {
            putString(KEY_TITLE, updated.title)
            putString(KEY_TARGET_DATE, updated.targetDate.toString())
            putString(KEY_MOTIVATIONAL_MESSAGE, updated.motivationalMessage)
            putBoolean(KEY_SHOW_TOP_HEADER, updated.showTopHeader)
            putString(KEY_TOP_HEADER_CUSTOM, updated.topHeaderCustomText)
            putString(KEY_CUSTOM_UNIT_LABEL, updated.customUnitLabel)
            putString(KEY_TOP_HEADER_ALIGN, updated.topHeaderAlignment.name)
            putString(KEY_TITLE_ALIGN, updated.titleAlignment.name)
            putString(KEY_DATE_ALIGN, updated.dateAlignment.name)
            putString(KEY_NUMBER_ALIGN, updated.numberAlignment.name)
            putString(KEY_MESSAGE_ALIGN, updated.messageAlignment.name)
            putString(KEY_THEME, updated.theme.name)
            putString(KEY_POSITION, updated.layoutPosition.name)
            putString(KEY_TARGET_SCREEN, updated.targetScreen.name)
            putBoolean(KEY_AUTO_DAILY, updated.autoDailyUpdate)
            apply()
        }

        _goalFlow.value = updated
    }

    fun recordWallpaperApplied(daysRemaining: Long) {
        val now = System.currentTimeMillis()
        prefs.edit().apply {
            putLong(KEY_LAST_APPLIED_TS, now)
            putLong(KEY_LAST_APPLIED_DAYS, daysRemaining)
            apply()
        }
        _goalFlow.value = _goalFlow.value.copy(
            lastAppliedTimestamp = now,
            lastAppliedDays = daysRemaining
        )
    }

    fun setTestMode(enabled: Boolean, offsetDays: Int = 0) {
        prefs.edit().apply {
            putBoolean(KEY_TEST_ENABLED, enabled)
            putInt(KEY_TEST_OFFSET, offsetDays)
            apply()
        }
        _testModeFlow.value = TestModeState(isEnabled = enabled, simulatedOffsetDays = offsetDays)
    }

    fun resetTestMode() {
        setTestMode(enabled = false, offsetDays = 0)
    }

    companion object {
        private const val PREFS_NAME = "dayzero_goal_prefs"
        private const val KEY_TITLE = "goal_title"
        private const val KEY_TARGET_DATE = "goal_target_date"
        private const val KEY_MOTIVATIONAL_MESSAGE = "goal_motivational_message"
        private const val KEY_SHOW_TOP_HEADER = "goal_show_top_header"
        private const val KEY_TOP_HEADER_CUSTOM = "goal_top_header_custom"
        private const val KEY_CUSTOM_UNIT_LABEL = "goal_custom_unit_label"
        private const val KEY_TOP_HEADER_ALIGN = "goal_top_header_align"
        private const val KEY_TITLE_ALIGN = "goal_title_align"
        private const val KEY_DATE_ALIGN = "goal_date_align"
        private const val KEY_NUMBER_ALIGN = "goal_number_align"
        private const val KEY_MESSAGE_ALIGN = "goal_message_align"
        private const val KEY_THEME = "goal_theme"
        private const val KEY_POSITION = "goal_position"
        private const val KEY_TARGET_SCREEN = "goal_target_screen"
        private const val KEY_AUTO_DAILY = "goal_auto_daily"
        private const val KEY_LAST_APPLIED_TS = "goal_last_applied_ts"
        private const val KEY_LAST_APPLIED_DAYS = "goal_last_applied_days"

        private const val KEY_TEST_ENABLED = "test_mode_enabled"
        private const val KEY_TEST_OFFSET = "test_mode_offset"

        @Volatile
        private var instance: GoalRepository? = null

        fun getInstance(context: Context): GoalRepository {
            return instance ?: synchronized(this) {
                instance ?: GoalRepository(context.applicationContext).also { instance = it }
            }
        }
    }
}
