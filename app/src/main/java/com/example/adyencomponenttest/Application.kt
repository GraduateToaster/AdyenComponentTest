package com.example.adyencomponenttest

import android.app.Application
import com.adyen.checkout.core.AdyenLogLevel
import com.adyen.checkout.core.AdyenLogger

class Application : Application() {

    override fun onCreate() {
        super.onCreate()
        AdyenLogger.setLogLevel(AdyenLogLevel.VERBOSE)
    }
}