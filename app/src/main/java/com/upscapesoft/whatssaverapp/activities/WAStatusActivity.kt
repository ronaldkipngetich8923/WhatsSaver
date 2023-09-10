package com.upscapesoft.whatssaverapp.activities

import android.Manifest
import android.annotation.SuppressLint
import android.app.Activity
import android.content.ActivityNotFoundException
import android.content.Intent
import android.content.SharedPreferences
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Color
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.viewpager.widget.ViewPager
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.tabs.TabLayout
import com.hbisoft.pickit.PickiT
import com.hbisoft.pickit.PickiTCallbacks
import com.rilixtech.widget.countrycodepicker.CountryCodePicker
import com.tapadoo.alerter.Alerter
import com.upscapesoft.whatssaverapp.R
import com.upscapesoft.whatssaverapp.adapters.FragmentAdapter
import com.upscapesoft.whatssaverapp.data.Constants
import com.upscapesoft.whatssaverapp.fragments.WAPhotosFragment
import com.upscapesoft.whatssaverapp.fragments.WAVideosFragment
import com.upscapesoft.whatssaverapp.helper.AdController
import com.upscapesoft.whatssaverapp.utils.Utils
import java.io.IOException
import java.io.UnsupportedEncodingException
import java.net.URLEncoder

class WAStatusActivity : AppCompatActivity(), PickiTCallbacks {

    private var viewPager: ViewPager? = null
    private var rl_main: RelativeLayout? = null
    private var pickiT: PickiT? = null
    lateinit var tabs: Array<String?>
    private var tabLayout: TabLayout? = null

    private var container: LinearLayout? = null
    private var backBtn: ImageView? = null
    private var directChatBtn: ImageView? = null

