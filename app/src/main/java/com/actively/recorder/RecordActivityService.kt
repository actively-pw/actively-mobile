package com.actively.recorder

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.Service
import android.content.Intent
import android.os.IBinder
import androidx.core.app.NotificationCompat
import com.actively.R
import com.actively.activity.Activity
import com.actively.recorder.usecase.RecordActivityUseCase
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.cancel
import kotlinx.coroutines.flow.launchIn
import kotlinx.datetime.Instant
import org.koin.android.ext.android.inject

class RecordActivityService : Service() {

    private val notificationManager by lazy { getSystemService(NotificationManager::class.java) }
    private val recordActivity by inject<RecordActivityUseCase>()
    private val scope = CoroutineScope(SupervisorJob())

    override fun onBind(intent: Intent?): IBinder? {
        return null
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        when (intent?.action) {
            START_ACTON -> startRecording(intent)
            STOP_ACTION -> stopRecording()
        }
        return super.onStartCommand(intent, flags, startId)
    }

    override fun onDestroy() {
        super.onDestroy()
        scope.cancel()
    }

    private fun startRecording(intent: Intent) {
        val (start, id) = parseInputData(intent) ?: return
        val notification = buildNotification()
        startForeground(NOTIFICATION_ID, notification)
        recordActivity(id, start).launchIn(scope)
    }

    private fun parseInputData(intent: Intent): Pair<Instant, Activity.Id>? {
        val start = intent.extras?.getString(START_TIMESTAMP_KEY)
            ?.let(Instant::parse) ?: return null
        val id = intent.extras?.getString(ACTIVITY_ID_KEY)?.let(Activity::Id) ?: return null
        return start to id
    }

    private fun buildNotification(): Notification {
        notificationManager.createNotificationChannel(
            NotificationChannel(CHANNEL_ID, CHANNEL_NAME, NotificationManager.IMPORTANCE_HIGH),
        )
        return NotificationCompat.Builder(this, CHANNEL_ID)
            .setSmallIcon(R.drawable.ic_launcher_foreground)
            .setContentTitle("Activity recording")
            .build()
    }

    private fun stopRecording() {
        stopForeground(STOP_FOREGROUND_REMOVE)
        stopSelf()
    }

    companion object {

        const val START_ACTON = "start_action"
        const val STOP_ACTION = "stop_action"
        const val START_TIMESTAMP_KEY = "start-key"
        const val ACTIVITY_ID_KEY = "activity-id-key"
        private const val CHANNEL_ID = "activity-notification"
        private const val CHANNEL_NAME = "Location tracking"
        private const val NOTIFICATION_ID = 1
    }
}
