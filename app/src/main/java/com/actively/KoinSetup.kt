package com.actively

import android.content.Context
import org.koin.android.ext.koin.androidContext
import org.koin.core.context.startKoin

object KoinSetup {

    fun initKoin(context: Context) = startKoin {
        androidContext(context)
    }
}
