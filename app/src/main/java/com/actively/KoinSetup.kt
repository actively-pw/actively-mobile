package com.actively

import android.content.Context
import com.actively.location.LocationProvider
import com.actively.location.LocationProviderImpl
import com.actively.location.usecase.GetUserLocationUpdatesUseCase
import com.actively.location.usecase.GetUserLocationUpdatesUseCaseImpl
import com.mapbox.common.location.compat.LocationEngineProvider
import org.koin.android.ext.koin.androidContext
import org.koin.core.context.startKoin
import org.koin.dsl.module

object KoinSetup {

    fun initKoin(context: Context) = startKoin {
        androidContext(context)
        modules(commonModule, useCasesModule)
    }

    private val useCasesModule = module {
        factory<GetUserLocationUpdatesUseCase> { GetUserLocationUpdatesUseCaseImpl(get()) }
    }

    private val commonModule = module {
        single { LocationEngineProvider.getBestLocationEngine(androidContext()) }
        single<LocationProvider> { LocationProviderImpl(get()) }
    }
}
