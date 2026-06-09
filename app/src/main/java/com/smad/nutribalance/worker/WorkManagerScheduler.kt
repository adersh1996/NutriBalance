package com.smad.nutribalance.worker

import android.content.Context
import androidx.work.Constraints
import androidx.work.ExistingPeriodicWorkPolicy
import androidx.work.ExistingWorkPolicy
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.PeriodicWorkRequestBuilder
import androidx.work.WorkManager
import java.util.concurrent.TimeUnit

object WorkManagerScheduler {

    private const val WEIGHT_REMINDER_PERIODIC = "weight_reminder_periodic"
    private const val WEIGHT_REMINDER_SNOOZE = "weight_reminder_snooze"

    /**
     * Schedules a periodic check every 24 hours.
     * Uses KEEP policy so multiple app launches don't re-schedule.
     */
    fun scheduleWeightReminder(context: Context) {
        val constraints = Constraints.Builder()
            .setRequiresBatteryNotLow(false)
            .build()

        val periodicWork = PeriodicWorkRequestBuilder<WeightReminderWorker>(
            repeatInterval = 24,
            repeatIntervalTimeUnit = TimeUnit.HOURS
        )
            .setConstraints(constraints)
            .build()

        WorkManager.getInstance(context).enqueueUniquePeriodicWork(
            WEIGHT_REMINDER_PERIODIC,
            ExistingPeriodicWorkPolicy.KEEP,
            periodicWork
        )
    }

    /**
     * "Remind me later" — schedules a one-time check after 2 days.
     * The 15-day cycle is NOT reset; the worker's logic still uses the original baseline.
     */
    fun snoozeReminder(context: Context) {
        val snoozeWork = OneTimeWorkRequestBuilder<WeightReminderWorker>()
            .setInitialDelay(2, TimeUnit.DAYS)
            .build()

        WorkManager.getInstance(context).enqueueUniqueWork(
            WEIGHT_REMINDER_SNOOZE,
            ExistingWorkPolicy.REPLACE,
            snoozeWork
        )
    }
}
