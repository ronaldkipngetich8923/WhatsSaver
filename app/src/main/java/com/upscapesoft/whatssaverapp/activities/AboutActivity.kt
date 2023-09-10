package com.upscapesoft.whatssaverapp.activities

import android.app.Activity
import android.os.Bundle
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.upscapesoft.whatssaverapp.BuildConfig
import com.upscapesoft.whatssaverapp.R
import com.upscapesoft.whatssaverapp.helper.AdController

class AboutActivity : AppCompatActivity() {

    private lateinit var activity: Activity

    private var backBtn: ImageView? = null
    private var versionName: TextView? = null
    private var container: LinearLayout? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_about)
        activity = this

        initViews()
        contentViews()
    }

    private fun initViews() {
        backBtn = findViewById(R.id.backBtn)
        container = findViewById(R.id.banner_container)
        versionName = findViewById(R.id.versionName)
    }

    private fun contentViews() {
        /*admob*/
        AdController.loadBannerAd(activity, container!!)
        AdController.loadInterAd(activity)

        backBtn!!.setOnClickListener {
            onBackPressed()
        }

        versionName!!.text = BuildConfig.VERSION_NAME

    }

    override fun onBackPressed() {
        AdController.adCounter++
        if (AdController.adCounter == AdController.adDisplayCounter) {
            AdController.showInterAd(activity, null, 0)
        } else {
            super.onBackPressed()
        }
    }

}