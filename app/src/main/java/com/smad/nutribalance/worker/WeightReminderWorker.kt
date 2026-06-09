package com.smad.nutribalance.worker

import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import androidx.core.app.NotificationCompat
import androidx.hilt.work.HiltWorker
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.smad.nutribalance.MainActivity
import com.smad.nutribalance.R
import com.smad.nutribalance.data.local.preferences.UserPreferencesDataStore
import dagger.assisted.Assisted
import dagger.assisted.AssistedInject
import kotlinx.coroutines.flow.first
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@HiltWorker
class WeightReminderWorker @AssistedInject constructor(
    @Assisted private val appContext: Context,
    @Assisted workerParams: WorkerParameters,
    private val preferencesDataStore: UserPreferencesDataStore
) : CoroutineWorker(appContext, workerParams) {

    companion object {
        const val NOTIFICATION_ID = 1001
        const val CHANNEL_ID = "weight_reminder_channel"
        private const val DAYS_THRESHOLD = 15
    }

    override suspend fun doWork(): Result {
        val dateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
        val today = dateFormat.format(Date())

        // Check if we've already sent a reminder today
        val lastReminderDate = preferencesDataStore.lastReminderDate.first()
        if (lastReminderDate == today) return Result.success()

        // Get the relevant dates
        val profileSetupDate = preferencesDataStore.profileSetupDate.first()
        val lastWeightUpdateDate = preferencesDataStore.lastWeightUpdateDate.first()

        // If profile isn't set up yet, skip
        if (profileSetupDate.isEmpty()) return Result.success()

        // Use the later of setup date or last weight update date as the baseline
        val baselineDate = if (lastWeightUpdateDate.isNotEmpty() && lastWeightUpdateDate > profileSetupDate) {
            lastWeightUpdateDate
        } else {
            profileSetupDate
        }

        // Calculate days since baseline
        val baseline = dateFormat.parse(baselineDate) ?: return Result.success()
        val todayDate = dateFormat.parse(today) ?: return Result.success()
        val diffMs = todayDate.time - baseline.time
        val daysDiff = (diffMs / (1000 * 60 * 60 * 24)).toInt()

        if (daysDiff >= DAYS_THRESHOLD) {
            showNotification()
            // Mark today as the reminder date so we don't repeat within the same day
            preferencesDataStore.setLastReminderDate(today)
        }

        return Result.success()
    }

    private fun showNotification() {
        val intent = Intent(appContext, MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            putExtra("navigate_to", "weight_update")
        }
        val pendingIntent = PendingIntent.getActivity(
            appContext,
            0,
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        val notification = NotificationCompat.Builder(appContext, CHANNEL_ID)
            .setSmallIcon(android.R.drawable.ic_dialog_info)
            .setContentTitle("Time to check your progress! ⚖️")
            .setContentText("It's been 15 days. Update your weight to recalculate your calorie goals.")
            .setStyle(
                NotificationCompat.BigTextStyle()
                    .bigText("It's been 15 days. Update your weight to recalculate your calorie goals.")
            )
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)
            .setAutoCancel(true)
            .setContentIntent(pendingIntent)
            .build()

        val notificationManager =
            appContext.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        notificationManager.notify(NOTIFICATION_ID, notification)
    }
}
