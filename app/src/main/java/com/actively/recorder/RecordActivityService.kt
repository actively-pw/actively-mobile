package com.actively.recorder

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.Service
import android.content.Intent
import android.os.IBinder
import androidx.core.app.NotificationCompat
import com.actively.R
import com.actively.recorder.usecase.RecordActivityUseCase
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.cancel
import kotlinx.coroutines.flow.launchIn
import kotlinx.datetime.Instant
import org.koin.android.ext.android.inject

class RecordActivityService : Service() {

    private val notificationManager by lazy { getSystemService(NotificationManager::class.java) }
    private val recordActivity by inject<RecordActivityUseCase>()
    private val scope = CoroutineScope(SupervisorJob())
    private var recordingJob: Job? = null

    override fun onBind(intent: Intent?): IBinder? {
        return null
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        when (intent?.action) {
            START_ACTON -> startRecording(intent)
            PAUSE_ACTION -> pauseRecording()
            RESUME_ACTION -> resumeRecording(intent)
            STOP_ACTION -> stopRecording()
        }
        return super.onStartCommand(intent, flags, startId)
    }

    override fun onDestroy() {
        super.onDestroy()
        scope.cancel()
    }

    private fun startRecording(intent: Intent) {
        val startedAt = intent.getStartedTimestamp() ?: return
        val notification = buildNotification()
        startForeground(NOTIFICATION_ID, notification)
        recordingJob = recordActivity(startedAt).launchIn(scope)
    }

    private fun Intent.getStartedTimestamp() = extras?.getString(START_TIMESTAMP_KEY)
        ?.let(Instant::parse)

    private fun buildNotification(): Notification {
        notificationManager.createNotificationChannel(
            NotificationChannel(CHANNEL_ID, CHANNEL_NAME, NotificationManager.IMPORTANCE_HIGH),
        )
        return NotificationCompat.Builder(this, CHANNEL_ID)
            .setSmallIcon(R.drawable.ic_launcher_foreground)
            .setContentTitle("Activity recording")
            .build()
    }

    private fun pauseRecording() {
        recordingJob?.cancel()
        recordingJob = null
    }

    private fun resumeRecording(intent: Intent) {
        val startedAt = intent.getStartedTimestamp() ?: return
        recordingJob = recordActivity(startedAt).launchIn(scope)
    }

    private fun stopRecording() {
        recordingJob?.cancel()
        recordingJob = null
        stopForeground(STOP_FOREGROUND_REMOVE)
        stopSelf()
    }

    companion object {

        const val START_ACTON = "start_action"
        const val PAUSE_ACTION = "pause_action"
        const val RESUME_ACTION = "resume_action"
        const val STOP_ACTION = "stop_action"
        const val START_TIMESTAMP_KEY = "start-key"
        private const val CHANNEL_ID = "activity-notification"
        private const val CHANNEL_NAME = "Location tracking"
        private const val NOTIFICATION_ID = 1
    }
}
