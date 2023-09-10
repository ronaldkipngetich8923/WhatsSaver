package com.upscapesoft.whatssaverapp.activities

import android.app.Activity
import android.os.Bundle
import android.widget.ImageView
import android.widget.LinearLayout
import androidx.appcompat.app.AppCompatActivity
import com.upscapesoft.whatssaverapp.R
import com.upscapesoft.whatssaverapp.helper.AdController

class HowToUseActivity : AppCompatActivity() {

    private var activity: Activity? = null

    private var backBtn: ImageView? = null
    private var container: LinearLayout? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_how_to_use)
        activity = this

        initViews()
        contentViews()

    }

    private fun initViews() {
        backBtn = findViewById(R.id.backBtn)
    }

    private fun contentViews() {
        /*admob*/
        container = findViewById(R.id.banner_container)
        AdController.loadBannerAd(this@HowToUseActivity, container)
        AdController.loadInterAd(this@HowToUseActivity)

        backBtn!!.setOnClickListener {
            onBackPressed()
        }

    }

    override fun onBackPressed() {
        AdController.adCounter++
        if (AdController.adCounter == AdController.adDisplayCounter) {
            AdController.showInterAd(this@HowToUseActivity, null, 0)
        } else {
            super.onBackPressed()
        }
    }

}