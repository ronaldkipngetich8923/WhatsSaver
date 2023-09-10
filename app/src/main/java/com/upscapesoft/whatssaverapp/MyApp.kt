package com.upscapesoft.whatssaverapp

import android.app.Application
import com.onesignal.OneSignal
import com.upscapesoft.whatssaverapp.utils.ThemeSettings
import com.upscapesoft.whatssaverapp.data.Constants
import com.upscapesoft.whatssaverapp.helper.AdController
import com.upscapesoft.whatssaverapp.helper.AppOpenManager

class MyApp : Application() {

    var appOpenManager: AppOpenManager? = null

    override fun onCreate() {
        super.onCreate()

        ThemeSettings.getInstance(this)!!.refreshTheme()

        // Enable verbose OneSignal logging to debug issues if needed.
        OneSignal.setLogLevel(OneSignal.LOG_LEVEL.VERBOSE, OneSignal.LOG_LEVEL.NONE)

        // OneSignal Initialization
        OneSignal.initWithContext(this)
        OneSignal.setAppId(Constants.ONESIGNAL_APP_ID)
        AdController.initAd(this)
        appOpenManager = AppOpenManager(this)

    }

}