    private var bottomSheetDialog: BottomSheetDialog? = null
    private var btn_send: Button? = null
    private var ccp: CountryCodePicker? = null
    private var edt_message: EditText? = null
    private var edt_phonenumber: EditText? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_wastatus)
        activity = this

        iniApp()
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            val sharedPreferences = getSharedPreferences(packageName, 0)
            if (sharedPreferences.getString("pathW", "")!!.length != 0) {
                uri = Uri.parse(sharedPreferences.getString("pathW", ""))
                loadFragments()
            } else {
                startActivity(Intent(this@WAStatusActivity, WAPermissionsActivity::class.java))
            }
        } else {
            if (ActivityCompat.checkSelfPermission(
                    this@WAStatusActivity,
                    Manifest.permission.WRITE_EXTERNAL_STORAGE
                ) == PackageManager.PERMISSION_GRANTED
            ) {
                loadFragments()
            } else {
                startActivity(Intent(this@WAStatusActivity, WAPermissionsActivity::class.java))
            }
        }

        val shouldShowDialog = intent.getBooleanExtra("shouldShowDialog", false)
        if (shouldShowDialog) {
            displayDeleteAlerter(true)
        }

        /*Admob*/
        container = findViewById(R.id.banner_container)
        AdController.loadBannerAd(this, container)
        AdController.loadInterAd(this)
        backBtn = findViewById(R.id.backBtn)
        backBtn!!.setOnClickListener {
            onBackPressed()
        }

        directChatBtn = findViewById(R.id.directChatBtn)
        directChatBtn!!.setOnClickListener {
            directChatBottomSheetDialog()
        }

    }

    private fun iniApp() {
        pickiT = PickiT(this, this)
        filePathsPhotos = ArrayList()
        filePathsVideos = ArrayList()
        filePathsSaved = ArrayList()
        filePathsPhotosChecked = ArrayList()
        filePathsVideosChecked = ArrayList()
        tabLayout = findViewById(R.id.tabs)
        rl_main = findViewById(R.id.rl_main)
        viewPager = findViewById(R.id.viewpager_fragments)
        sharedPreferences = getSharedPreferences(packageName, 0)
    }

    private fun loadFragments() {
        setupViewPager(viewPager)

        tabs = arrayOfNulls(2)
        tabs[0] = resources.getString(R.string.photos)
        tabs[1] = resources.getString(R.string.videos)
        tabLayout!!.setupWithViewPager(viewPager)
        for (i in 0 until tabLayout!!.tabCount) {
            val tab = tabLayout!!.getTabAt(i)
            tab!!.customView = getTabViewUn(i)
        }
        setupTabIcons()
        tabLayout!!.addOnTabSelectedListener(object : TabLayout.OnTabSelectedListener {
            override fun onTabSelected(tab: TabLayout.Tab) {
                viewPager!!.currentItem = tab.position
                val tabs = tabLayout!!.getTabAt(tab.position)
                tabs!!.customView = null
                tabs.customView = getTabView(tab.position)

                AdController.adCounter++
                AdController.showInterAd(this@WAStatusActivity, null, 0)
            }

            override fun onTabUnselected(tab: TabLayout.Tab) {
                val tabs = tabLayout!!.getTabAt(tab.position)
                tabs!!.customView = null
                tabs.customView = getTabViewUn(tab.position)
            }

            override fun onTabReselected(tab: TabLayout.Tab) {}
        })
    }

    private fun setupViewPager(viewPager: ViewPager?) {
        val adapter = FragmentAdapter(
            supportFragmentManager
        )
        adapter.addFragment(WAPhotosFragment(), "Photos")
        adapter.addFragment(WAVideosFragment(), "Videos")
        viewPager!!.adapter = adapter
    }

    override fun PickiTonStartListener() {}
    override fun PickiTonProgressUpdate(progress: Int) {}
    override fun PickiTonCompleteListener(
        path: String,
        wasDriveFile: Boolean,
        wasUnknownProvider: Boolean,
        wasSuccessful: Boolean,
        Reason: String
    ) {
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

    @SuppressLint("InflateParams")
    fun getTabViewUn(pos: Int): View {
        val v = LayoutInflater.from(this@WAStatusActivity).inflate(R.layout.custom_tab_lay, null)
        val txt = v.findViewById<TextView>(R.id.tab)
        txt.text = tabs[pos]
        txt.setTextColor(resources.getColor(R.color.tab_txt_unpressed))
        txt.setBackgroundResource(R.drawable.btn_unpressed_state)
        val tab = FrameLayout.LayoutParams(
            resources.displayMetrics.widthPixels * 438 / 1080,
            resources.displayMetrics.heightPixels * 140 / 1920
        )
        txt.layoutParams = tab
        return v
    }

    @SuppressLint("InflateParams")
    private fun setupTabIcons() {
        val v = LayoutInflater.from(this).inflate(R.layout.custom_tab_lay, null)
        val txt = v.findViewById<TextView>(R.id.tab)
        txt.text = tabs[0]
        txt.setTextColor(resources.getColor(R.color.tab_txt_pressed))
        txt.setBackgroundResource(R.drawable.btn_pressed_state)
        val tabp = FrameLayout.LayoutParams(
            resources.displayMetrics.widthPixels * 438 / 1080,
            resources.displayMetrics.heightPixels * 140 / 1920
        )
        txt.layoutParams = tabp
        val tab = tabLayout!!.getTabAt(0)
        tab!!.customView = null
        tab.customView = v
    }

    @SuppressLint("InflateParams")
    fun getTabView(pos: Int): View {
        val v = LayoutInflater.from(this@WAStatusActivity).inflate(R.layout.custom_tab_lay, null)
        val txt = v.findViewById<TextView>(R.id.tab)
        txt.text = tabs[pos]
        txt.setTextColor(resources.getColor(R.color.tab_txt_pressed))
        txt.setBackgroundResource(R.drawable.btn_pressed_state)
        val tab = FrameLayout.LayoutParams(
            resources.displayMetrics.widthPixels * 438 / 1080,
            resources.displayMetrics.heightPixels * 140 / 1920
        )
        txt.layoutParams = tab
        return v
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<String?>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == Constants.REQ_CODE_EXTERNAL_STORAGE_PERMISSION && grantResults.size > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            Utils.startAppWhatsapp(activity!!)
        }
    }

    private fun directChatBottomSheetDialog() {
        bottomSheetDialog = BottomSheetDialog(this, R.style.CustomBottomSheetDialogTheme)
        bottomSheetDialog!!.window!!.decorView.setBackgroundColor(Color.TRANSPARENT)
        bottomSheetDialog!!.setContentView(R.layout.direct_chat_bottom_sheet_dialog_lay)

        btn_send = bottomSheetDialog!!.findViewById<View>(R.id.btn_send) as Button
        edt_message = bottomSheetDialog!!.findViewById<View>(R.id.edt_message) as EditText
        edt_phonenumber = bottomSheetDialog!!.findViewById<View>(R.id.etphonenumber) as EditText
        ccp = bottomSheetDialog!!.findViewById<View>(R.id.ccp) as CountryCodePicker
        btn_send!!.setOnClickListener {
            openWhatsApp()
        }

        bottomSheetDialog!!.show()

    }

    private fun openWhatsApp() {
        val str: String =
            ccp!!.selectedCountryCode.toString() + " " + edt_phonenumber!!.text.toString()
        if (whatsappInstalledOrNot("com.whatsapp")) {
            val mIntent = Intent(
                "android.intent.action.VIEW", Uri.parse(
                    "whatsapp://send/?text=" + URLEncoder.encode(
                        edt_message!!.text.toString(), "UTF-8"
                    ).toString() + "&phone=" + str
                )
            )
            mIntent.setPackage("com.whatsapp")
            try {
                startActivity(
                    mIntent
                )
            } catch (unused: ActivityNotFoundException) {
                Toast.makeText(applicationContext, "Please install WhatsApp", Toast.LENGTH_LONG).show()
            } catch (unused2: UnsupportedEncodingException) {
                Toast.makeText(applicationContext, "Error while encoding your text message", Toast.LENGTH_LONG)
                    .show()
            }
        } else {
            val intent =
                Intent("android.intent.action.VIEW", Uri.parse("https://play.google.com/store/apps/details?id=com.whatsapp&hl"))
            Toast.makeText(applicationContext, "WhatsApp not Installed", Toast.LENGTH_SHORT).show()
            startActivity(intent)
        }
    }

    @SuppressLint("WrongConstant")
    private fun whatsappInstalledOrNot(str: String): Boolean {
        return try {
            packageManager.getPackageInfo(str, 1)
            true
        } catch (unused: PackageManager.NameNotFoundException) {
            false
        }
    }

    companion object {
        @SuppressLint("StaticFieldLeak")
        private var activity: Activity? = null
        @JvmField
        var filePathsPhotos: ArrayList<String>? = null
        var filePathsSaved: ArrayList<String>? = null
        @JvmField
        var filePathsVideos: ArrayList<String>? = null
        @JvmField
        var filePathsPhotosChecked: ArrayList<String>? = null
        @JvmField
        var filePathsVideosChecked: ArrayList<String>? = null
        @JvmField
        var statusMode = 0
        @JvmField
        var uri: Uri? = null
        @JvmField
        var sharedPreferences: SharedPreferences? = null

       fun displayDeleteAlerter(hasDeleted: Boolean) {
            if (hasDeleted) {
                Alerter.create(activity!!)
                    .setTitle(R.string.deleted_s)
                    .setText(R.string.delete_s_long)
                    .setBackgroundColorRes(R.color.tint_color)
                    .show()
            } else {
                Alerter.create(activity!!)
                    .setTitle(R.string.error)
                    .setText(R.string.error_long_delete)
                    .setBackgroundColorRes(R.color.tint_color)
                    .show()
            }
       }

    }

}