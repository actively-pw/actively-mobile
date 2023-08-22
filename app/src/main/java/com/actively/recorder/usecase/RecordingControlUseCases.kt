package com.actively.recorder.usecase

data class RecordingControlUseCases(
    val startRecording: StartRecordingUseCase,
    val pauseRecording: PauseRecordingUseCase,
    val resumeRecording: ResumeRecordingUseCase,
    val stopRecording: StopRecordingUseCase,
)
