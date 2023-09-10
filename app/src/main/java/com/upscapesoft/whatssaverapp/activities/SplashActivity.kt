package com.upscapesoft.whatssaverapp.activities

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.airbnb.lottie.LottieAnimationView
import com.upscapesoft.whatssaverapp.R
import com.upscapesoft.whatssaverapp.data.Constants
import com.upscapesoft.whatssaverapp.helper.SharedPrefs
import com.upscapesoft.whatssaverapp.utils.Utils

@SuppressLint("CustomSplashScreen")
class SplashActivity : AppCompatActivity() {

    private var activity: Activity? = null
    private var appNameSplash: TextView? = null
    private var appLoader: LottieAnimationView? = null
    private var companyName: TextView? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        Utils.setLanguage(this@SplashActivity, SharedPrefs.getLang(this@SplashActivity))
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash)
        activity = this

        goToMainScreen()

        appNameSplash = findViewById(R.id.appNameSplash)
        appLoader = findViewById(R.id.appLoader)
        companyName = findViewById(R.id.companyName)

        appNameSplash!!.alpha = 0f
        appNameSplash!!.animate()
            .translationY(appNameSplash!!.height.toFloat())
            .alpha(1f)
            .setDuration(1000)
            .setStartDelay(1000)
            .translationY(appNameSplash!!.height.toFloat())
            .alpha(1f)
            .setDuration(1200).startDelay = 1500

        appLoader!!.alpha = 0f
        appLoader!!.animate()
            .translationY(appLoader!!.height.toFloat())
            .alpha(1f)
            .setDuration(1000)
            .setStartDelay(1000)
            .translationY(appLoader!!.height.toFloat())
            .alpha(1f)
            .setDuration(1200).startDelay = 1500

        companyName!!.alpha = 0f
        companyName!!.animate()
            .translationY(companyName!!.height.toFloat())
            .alpha(1f)
            .setDuration(1000)
            .setStartDelay(1000)
            .translationY(companyName!!.height.toFloat())
            .alpha(1f)
            .setDuration(1200).startDelay = 1500

    }

    private fun goToMainScreen() {
        Handler().postDelayed({
            val intent = Intent(this@SplashActivity, MainActivity::class.java)
            startActivity(intent)
            finish()
        }, Constants.SPLASH_SCREEN_TIMEOUT.toLong())

    }

}