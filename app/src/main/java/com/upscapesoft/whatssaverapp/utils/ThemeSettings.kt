package com.upscapesoft.whatssaverapp.utils

import android.app.Activity
import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatDelegate
import com.upscapesoft.whatssaverapp.activities.MainActivity

class ThemeSettings private constructor(context: Context) {

    private object Key {
        const val THEME_ACCENT_COLOR_DEFAULT = 0
        const val THEME_ACCENT_COLOR_RED = 1
        const val THEME_ACCENT_COLOR_GREEN = 2
        const val NIGHT_MODE = "nightMode"
    }

    @JvmField
    var nightMode: Boolean
    fun save(context: Context) {
        val editor = context.getSharedPreferences("settings", Context.MODE_PRIVATE).edit()
        editor.putBoolean(Key.NIGHT_MODE, nightMode)
        editor.apply()
    }

    fun refreshTheme() {
        AppCompatDelegate.setDefaultNightMode(if (nightMode) AppCompatDelegate.MODE_NIGHT_YES else AppCompatDelegate.MODE_NIGHT_NO)
    }

    fun changeToTheme(activity: Activity) {
        activity.finish()
        activity.startActivity(Intent(activity, MainActivity::class.java))
        activity.overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out)
    }

    /**fun onActivityCreateSetTheme(activity: Activity, sTheme: Int) {
        when (sTheme) {
            Key.THEME_ACCENT_COLOR_DEFAULT -> activity.setTheme(R.style.Theme_MyTodos_Default)
            Key.THEME_ACCENT_COLOR_RED -> activity.setTheme(R.style.Theme_MyTodos_Red)
            Key.THEME_ACCENT_COLOR_GREEN -> activity.setTheme(R.style.Theme_MyTodos_Green)
            else -> activity.setTheme(R.style.Theme_MyTodos_Default)
        }
    }*/

    companion object {
        private var instance: ThemeSettings? = null
        @JvmStatic
        fun getInstance(context: Context): ThemeSettings? {
            if (instance == null) instance = ThemeSettings(context)
            return instance
        }
    }

    init {
        val prefs = context.getSharedPreferences("settings", Context.MODE_PRIVATE)
        nightMode = prefs.getBoolean(Key.NIGHT_MODE, false)
    }

}