package com.example.receiver

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.util.Log
import com.example.data.GoalRepository
import com.example.worker.WorkManagerScheduler

class DayZeroBroadcastReceiver : BroadcastReceiver() {

    override fun onReceive(context: Context, intent: Intent?) {
        val action = intent?.action ?: return
        Log.d(TAG, "Received broadcast intent action: $action")

        when (action) {
            Intent.ACTION_BOOT_COMPLETED,
            Intent.ACTION_MY_PACKAGE_REPLACED,
            Intent.ACTION_TIMEZONE_CHANGED,
            Intent.ACTION_TIME_CHANGED,
            Intent.ACTION_DATE_CHANGED -> {
                val repository = GoalRepository.getInstance(context)
                val goal = repository.getGoal()

                if (goal.autoDailyUpdate) {
                    Log.i(TAG, "Re-scheduling WorkManager and triggering immediate sync on event: $action")
                    WorkManagerScheduler.scheduleDailyUpdate(context)
                    WorkManagerScheduler.triggerImmediateSync(context)
                } else {
                    Log.d(TAG, "Auto daily update disabled. Ignoring $action")
                }
            }
        }
    }

    companion object {
        private const val TAG = "DayZeroReceiver"
    }
}
