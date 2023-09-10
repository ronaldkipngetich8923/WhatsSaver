package com.upscapesoft.whatssaverapp.fragments

import android.annotation.SuppressLint
import android.content.ActivityNotFoundException
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.rilixtech.widget.countrycodepicker.CountryCodePicker
import com.upscapesoft.whatssaverapp.R
import java.io.UnsupportedEncodingException
import java.net.URLEncoder

class WABDirectChatFragment : Fragment() {

    var btn_send: Button? = null
    var ccp: CountryCodePicker? = null
    var edt_message: EditText? = null
    var edt_phonenumber: EditText? = null

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View? {
        return inflater.inflate(R.layout.fragment_direct_chat, container, false)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        btn_send = view?.findViewById<View>(R.id.btn_send) as Button
        edt_message = view?.findViewById<View>(R.id.edt_message) as EditText
        edt_phonenumber = view?.findViewById<View>(R.id.etphonenumber) as EditText
        ccp = view?.findViewById<View>(R.id.ccp) as CountryCodePicker
        btn_send!!.setOnClickListener {
            openWhatsAppBusiness()
        }

    }

    private fun openWhatsAppBusiness() {
        val str: String =
            ccp!!.selectedCountryCode.toString() + " " + edt_phonenumber!!.text.toString()
        if (whatsappBusinessInstalledOrNot("com.whatsapp.w4b")) {
            val mIntent = Intent(
                "android.intent.action.VIEW", Uri.parse(
                    "whatsapp://send/?text=" + URLEncoder.encode(
                        edt_message!!.text.toString(), "UTF-8"
                    ).toString() + "&phone=" + str
                )
            )
            mIntent.setPackage("com.whatsapp.w4b")
            try {
                startActivity(
                    mIntent
                )
            } catch (unused: ActivityNotFoundException) {
                Toast.makeText(requireActivity().applicationContext, "Please install WhatsApp Business", Toast.LENGTH_LONG).show()
            } catch (unused2: UnsupportedEncodingException) {
                Toast.makeText(requireActivity().applicationContext, "Error while encoding your text message", Toast.LENGTH_LONG)
                    .show()
            }
        } else {
            val intent =
                Intent("android.intent.action.VIEW", Uri.parse("https://play.google.com/store/apps/details?id=com.whatsapp.w4b&hl"))
            Toast.makeText(requireActivity().applicationContext, "WhatsApp Business not Installed", Toast.LENGTH_SHORT).show()
            startActivity(intent)
        }
    }

    @SuppressLint("WrongConstant")
    private fun whatsappBusinessInstalledOrNot(str: String): Boolean {
        return try {
            requireActivity().packageManager.getPackageInfo(str, 1)
            true
        } catch (unused: PackageManager.NameNotFoundException) {
            false
        }
    }

}