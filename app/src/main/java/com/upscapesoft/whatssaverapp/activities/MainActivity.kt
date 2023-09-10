package com.upscapesoft.whatssaverapp.activities

import android.Manifest
import android.app.Activity
import android.content.ActivityNotFoundException
import android.content.Intent
import android.content.IntentSender.SendIntentException
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.provider.Settings
import android.view.View
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.Toast
import androidx.annotation.NonNull
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.app.AppCompatActivity
import androidx.cardview.widget.CardView
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.view.GravityCompat
import androidx.drawerlayout.widget.DrawerLayout
import com.example.awesomedialog.AwesomeDialog
import com.example.awesomedialog.background
import com.example.awesomedialog.body
import com.example.awesomedialog.icon
import com.example.awesomedialog.onNegative
import com.example.awesomedialog.onPositive
import com.example.awesomedialog.title
import com.google.android.material.navigation.NavigationView
import com.google.android.material.snackbar.Snackbar
import com.google.android.play.core.appupdate.AppUpdateInfo
import com.google.android.play.core.appupdate.AppUpdateManager
import com.google.android.play.core.appupdate.AppUpdateManagerFactory
import com.google.android.play.core.install.InstallState
import com.google.android.play.core.install.InstallStateUpdatedListener
import com.google.android.play.core.install.model.AppUpdateType
import com.google.android.play.core.install.model.InstallStatus
import com.google.android.play.core.install.model.UpdateAvailability
import com.google.android.play.core.tasks.Task
import com.infideap.drawerbehavior.Advance3DDrawerLayout
import com.upscapesoft.whatssaverapp.R
import com.upscapesoft.whatssaverapp.data.Constants
import com.upscapesoft.whatssaverapp.helper.AdController
import com.upscapesoft.whatssaverapp.helper.SharedPrefs
import com.upscapesoft.whatssaverapp.utils.ThemeSettings
import com.upscapesoft.whatssaverapp.utils.Utils
import com.vimalcvs.switchdn.DayNightSwitch
import com.vimalcvs.switchdn.DayNightSwitchAnimListener

class MainActivity : AppCompatActivity() {

    private var activity: Activity? = null

    private var backPressedTime: Long = 0
    private var container: LinearLayout? = null
    private var navigationView: NavigationView? = null
    private var drawer: Advance3DDrawerLayout? = null
    private var dayNightSwitch: DayNightSwitch? = null

    private var appUpdateManager: AppUpdateManager? = null
    private var installStateUpdatedListener: InstallStateUpdatedListener? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        Utils.setLanguage(this@MainActivity, SharedPrefs.getLang(this@MainActivity))
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        activity = this

