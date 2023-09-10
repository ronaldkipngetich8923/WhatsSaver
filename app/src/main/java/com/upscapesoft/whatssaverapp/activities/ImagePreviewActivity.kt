package com.upscapesoft.whatssaverapp.activities

import android.app.Activity
import android.content.Intent
import android.content.SharedPreferences
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.view.View
import android.widget.ImageView
import android.widget.LinearLayout
import androidx.appcompat.app.AppCompatActivity
import androidx.core.net.toUri
import androidx.viewpager.widget.ViewPager
import com.nambimobile.widgets.efab.FabOption
import com.tapadoo.alerter.Alerter
import com.upscapesoft.whatssaverapp.R
import com.upscapesoft.whatssaverapp.adapters.PhotoPreviewAdapter
import com.upscapesoft.whatssaverapp.helper.AdController
import com.upscapesoft.whatssaverapp.helper.SharedPrefs
import com.upscapesoft.whatssaverapp.helper.StorageFunctions
import com.upscapesoft.whatssaverapp.utils.Utils
import java.io.File
import java.io.IOException

class ImagePreviewActivity : AppCompatActivity() {

    private var backBtn: ImageView? = null
    private var downloadBtn: FabOption? = null
    private var shareBtn: FabOption? = null
    private var repostBtn: FabOption? = null

    private var container: LinearLayout? = null
    private var activity: Activity? = null
    private var storageHelper: StorageFunctions? = null

