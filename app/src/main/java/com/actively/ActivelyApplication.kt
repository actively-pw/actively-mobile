package com.actively

import android.app.Application

class ActivelyApplication: Application() {

    override fun onCreate() {
        super.onCreate()
        KoinSetup.initKoin(this)
    }
}
