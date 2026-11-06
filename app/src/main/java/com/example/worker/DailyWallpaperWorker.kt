package com.example.worker

import android.content.Context
import android.util.Log
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.example.data.GoalRepository
import com.example.model.CountdownCalculator
import com.example.wallpaper.DayZeroWallpaperManager
import com.example.wallpaper.WallpaperApplyResult
import java.time.LocalDate

class DailyWallpaperWorker(
    appContext: Context,
    workerParams: WorkerParameters
) : CoroutineWorker(appContext, workerParams) {

    override suspend fun doWork(): Result {
        Log.d(TAG, "DailyWallpaperWorker triggered")
        val repository = GoalRepository.getInstance(applicationContext)
        val goal = repository.getGoal()

        if (!goal.autoDailyUpdate) {
            Log.d(TAG, "Auto daily update disabled in goal preferences. Skipping.")
            return Result.success()
        }

        val testMode = repository.testModeFlow.value
        val effectiveDate = testMode.getEffectiveCurrentDate()
        val countdown = CountdownCalculator.calculate(goal.targetDate, effectiveDate)

        // Only update if days changed or wallpaper hasn't been set yet
        val shouldUpdate = goal.lastAppliedDays != countdown.daysRemaining || goal.lastAppliedTimestamp == 0L

        if (!shouldUpdate) {
            Log.d(TAG, "Wallpaper already matches current countdown (${countdown.daysRemaining} days). No redraw needed.")
            return Result.success()
        }

        val wallpaperManager = DayZeroWallpaperManager(applicationContext)
        val result = wallpaperManager.applyGoalWallpaper(
            goal = goal,
            targetScreen = goal.targetScreen,
            referenceDate = effectiveDate
        )

        return when (result) {
            is WallpaperApplyResult.Success -> {
                Log.i(TAG, "Successfully updated wallpaper in background: ${result.message}")
                repository.recordWallpaperApplied(countdown.daysRemaining)
                Result.success()
            }
            is WallpaperApplyResult.Failure -> {
                Log.e(TAG, "Failed to apply wallpaper in background: ${result.error}", result.throwable)
                if (runAttemptCount < 3) {
                    Result.retry()
                } else {
                    Result.failure()
                }
            }
        }
    }

    companion object {
        const val TAG = "DayZeroWorker"
        const val UNIQUE_PERIODIC_WORK_NAME = "dayzero_daily_wallpaper_work"
        const val UNIQUE_ONE_TIME_WORK_NAME = "dayzero_immediate_wallpaper_work"
    }
}
