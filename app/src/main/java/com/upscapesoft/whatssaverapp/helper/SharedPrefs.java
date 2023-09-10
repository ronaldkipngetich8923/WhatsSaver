package com.upscapesoft.whatssaverapp.helper;

import android.content.Context;
import android.content.SharedPreferences;

import androidx.appcompat.app.AppCompatDelegate;

public class SharedPrefs {

    public static final String PREF_NIGHT_MODE = "night_mode";
    private static SharedPreferences mPreferences;

    private static SharedPreferences getInstance(Context context) {
        if (mPreferences == null) {
            mPreferences = context.getApplicationContext()
                    .getSharedPreferences("wa_data", Context.MODE_PRIVATE);
        }
        return mPreferences;
    }

    public static int getInt(Context context, String key, int defaultValue) {
        return getInstance(context).getInt(key, defaultValue);
    }

    public static void setInt(Context context, String key, int value) {
        getInstance(context).edit().putInt(key, value).apply();
    }

    public static void clearPrefs(Context context) {
        getInstance(context).edit().clear().apply();
    }

    public static int getAppNightDayMode(Context context) {
        return getInt(context, PREF_NIGHT_MODE, AppCompatDelegate.MODE_NIGHT_NO);
    }

    public static void setLang(Context context, String value) {
        getInstance(context).edit().putString("language", value).apply();
    }

    public static String getLang(Context context) {
        return getInstance(context).getString("language", "en");
    }

}
