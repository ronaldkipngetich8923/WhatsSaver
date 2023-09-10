package com.upscapesoft.whatssaverapp.activities

import android.app.Activity
import android.os.Build
import android.os.Bundle
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.MediaController
import android.widget.VideoView
import androidx.appcompat.app.AppCompatActivity
import com.upscapesoft.whatssaverapp.R
import com.upscapesoft.whatssaverapp.helper.AdController
import com.upscapesoft.whatssaverapp.helper.SharedPrefs
import com.upscapesoft.whatssaverapp.utils.Utils

class VideoPlayerActivity : AppCompatActivity() {

    private var activity: Activity? = null
    private var backBtn: ImageView? = null
    private var previewVid: VideoView? = null
    private var container: LinearLayout? = null
    private var videoPath: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        Utils.setLanguage(this@VideoPlayerActivity, SharedPrefs.getLang(this@VideoPlayerActivity))

        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_video_player)
        activity = this

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            window.statusBarColor = resources.getColor(R.color.black, this.theme)
        } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            window.statusBarColor = resources.getColor(R.color.black)
        }

        backBtn = findViewById(R.id.backBtn)

        backBtn!!.setOnClickListener {
            onBackPressed()
        }

        videoPath = intent.getStringExtra("videosVid2")

        previewVid = findViewById(R.id.previewVid)
        previewVid!!.setVideoPath(videoPath)
        val mediaController = MediaController(this)
        mediaController.setAnchorView(previewVid)
        previewVid!!.setMediaController(mediaController)
        previewVid!!.start()

        previewVid!!.setOnCompletionListener {
            finish()
        }

        /*admob*/
        container = findViewById(R.id.banner_container)
        AdController.loadBannerAd(this@VideoPlayerActivity, container)
        AdController.loadInterAd(this@VideoPlayerActivity)

    }

    override fun onResume() {
        super.onResume()
        previewVid!!.setVideoPath(videoPath)
        previewVid!!.start()

        previewVid!!.setOnCompletionListener {
            finish()
        }
    }

    override fun onBackPressed() {
        super.onBackPressed()
        AdController.adCounter++
        if (AdController.adCounter == AdController.adDisplayCounter) {
            AdController.showInterAd(this@VideoPlayerActivity, null, 0)
        } else {
            super.onBackPressed()
        }
    }

}