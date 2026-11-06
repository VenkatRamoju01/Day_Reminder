package com.example.worker

import android.content.Context
import android.util.Log
import androidx.work.ExistingPeriodicWorkPolicy
import androidx.work.ExistingWorkPolicy
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.PeriodicWorkRequestBuilder
import androidx.work.WorkManager
import java.time.Duration
import java.time.LocalDateTime
import java.time.LocalTime
import java.util.concurrent.TimeUnit

object WorkManagerScheduler {

    private const val TAG = "WorkManagerScheduler"

    /**
     * Schedules the daily wallpaper update job to run every 24 hours,
     * initially aligned to run just after midnight (00:01 AM local time).
     */
    fun scheduleDailyUpdate(context: Context) {
        val workManager = WorkManager.getInstance(context)

        val delayMillis = calculateDelayUntilNextMidnight()
        Log.d(TAG, "Scheduling daily update with initial delay of ${delayMillis / 1000 / 60} minutes")

        val periodicWorkRequest = PeriodicWorkRequestBuilder<DailyWallpaperWorker>(24, TimeUnit.HOURS)
            .setInitialDelay(delayMillis, TimeUnit.MILLISECONDS)
            .addTag("DAYZERO_DAILY")
            .build()

        workManager.enqueueUniquePeriodicWork(
            DailyWallpaperWorker.UNIQUE_PERIODIC_WORK_NAME,
            ExistingPeriodicWorkPolicy.UPDATE,
            periodicWorkRequest
        )
    }

    /**
     * Triggers an immediate one-time background sync.
     */
    fun triggerImmediateSync(context: Context) {
        val workManager = WorkManager.getInstance(context)
        val oneTimeWork = OneTimeWorkRequestBuilder<DailyWallpaperWorker>()
            .addTag("DAYZERO_IMMEDIATE")
            .build()

        workManager.enqueueUniqueWork(
            DailyWallpaperWorker.UNIQUE_ONE_TIME_WORK_NAME,
            ExistingWorkPolicy.REPLACE,
            oneTimeWork
        )
    }

    /**
     * Cancels any scheduled background periodic work.
     */
    fun cancelDailyUpdate(context: Context) {
        WorkManager.getInstance(context).cancelUniqueWork(DailyWallpaperWorker.UNIQUE_PERIODIC_WORK_NAME)
    }

    /**
     * Calculates milliseconds until the next 00:01:00 AM.
     */
    private fun calculateDelayUntilNextMidnight(): Long {
        val now = LocalDateTime.now()
        // Target 00:01 AM tomorrow
        val targetMidnight = now.toLocalDate().plusDays(1).atTime(LocalTime.of(0, 1, 0))
        val duration = Duration.between(now, targetMidnight)
        val millis = duration.toMillis()
        return if (millis > 0) millis else 60_000L
    }
}
