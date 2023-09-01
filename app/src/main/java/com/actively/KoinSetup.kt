package com.actively

import android.content.Context
import app.cash.sqldelight.db.SqlDriver
import app.cash.sqldelight.driver.android.AndroidSqliteDriver
import com.actively.activity.usecase.CreateActivityUseCase
import com.actively.activity.usecase.CreateActivityUseCaseImpl
import com.actively.datasource.ActivityRecordingDataSource
import com.actively.datasource.ActivityRecordingDataSourceImpl
import com.actively.location.LocationProvider
import com.actively.location.LocationProviderImpl
import com.actively.recorder.RecorderStateMachine
import com.actively.recorder.RecorderStateMachineImpl
import com.actively.recorder.ui.RecorderViewModel
import com.actively.recorder.usecase.GetRecorderStateUseCase
import com.actively.recorder.usecase.GetRecorderStateUseCaseImpl
import com.actively.recorder.usecase.GetStatsUseCase
import com.actively.recorder.usecase.PauseRecordingUseCase
import com.actively.recorder.usecase.PauseRecordingUseCaseImpl
import com.actively.recorder.usecase.RecordActivityUseCase
import com.actively.recorder.usecase.RecordActivityUseCaseImpl
import com.actively.recorder.usecase.RecordingControlUseCases
import com.actively.recorder.usecase.ResumeRecordingUseCase
import com.actively.recorder.usecase.ResumeRecordingUseCaseImpl
import com.actively.recorder.usecase.SetRecorderStateUseCase
import com.actively.recorder.usecase.SetRecorderStateUseCaseImpl
import com.actively.recorder.usecase.StartRecordingUseCase
import com.actively.recorder.usecase.StartRecordingUseCaseImpl
import com.actively.recorder.usecase.StopRecordingUseCase
import com.actively.recorder.usecase.StopRecordingUseCaseImpl
import com.actively.repository.ActivityRecordingRepository
import com.actively.repository.ActivityRecordingRepositoryImpl
import com.actively.util.TimeProvider
import com.actively.util.UUIDProvider
import com.actively.util.UUIDProviderImpl
import com.mapbox.common.location.compat.LocationEngineProvider
import kotlinx.datetime.Clock
import org.koin.android.ext.koin.androidContext
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.core.context.startKoin
import org.koin.dsl.module

object KoinSetup {

    fun initKoin(context: Context) = startKoin {
        androidContext(context)
        modules(commonModule, useCasesModule, viewModelModule)
    }

    private val viewModelModule = module {
        viewModel { RecorderViewModel(get(), get(), get(), get()) }
    }

    private val useCasesModule = module {
        factory<RecordActivityUseCase> { RecordActivityUseCaseImpl(get(), get()) }
        factory { RecordingControlUseCases(get(), get(), get(), get()) }
        factory<StartRecordingUseCase> {
            StartRecordingUseCaseImpl(get(), get(), androidContext())
        }
        factory<ResumeRecordingUseCase> {
            ResumeRecordingUseCaseImpl(get(), androidContext())
        }
        factory<PauseRecordingUseCase> { PauseRecordingUseCaseImpl(androidContext()) }
        factory<StopRecordingUseCase> { StopRecordingUseCaseImpl(androidContext()) }
        factory<CreateActivityUseCase> { CreateActivityUseCaseImpl(get()) }
        factory<SetRecorderStateUseCase> { SetRecorderStateUseCaseImpl(get()) }
        factory<GetRecorderStateUseCase> { GetRecorderStateUseCaseImpl(get()) }
        factory { GetStatsUseCase(get()) }
    }

    private val commonModule = module {
        single { LocationEngineProvider.getBestLocationEngine(androidContext()) }
        single<LocationProvider> { LocationProviderImpl(get()) }
        single<SqlDriver> { AndroidSqliteDriver(ActivityDatabase.Schema, androidContext()) }
        single<ActivityDatabase> { ActivityDatabase(get()) }
        single<ActivityRecordingDataSource> { ActivityRecordingDataSourceImpl(get()) }
        single<ActivityRecordingRepository> { ActivityRecordingRepositoryImpl(get()) }
        single<TimeProvider> { TimeProvider(Clock.System::now) }
        single<UUIDProvider> { UUIDProviderImpl() }
        single<RecorderStateMachine> { RecorderStateMachineImpl() }
    }
}
