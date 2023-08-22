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
import com.actively.recorder.usecase.SetRecorderStateUseCase
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.cancel
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.launch
import kotlinx.datetime.Instant
import org.koin.android.ext.android.inject

class RecordActivityService : Service() {

    private val notificationManager by lazy { getSystemService(NotificationManager::class.java) }
    private val recordActivityUseCase by inject<RecordActivityUseCase>()
    private val setRecorderStateUseCase by inject<SetRecorderStateUseCase>()
    private val recorderStateMachine by inject<RecorderStateMachine>()
    private val scope = CoroutineScope(SupervisorJob())
    private var recordingJob: Job? = null

    override fun onBind(intent: Intent?): IBinder? {
        return null
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        when (intent?.action) {
            START_ACTON -> recorderStateMachine.transitionTo(RecorderState.Started) {
                setRecorderState(RecorderState.Started)
                startRecording(intent)
            }

            PAUSE_ACTION -> recorderStateMachine.transitionTo(RecorderState.Paused) {
                setRecorderState(RecorderState.Paused)
                pauseRecording()
            }

            RESUME_ACTION -> recorderStateMachine.transitionTo(RecorderState.Started) {
                setRecorderState(RecorderState.Started)
                resumeRecording(intent)
            }

            STOP_ACTION -> recorderStateMachine.transitionTo(RecorderState.Stopped) {
                setRecorderState(RecorderState.Stopped)
                stopRecording()
            }
        }
        return super.onStartCommand(intent, flags, startId)
    }

    override fun onDestroy() {
        super.onDestroy()
        scope.cancel()
    }

    private fun startRecording(intent: Intent) {
        val startedAt = intent.getTimestamp()
            ?: error("No timestamp was provided with intent")
        val notification = buildNotification()
        startForeground(NOTIFICATION_ID, notification)
        recordingJob = recordActivityUseCase(startedAt).launchIn(scope)
    }

    private fun pauseRecording() {
        recordingJob?.cancel()
        recordingJob = null
    }

    private fun resumeRecording(intent: Intent) {
        val resumedAt = intent.getTimestamp()
            ?: error("No timestamp was provided with intent")
        recordingJob = recordActivityUseCase(resumedAt).launchIn(scope)
    }

    private fun stopRecording() {
        pauseRecording()
        stopForeground(STOP_FOREGROUND_REMOVE)
        stopSelf()
    }

    private fun Intent.getTimestamp() = extras?.getString(TIMESTAMP_KEY)
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

    private fun setRecorderState(state: RecorderState) = scope.launch {
        setRecorderStateUseCase(state)
    }

    companion object {

        const val START_ACTON = "start_action"
        const val PAUSE_ACTION = "pause_action"
        const val RESUME_ACTION = "resume_action"
        const val STOP_ACTION = "stop_action"
        const val TIMESTAMP_KEY = "start-key"
        private const val CHANNEL_ID = "activity-notification"
        private const val CHANNEL_NAME = "Location tracking"
        private const val NOTIFICATION_ID = 1
    }
}
