package com.upscapesoft.whatssaverapp.activities

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.RelativeLayout
import androidx.appcompat.app.AppCompatActivity
import com.upscapesoft.whatssaverapp.R
import com.upscapesoft.whatssaverapp.helper.AdController
import com.upscapesoft.whatssaverapp.helper.SharedPrefs
import com.upscapesoft.whatssaverapp.utils.Utils

class LanguageActivity : AppCompatActivity() {

    private var activity: Activity? = null

    private var backBtn: ImageView? = null
    private var container: LinearLayout? = null

    private var englishLangBtn: RelativeLayout? = null
    private var hindiLangBtn: RelativeLayout? = null
    private var spanishLangBtn: RelativeLayout? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        Utils.setLanguage(this@LanguageActivity, SharedPrefs.getLang(this@LanguageActivity))
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_language)
        activity = this

        initViews()
        contentViews()

    }

    private fun initViews() {
        backBtn = findViewById(R.id.backBtn)
        container = findViewById(R.id.banner_container)

        englishLangBtn = findViewById(R.id.englishLangBtn)
        hindiLangBtn = findViewById(R.id.hindiLangBtn)
        spanishLangBtn = findViewById(R.id.spanishLangBtn)
    }

    private fun contentViews() {
        /*admob*/
        AdController.loadBannerAd(activity, container!!)
        AdController.loadInterAd(activity)

        backBtn!!.setOnClickListener {
            onBackPressed()
        }

        englishLangBtn!!.setOnClickListener {
            SharedPrefs.setLang(activity, "en")
            refresh()
        }

        hindiLangBtn!!.setOnClickListener {
            SharedPrefs.setLang(activity, "hi")
            refresh()
        }

        spanishLangBtn!!.setOnClickListener {
            SharedPrefs.setLang(activity, "es")
            refresh()
        }

    }

    private fun refresh() {
        finish()
        val intent = Intent(activity, MainActivity::class.java)
        overridePendingTransition(0,0)
        startActivity(intent)
        overridePendingTransition(0,0)
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