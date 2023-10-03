package com.actively

import android.content.Context
import androidx.work.WorkManager
import app.cash.sqldelight.db.SqlDriver
import app.cash.sqldelight.driver.android.AndroidSqliteDriver
import com.actively.activity.usecase.CreateActivityUseCase
import com.actively.activity.usecase.CreateActivityUseCaseImpl
import com.actively.datasource.ActivityRecordingDataSource
import com.actively.datasource.ActivityRecordingDataSourceImpl
import com.actively.datasource.RecordedActivitiesDataSourceFactory
import com.actively.datasource.RecordedActivitiesDataSourceFactoryImpl
import com.actively.datasource.SyncActivitiesDataSource
import com.actively.datasource.SyncActivitiesDataSourceImpl
import com.actively.home.ui.HomeViewModel
import com.actively.location.LocationProvider
import com.actively.location.LocationProviderImpl
import com.actively.recorder.RecorderStateMachine
import com.actively.recorder.RecorderStateMachineImpl
import com.actively.recorder.ui.RecorderViewModel
import com.actively.recorder.ui.SaveActivityViewModel
import com.actively.recorder.usecase.DiscardActivityUseCase
import com.actively.recorder.usecase.DiscardActivityUseCaseImpl
import com.actively.recorder.usecase.GetRecorderStateUseCase
import com.actively.recorder.usecase.GetRecorderStateUseCaseImpl
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
import com.actively.synchronizer.usecases.GetSyncStateUseCase
import com.actively.synchronizer.usecases.GetSyncStateUseCaseImpl
import com.actively.synchronizer.usecases.LaunchSynchronizationUseCase
import com.actively.synchronizer.usecases.LaunchSynchronizationUseCaseImpl
import com.actively.synchronizer.usecases.SendActivityUseCase
import com.actively.synchronizer.usecases.SendActivityUseCaseImpl
import com.actively.synchronizer.usecases.SynchronizeActivitiesUseCase
import com.actively.synchronizer.usecases.SynchronizeActivitiesUseCaseImpl
import com.actively.util.TimeProvider
import com.actively.util.UUIDProvider
import com.actively.util.UUIDProviderImpl
import com.mapbox.common.location.compat.LocationEngineProvider
import io.ktor.client.HttpClient
import io.ktor.client.engine.android.Android
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.client.plugins.logging.ANDROID
import io.ktor.client.plugins.logging.LogLevel
import io.ktor.client.plugins.logging.Logger
import io.ktor.client.plugins.logging.Logging
import io.ktor.serialization.kotlinx.json.json
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.datetime.Clock
import kotlinx.serialization.json.Json
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
        viewModel { RecorderViewModel(get(), get(), get()) }
        viewModel { SaveActivityViewModel(get(), get(), get()) }
        viewModel { HomeViewModel(get(), get()) }
    }

    private val useCasesModule = module {
        factory<RecordActivityUseCase> { RecordActivityUseCaseImpl(get(), get(), get()) }
        factory { RecordingControlUseCases(get(), get(), get()) }
        factory<StartRecordingUseCase> {
            StartRecordingUseCaseImpl(get(), get(), androidContext())
        }
        factory<ResumeRecordingUseCase> {
            ResumeRecordingUseCaseImpl(get(), androidContext())
        }
        factory<PauseRecordingUseCase> { PauseRecordingUseCaseImpl(androidContext()) }
        factory<StopRecordingUseCase> { StopRecordingUseCaseImpl(get(), get(), androidContext()) }
        factory<CreateActivityUseCase> { CreateActivityUseCaseImpl(get()) }
        factory<SetRecorderStateUseCase> { SetRecorderStateUseCaseImpl(get()) }
        factory<GetRecorderStateUseCase> { GetRecorderStateUseCaseImpl(get()) }
        factory<SynchronizeActivitiesUseCase> { SynchronizeActivitiesUseCaseImpl(get(), get()) }
        factory<LaunchSynchronizationUseCase> { LaunchSynchronizationUseCaseImpl(get()) }
        factory<SendActivityUseCase> { SendActivityUseCaseImpl(get()) }
        factory<DiscardActivityUseCase> { DiscardActivityUseCaseImpl(get()) }
        factory<GetSyncStateUseCase> { GetSyncStateUseCaseImpl(get()) }
    }

    private val commonModule = module {
        single { LocationEngineProvider.getBestLocationEngine(androidContext()) }
        single<LocationProvider> { LocationProviderImpl(get()) }
        single<SqlDriver> {
            AndroidSqliteDriver(
                ActivityDatabase.Schema,
                androidContext(),
                "recording_database.db"
            )
        }
        single {
            HttpClient(Android) {
                install(ContentNegotiation) {
                    json(Json {
                        isLenient = true
                        prettyPrint = true
                        ignoreUnknownKeys = true
                    })
                }
                install(Logging) {
                    logger = Logger.ANDROID
                    level = LogLevel.ALL
                }
                expectSuccess = true
            }
        }
        single { WorkManager.getInstance(androidContext()) }
        single<ActivityDatabase> { ActivityDatabase(get()) }
        single<ActivityRecordingDataSource> { ActivityRecordingDataSourceImpl(get()) }
        single<SyncActivitiesDataSource> { SyncActivitiesDataSourceImpl(get()) }
        single<ActivityRecordingRepository> { ActivityRecordingRepositoryImpl(get(), get()) }
        single<TimeProvider> { TimeProvider(Clock.System::now) }
        single<UUIDProvider> { UUIDProviderImpl() }
        factory<RecorderStateMachine> { RecorderStateMachineImpl() }
        single<RecordedActivitiesDataSourceFactory> { RecordedActivitiesDataSourceFactoryImpl(get()) }
        single<CoroutineScope> { CoroutineScope(SupervisorJob() + Dispatchers.Main) }
    }
}