    private var viewPager: ViewPager? = null
    private var photosPreviewAdapter: PhotoPreviewAdapter? = null
    private var editor: SharedPreferences.Editor? = null
    private var position = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        Utils.setLanguage(this@ImagePreviewActivity, SharedPrefs.getLang(this@ImagePreviewActivity))

        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_image_preview)
        activity = this

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            window.statusBarColor = resources.getColor(R.color.black, this.theme)
        } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            window.statusBarColor = resources.getColor(R.color.black)
        }

        backBtn = findViewById(R.id.backBtn)
        shareBtn = findViewById(R.id.shareBtn)
        downloadBtn = findViewById(R.id.downloadBtn)
        repostBtn = findViewById(R.id.repostBtn)

        storageHelper = StorageFunctions()
        if (SavedStatusActivity.sharedPreferences != null) {
            editor = SavedStatusActivity.sharedPreferences!!.edit()
        }

        position = intent.getIntExtra("position", 0)
        photosPreviewAdapter = PhotoPreviewAdapter(this@ImagePreviewActivity)
        viewPager = findViewById(R.id.viewPager)
        viewPager!!.adapter = photosPreviewAdapter
        viewPager!!.currentItem = position

        viewPager!!.addOnPageChangeListener(object : ViewPager.OnPageChangeListener {
            override fun onPageScrolled(
                position: Int,
                positionOffset: Float,
                positionOffsetPixels: Int
            ) {
            }

            override fun onPageSelected(position: Int) {
                AdController.adCounter++
                AdController.showInterAd(this@ImagePreviewActivity, null,0)

            }

            override fun onPageScrollStateChanged(state: Int) {}
        })

        downloadBtn!!.setOnClickListener(clickListener)
        shareBtn!!.setOnClickListener(clickListener)
        backBtn!!.setOnClickListener(clickListener)
        repostBtn!!.setOnClickListener(clickListener)

        /*admob*/
        container = findViewById(R.id.banner_container)
        AdController.loadBannerAd(this@ImagePreviewActivity, container)
        AdController.loadInterAd(this@ImagePreviewActivity)

    }

    private val clickListener = View.OnClickListener { v ->
        val shouldShowWhats = intent.getBooleanExtra("shouldShowWhats", false)
        val shouldShowWhatsB = intent.getBooleanExtra("shouldShowWhatsB", false)
        val shouldShowSaved = intent.getBooleanExtra("shouldShowSaved", false)

        when (v.id) {
            R.id.backBtn -> {
                onBackPressed()
            }

            R.id.downloadBtn -> {
                if (shouldShowWhats) {
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                        displaySaveAlerter(
                            storageHelper!!.savePhotoQ(
                                activity, getBitmapOnAndroidQ(
                                    Uri.parse(
                                        WAStatusActivity.filePathsPhotos!![viewPager!!.currentItem].toUri().toString()
                                    )
                                )
                            )
                        )
                    } else {
                        displaySaveAlerter(
                            storageHelper!!.save(
                                File(WAStatusActivity.filePathsPhotos!!.get(position)),
                                0,
                                activity
                            )
                        )
                    }
                } else if (shouldShowWhatsB) {
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                        displaySaveAlerter(
                            storageHelper!!.savePhotoQ(
                                activity, getBitmapOnAndroidQ(
                                    Uri.parse(
                                        WABStatusActivity.filePathsPhotos!![viewPager!!.currentItem].toUri().toString()
                                    )
                                )
                            )
                        )
                    } else {
                        displaySaveAlerter(
                            storageHelper!!.save(
                                File(WABStatusActivity.filePathsPhotos!!.get(position)),
                                0,
                                activity
                            )
                        )
                    }
                }

            }

            R.id.shareBtn -> {
                when {
                    shouldShowWhats -> {
                        val parse = Uri.parse(WAStatusActivity.filePathsPhotos!![viewPager!!.currentItem].toUri().toString())
                        shareMyFile(parse)
                    }
                    shouldShowWhatsB -> {
                        val parse = Uri.parse(WABStatusActivity.filePathsPhotos!![viewPager!!.currentItem].toUri().toString())
                        shareMyFile(parse)
                    }
                    shouldShowSaved -> {
                        val parse = Uri.parse(SavedStatusActivity.filePathsPhotos!![viewPager!!.currentItem].toUri().toString())
                        shareMyFile(parse)
                    }
                }

            }

            R.id.repostBtn -> {
                when {
                    shouldShowWhats -> {
                        val parse = Uri.parse(WAStatusActivity.filePathsPhotos!![viewPager!!.currentItem].toUri().toString())
                        repostFile(parse)
                    }
                    shouldShowWhatsB -> {
                        val parse = Uri.parse(WABStatusActivity.filePathsPhotos!![viewPager!!.currentItem].toUri().toString())
                        repostFile(parse)
                    }
                    shouldShowSaved -> {
                        val parse = Uri.parse(SavedStatusActivity.filePathsPhotos!![viewPager!!.currentItem].toUri().toString())
                        repostFile(parse)
                    }
                }

            }

        }

    }

    fun getBitmapOnAndroidQ(uri: Uri?): Bitmap? {
        var bitmap: Bitmap? = null
        try {
            val `is` = contentResolver.openInputStream(uri!!)
            bitmap = BitmapFactory.decodeStream(`is`)
            `is`!!.close()
        } catch (e: IOException) {
            e.printStackTrace()
        }
        return bitmap
    }

    private fun shareMyFile(uri: Uri?) {
        val mIntent = Intent("android.intent.action.SEND")
        mIntent.type = "image/*"
        mIntent.putExtra(
            "android.intent.extra.STREAM",
            uri)
        startActivity(Intent.createChooser(mIntent, resources.getString(R.string.share_via)))

    }

    private fun repostFile(uri: Uri?) {
        val mIntent = Intent("android.intent.action.SEND")
        mIntent.type = "image/*"
        mIntent.setPackage("com.whatsapp")
        mIntent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
        mIntent.putExtra(
            "android.intent.extra.STREAM",
            uri)
        startActivity(mIntent)
    }

    private fun displaySaveAlerter(hasSaved: Boolean) {
        if (hasSaved) {
            Alerter.create(this)
                .setTitle(R.string.saved_s)
                .setText(R.string.save_s_long)
                .setBackgroundColorRes(R.color.tint_color)
                .show()
        } else {
            Alerter.create(this)
                .setTitle(R.string.error)
                .setText(R.string.error_long)
                .setBackgroundColorRes(R.color.tint_color)
                .show()
        }

    }

    override fun onBackPressed() {
        super.onBackPressed()
        AdController.adCounter++
        if (AdController.adCounter == AdController.adDisplayCounter) {
            AdController.showInterAd(this@ImagePreviewActivity, null, 0)
        } else {
            super.onBackPressed()
        }

    }

}