        initViews()
        contentViews()

    }

    private fun initViews() {
        drawer = findViewById(R.id.drawer_layout)
        dayNightSwitch = findViewById(R.id.dayNightSwitch)
    }

    private fun contentViews() {
        if (!checkAndRequestPermissions()) {
            checkAndRequestPermissions()
        }

        appUpdateManager = AppUpdateManagerFactory.create(applicationContext)
        installStateUpdatedListener = InstallStateUpdatedListener { state: InstallState ->
            when {
                state.installStatus() == InstallStatus.DOWNLOADED -> {
                    popupSnackBarForCompleteUpdate()
                }
                state.installStatus() == InstallStatus.INSTALLED -> {
                    removeInstallStateUpdateListener()
                }
                else -> {
                    Toast.makeText(
                        applicationContext,
                        "InstallStateUpdatedListener: state: " + state.installStatus(),
                        Toast.LENGTH_LONG
                    ).show()
                }
            }
        }
        appUpdateManager!!.registerListener(installStateUpdatedListener!!)

        checkUpdate()

        /*Admob*/
        container = findViewById(R.id.banner_container)
        AdController.loadBannerAd(activity, container)

        drawer!!.setViewScale(GravityCompat.START, 0.9f)

        drawer!!.setViewElevation(
            GravityCompat.START,
            30f
        )

        drawer!!.setViewScrimColor(
            GravityCompat.START,
            ContextCompat.getColor(this, R.color.primary)
        )

        drawer!!.drawerElevation = 30f

        drawer!!.setRadius(GravityCompat.START, 25f)
        drawer!!.setViewRotation(GravityCompat.START, 0f)
        drawer!!.closeDrawer(GravityCompat.START)
        drawer!!.addDrawerListener(object : DrawerLayout.SimpleDrawerListener() {
            override fun onDrawerClosed(drawerView: View) {
                super.onDrawerClosed(drawerView)
            }
        })

        initDrawer()

        val actionBarDrawerToggle = ActionBarDrawerToggle(
            this,
            drawer,
            null,
            R.string.navigation_drawer_open,
            R.string.navigation_drawer_close
        )
        drawer!!.addDrawerListener(actionBarDrawerToggle)
        actionBarDrawerToggle.syncState()

        val navigationView2 = findViewById<View>(R.id.nav_view) as NavigationView
        navigationView = navigationView2

        findViewById<ImageView>(R.id.appMenuBtn).setOnClickListener {
            openDrawer()
        }

        findViewById<ImageView>(R.id.whatsappBtn).setOnClickListener {
            Utils.openWhatsapp(activity!!)
        }

        findViewById<ImageView>(R.id.whatsappBusinessBtn).setOnClickListener {
            Utils.openWhatsappBusiness(activity!!)
        }

        val pm = activity?.packageManager

        findViewById<CardView>(R.id.whatsappStatusBtn).setOnClickListener {
            try {
                if (Utils.isPackageInstalled("com.whatsapp", pm!!)) {
                    startActivity(Intent(this@MainActivity, WAStatusActivity::class.java))
                } else {
                    Toast.makeText(applicationContext, "Please install WhatsApp and view status!", Toast.LENGTH_LONG).show()
                    Utils.installWhatsAppDialog(activity!!)
                }

            } catch (e: ActivityNotFoundException) {
                Toast.makeText(applicationContext, "Please install WhatsApp and view status!", Toast.LENGTH_LONG).show()
                Utils.installWhatsAppDialog(activity!!)
            }
        }

        findViewById<CardView>(R.id.whatsappBusinessStatusBtn).setOnClickListener {
            try {
                if (Utils.isPackageInstalled("com.whatsapp.w4b", pm!!)) {
                    startActivity(Intent(this@MainActivity, WABStatusActivity::class.java))
                } else {
                    Toast.makeText(applicationContext, "Please install WhatsApp Business and view status!", Toast.LENGTH_LONG).show()
                    Utils.installWhatsAppBusinessDialog(activity!!)
                }

            } catch (e: ActivityNotFoundException) {
                Toast.makeText(applicationContext, "Please install WhatsApp Business and view status!", Toast.LENGTH_LONG).show()
                Utils.installWhatsAppBusinessDialog(activity!!)
            }
        }

        findViewById<CardView>(R.id.directChatBtn).setOnClickListener {
            startActivity(Intent(this@MainActivity, DirectChatActivity::class.java))
        }

        findViewById<CardView>(R.id.appLanguageBtn).setOnClickListener {
            startActivity(Intent(this@MainActivity, LanguageActivity::class.java))
        }

        findViewById<CardView>(R.id.howToUseBtn).setOnClickListener {
            startActivity(Intent(this@MainActivity, HowToUseActivity::class.java))
        }

        findViewById<CardView>(R.id.savedStatusBtn).setOnClickListener {
            startActivity(Intent(this@MainActivity, SavedStatusActivity::class.java))
        }

        lightDarkMode()

    }

    private fun initDrawer() {
        findViewById<View>(R.id.navHomeBtn).setOnClickListener { v: View? ->
            closeDrawer()
        }
        findViewById<View>(R.id.navInAppPurchasesBtn).setOnClickListener { v: View? ->
            closeDrawer()
            startActivity(Intent(this@MainActivity, PremiumActivity::class.java))
        }
        findViewById<View>(R.id.navPrivacyPolicyBtn).setOnClickListener { v: View? ->
            closeDrawer()
            Utils.openUrl(activity!!, Constants.PRIVACY_POLICY_URL)
        }
        findViewById<View>(R.id.navShareBtn).setOnClickListener { v: View? ->
            closeDrawer()
            Utils.shareApp(activity!!, resources.getString(R.string.app_name))
        }
        findViewById<View>(R.id.navRateBtn).setOnClickListener { v: View? ->
            closeDrawer()
            Utils.rateApp(activity!!)
        }
        findViewById<View>(R.id.navAboutBtn).setOnClickListener {
            closeDrawer()
            startActivity(Intent(this@MainActivity, AboutActivity::class.java))
        }
        findViewById<View>(R.id.navMoreBtn).setOnClickListener { v: View? ->
            closeDrawer()
            Utils.moreApps(activity!!)
        }
    }

    private fun openDrawer() {
        drawer!!.openDrawer(GravityCompat.START)
    }

    private fun closeDrawer() {
        drawer!!.closeDrawer(GravityCompat.START)
    }

    private fun lightDarkMode() {
        dayNightSwitch!!.setDuration(450)
        dayNightSwitch!!.setIsNight(ThemeSettings.getInstance(this)!!.nightMode)

        dayNightSwitch!!.setListener { isNight ->
            closeDrawer()
            Handler().postDelayed({
                ThemeSettings.getInstance(this@MainActivity)!!.nightMode = isNight
                ThemeSettings.getInstance(this@MainActivity)!!.refreshTheme()
            }, 300)

        }

        dayNightSwitch!!.setAnimListener(object : DayNightSwitchAnimListener {
            override fun onAnimEnd() {
                val intent = Intent(this@MainActivity, this@MainActivity.javaClass)
                intent.putExtras(getIntent())
                startActivity(intent)
                overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out)
                finish()
            }

            override fun onAnimValueChanged(v: Float) {}
            override fun onAnimStart() {}
        })

    }

    private fun checkUpdate() {
        val appUpdateInfoTask: Task<AppUpdateInfo> = appUpdateManager!!.appUpdateInfo
        appUpdateInfoTask.addOnSuccessListener { appUpdateInfo ->
            if (appUpdateInfo.updateAvailability() == UpdateAvailability.UPDATE_AVAILABLE
                && appUpdateInfo.isUpdateTypeAllowed(AppUpdateType.FLEXIBLE)
            ) {
                startUpdateFlow(appUpdateInfo)
            } else if (appUpdateInfo.installStatus() == InstallStatus.DOWNLOADED) {
                popupSnackBarForCompleteUpdate()
            }
        }
    }

    private fun startUpdateFlow(appUpdateInfo: AppUpdateInfo) {
        try {
            appUpdateManager!!.startUpdateFlowForResult(
                appUpdateInfo,
                AppUpdateType.FLEXIBLE,
                this,
                Constants.FLEXIBLE_APP_UPDATE_REQ_CODE
            )
        } catch (e: SendIntentException) {
            e.printStackTrace()
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == Constants.FLEXIBLE_APP_UPDATE_REQ_CODE) {
            when (resultCode) {
                RESULT_CANCELED -> {
                    Toast.makeText(
                        applicationContext,
                        "Update canceled by user! Result Code: $resultCode", Toast.LENGTH_LONG
                    ).show()
                }
                RESULT_OK -> {
                    Toast.makeText(
                        applicationContext,
                        "Update success! Result Code: $resultCode", Toast.LENGTH_LONG
                    ).show()
                }
                else -> {
                    Toast.makeText(
                        applicationContext,
                        "Update Failed! Result Code: $resultCode",
                        Toast.LENGTH_LONG
                    ).show()
                    checkUpdate()
                }
            }
        }
    }

    private fun popupSnackBarForCompleteUpdate() {
        Snackbar.make(
            findViewById<View>(android.R.id.content).rootView,
            "New version of " + resources.getString(R.string.app_name) + " is ready!",
            Snackbar.LENGTH_INDEFINITE
        )
            .setAction("Install") { view: View? ->
                if (appUpdateManager != null) {
                    appUpdateManager!!.completeUpdate()
                }
            }
            .setActionTextColor(resources.getColor(R.color.text_color))
            .show()
    }

    private fun removeInstallStateUpdateListener() {
        if (appUpdateManager != null) {
            appUpdateManager!!.unregisterListener(installStateUpdatedListener!!)
        }
    }

    override fun onStop() {
        super.onStop()
        removeInstallStateUpdateListener()
    }

    override fun onDestroy() {
        super.onDestroy()
        ThemeSettings.getInstance(this)!!.save(this)

    }

    private fun checkAndRequestPermissions(): Boolean {
        val permissionReadExternalStorage: Int = if (Build.VERSION.SDK_INT != Build.VERSION_CODES.TIRAMISU) ContextCompat.checkSelfPermission(
            this,
            Manifest.permission.READ_EXTERNAL_STORAGE
        ) else 0
        val permissionWriteExternalStorage: Int = if (Build.VERSION.SDK_INT != Build.VERSION_CODES.TIRAMISU) ContextCompat.checkSelfPermission(
            this,
            Manifest.permission.WRITE_EXTERNAL_STORAGE
        ) else 0

        val listPermissionsNeeded: MutableList<String> = ArrayList()
        if (permissionWriteExternalStorage!= PackageManager.PERMISSION_GRANTED) {
            if (Build.VERSION.SDK_INT != Build.VERSION_CODES.TIRAMISU) listPermissionsNeeded.add(
                Manifest.permission.WRITE_EXTERNAL_STORAGE
            )
        }
        if (permissionReadExternalStorage != PackageManager.PERMISSION_GRANTED) {
            if (Build.VERSION.SDK_INT != Build.VERSION_CODES.TIRAMISU) listPermissionsNeeded.add(
                Manifest.permission.READ_EXTERNAL_STORAGE
            )
        }

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            val permissionVideoStorage = ContextCompat.checkSelfPermission(
                this,
                Manifest.permission.READ_MEDIA_VIDEO
            )
            if (permissionVideoStorage != PackageManager.PERMISSION_GRANTED) {
                listPermissionsNeeded.add(Manifest.permission.READ_MEDIA_VIDEO)
            }
            val notificationPermission = ContextCompat.checkSelfPermission(
                this,
                Manifest.permission.POST_NOTIFICATIONS
            )
            if (notificationPermission != PackageManager.PERMISSION_GRANTED) {
                listPermissionsNeeded.add(Manifest.permission.POST_NOTIFICATIONS)
            }
        }
        if (listPermissionsNeeded.isNotEmpty()) {
            ActivityCompat.requestPermissions(
                this,
                listPermissionsNeeded.toTypedArray(),
                Constants.REQUEST_ID_MULTIPLE_PERMISSIONS
            )
            return false
        }
        return true
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        @NonNull permissions: Array<String>, @NonNull grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        when (requestCode) {
            Constants.REQUEST_ID_MULTIPLE_PERMISSIONS -> {
                val perms: MutableMap<String, Int> = HashMap()
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                    perms[Manifest.permission.READ_MEDIA_VIDEO] = PackageManager.PERMISSION_GRANTED
                    perms[Manifest.permission.POST_NOTIFICATIONS] =
                        PackageManager.PERMISSION_GRANTED
                } else {
                    perms[Manifest.permission.WRITE_EXTERNAL_STORAGE] =
                        PackageManager.PERMISSION_GRANTED
                    perms[Manifest.permission.READ_EXTERNAL_STORAGE] =
                        PackageManager.PERMISSION_GRANTED
                }

                if (grantResults.isNotEmpty()) {
                    var i = 0
                    while (i < permissions.size) {
                        perms[permissions[i]] = grantResults[i]
                        i++
                    }
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                        if (perms[Manifest.permission.READ_MEDIA_VIDEO] == PackageManager.PERMISSION_GRANTED &&
                            perms[Manifest.permission.POST_NOTIFICATIONS] == PackageManager.PERMISSION_GRANTED) {
                            Toast.makeText(
                                this,
                                "Permissions granted!",
                                Toast.LENGTH_LONG
                            ).show()

                        } else {
                            if (ActivityCompat.shouldShowRequestPermissionRationale(
                                    this,
                                    Manifest.permission.READ_MEDIA_VIDEO
                                )
                                || ActivityCompat.shouldShowRequestPermissionRationale(
                                    this,
                                    Manifest.permission.POST_NOTIFICATIONS
                                )
                            ) {
                                val message = "Necessary Permissions are required by the app for notifications and to save statuses on your device"
                                showPermissionRequiredDialog(message)
                            } else {
                                permissionSettingScreen()
                            }
                        }
                    } else {
                        if (perms[Manifest.permission.WRITE_EXTERNAL_STORAGE] == PackageManager.PERMISSION_GRANTED
                            && perms[Manifest.permission.READ_EXTERNAL_STORAGE] == PackageManager.PERMISSION_GRANTED
                        ) {
                            Toast.makeText(
                                this,
                                "Permissions granted",
                                Toast.LENGTH_LONG
                            ).show()

                        } else {
                            if (ActivityCompat.shouldShowRequestPermissionRationale(
                                    this,
                                    Manifest.permission.WRITE_EXTERNAL_STORAGE
                                )
                                || ActivityCompat.shouldShowRequestPermissionRationale(
                                    this,
                                    Manifest.permission.READ_EXTERNAL_STORAGE
                                )
                            ) {
                                val message = "Necessary Permissions are required by the app to save statuses on your device"
                                showPermissionRequiredDialog(message)
                            } else {
                                permissionSettingScreen()
                            }
                        }
                    }
                }
            }
        }
    }

    private fun permissionSettingScreen() {
        Toast.makeText(this, "Enable All permissions, Click On Permission", Toast.LENGTH_LONG).show()
        val intent = Intent()
        intent.action = Settings.ACTION_APPLICATION_DETAILS_SETTINGS
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        val uri = Uri.fromParts("package", packageName, null)
        intent.data = uri
        startActivity(intent)
        finish()
    }

    private fun showPermissionRequiredDialog(message: String) {
        AwesomeDialog.build(activity!!)
            .title(
                "Permissions required!",
                titleColor = ContextCompat.getColor(activity!!, android.R.color.white)
            )
            .body(
                message,
                color = ContextCompat.getColor(activity!!, android.R.color.white)
            )
            .icon(R.drawable.ic_permission)
            .background(R.drawable.rounded_bg)
            .onPositive(
                "OK",
                buttonBackgroundColor = R.drawable.rounded_bg_white,
                textColor = ContextCompat.getColor(activity!!, R.color.text_color)
            ) {
                checkAndRequestPermissions()
            }
            .onNegative(
                "Cancel",
                buttonBackgroundColor = R.drawable.rounded_bg_white,
                textColor = ContextCompat.getColor(activity!!, R.color.text_color)
            ) {
                AwesomeDialog.build(activity!!).dismiss()
                permissionSettingScreen()
                Toast.makeText(
                    activity,
                    message,
                    Toast.LENGTH_LONG
                ).show()

            }

    }

    override fun onBackPressed() {
        when {
            drawer!!.isDrawerOpen(GravityCompat.START) -> {
                closeDrawer()
            }
            backPressedTime + 2000 > System.currentTimeMillis() -> {
                super.onBackPressed()
                finishAffinity()
            }
            else -> {
                Toast.makeText(applicationContext, "Please click BACK again to exit", Toast.LENGTH_SHORT).show()
            }
        }
        backPressedTime = System.currentTimeMillis()

    }

}