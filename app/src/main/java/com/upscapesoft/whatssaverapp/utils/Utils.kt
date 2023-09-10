package com.upscapesoft.whatssaverapp.utils

import android.app.Activity
import android.app.Dialog
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.net.Uri
import android.widget.RelativeLayout
import android.widget.TextView
import com.upscapesoft.whatssaverapp.R
import com.upscapesoft.whatssaverapp.activities.WABStatusActivity
import com.upscapesoft.whatssaverapp.activities.WAStatusActivity
import java.util.Locale

class Utils(private var context: Context) {

    companion object {

        fun startAppWhatsapp(activity: Activity) {
            activity.startActivity(Intent(activity, WAStatusActivity::class.java))
        }

        fun startAppWhatsappBusiness(activity: Activity) {
            activity.startActivity(Intent(activity, WABStatusActivity::class.java))
        }

        fun setLanguage(context: Context, lang: String?) {
            val myLocale = Locale(lang)
            val res = context.resources
            val dm = res.displayMetrics
            val conf = res.configuration
            conf.locale = myLocale
            res.updateConfiguration(conf, dm)
        }

        fun isPackageInstalled(packageName: String, packageManager: PackageManager): Boolean {
            return try {
                packageManager.getPackageInfo(packageName, 0)
                true
            } catch (e: PackageManager.NameNotFoundException) {
                false
            }
        }

        fun openWhatsapp(activity: Activity) {
            try {
               activity.startActivity(activity.packageManager.getLaunchIntentForPackage("com.whatsapp"))
            } catch (e: Exception) {
                e.printStackTrace()
                installWhatsAppDialog(activity)
            }
        }

        fun openWhatsappBusiness(activity: Activity) {
            try {
                activity.startActivity(activity.packageManager.getLaunchIntentForPackage("com.whatsapp.w4b"))
            } catch (e: Exception) {
                e.printStackTrace()
                installWhatsAppBusinessDialog(activity)
            }
        }

        fun installWhatsAppDialog(activity: Activity) {
            val dialog = Dialog(activity)
            dialog.setContentView(R.layout.install_whatsapp_dialog_lay)
            dialog.window!!.setBackgroundDrawable(
                ColorDrawable(Color.TRANSPARENT)
            )

            val installWhatsappBtn: RelativeLayout = dialog.findViewById(R.id.installWhatsappBtn)
            val dismissBtn: TextView = dialog.findViewById(R.id.dismissBtn)

            installWhatsappBtn.setOnClickListener {
                val url = "https://play.google.com/store/apps/details?id=com.whatsapp&hl"
                val i = Intent(Intent.ACTION_VIEW)
                i.data = Uri.parse(url)
                activity.startActivity(i)
                dialog.dismiss()
            }

            dismissBtn.setOnClickListener {
                dialog.dismiss()
            }

            dialog.show()
        }

        fun installWhatsAppBusinessDialog(activity: Activity) {
            val dialog = Dialog(activity)
            dialog.setContentView(R.layout.install_whatsapp_business_dialog_lay)
            dialog.window!!.setBackgroundDrawable(
                ColorDrawable(Color.TRANSPARENT)
            )

            val installWhatsappBtn: RelativeLayout = dialog.findViewById(R.id.installWhatsappBtn)
            val dismissBtn: TextView = dialog.findViewById(R.id.dismissBtn)

            installWhatsappBtn.setOnClickListener {
                val url = "https://play.google.com/store/apps/details?id=com.whatsapp.w4b&hl"
                val i = Intent(Intent.ACTION_VIEW)
                i.data = Uri.parse(url)
                activity.startActivity(i)
                dialog.dismiss()
            }

            dismissBtn.setOnClickListener {
                dialog.dismiss()
            }

            dialog.show()
        }

        fun shareApp(activity: Activity, appName: String) {
            val sharingIntent = Intent(Intent.ACTION_SEND)
            sharingIntent.type = "text/plain"
            val shareBodyText =
                "https://play.google.com/store/apps/details?id=" + activity.packageName
            sharingIntent.putExtra(
                Intent.EXTRA_SUBJECT,
                "Want to save WhatsApp & WhatsApp Business status? Want to direct chat without saving the contact number? Try $appName. DOWNLOAD NOW!"
            )
            sharingIntent.putExtra(Intent.EXTRA_TEXT, shareBodyText)
            activity.startActivity(
                Intent.createChooser(
                    sharingIntent,
                    activity.resources.getString(R.string.share_via)
                )
            )
        }

        fun rateApp(activity: Activity) {
            activity.startActivity(
                Intent(
                    Intent.ACTION_VIEW,
                    Uri.parse("market://details?id=" + activity.packageName)
                )
            )
        }

        fun moreApps(activity: Activity) {
            activity.startActivity(
                Intent(
                    Intent.ACTION_VIEW,
                    Uri.parse("market://details?id=" + activity.packageName)
                )
            )
        }

        fun openUrl(activity: Activity, url: String) {
            if (url.isEmpty()) return
            val intent = Intent(Intent.ACTION_VIEW)
            intent.data = Uri.parse(url)
            activity.startActivity(intent)
        }

    }

}