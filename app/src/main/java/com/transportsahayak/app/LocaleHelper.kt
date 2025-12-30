package com.transportsahayak.app

import android.content.Context
import android.content.SharedPreferences
import android.content.res.Configuration
import java.util.*

object LocaleHelper {
    private const val PREF_NAME = "app_prefs"
    private const val KEY_LANGUAGE = "language"
    private const val KEY_FIRST_LAUNCH = "first_launch"

    fun setLocale(context: Context, languageCode: String) {
        saveLanguage(context, languageCode)
        updateResources(context, languageCode)
    }

    fun getLanguage(context: Context): String {
        val prefs = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE)
        return prefs.getString(KEY_LANGUAGE, "en") ?: "en"
    }

    private fun saveLanguage(context: Context, languageCode: String) {
        val prefs = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE)
        prefs.edit().putString(KEY_LANGUAGE, languageCode).apply()
    }

    fun isFirstLaunch(context: Context): Boolean {
        val prefs = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE)
        return prefs.getBoolean(KEY_FIRST_LAUNCH, true)
    }

    fun setFirstLaunchComplete(context: Context) {
        val prefs = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE)
        prefs.edit().putBoolean(KEY_FIRST_LAUNCH, false).apply()
    }

    private fun updateResources(context: Context, languageCode: String) {
        val locale = Locale(languageCode)
        Locale.setDefault(locale)
        val config = Configuration(context.resources.configuration)
        config.setLocale(locale)
        context.resources.updateConfiguration(config, context.resources.displayMetrics)
    }
}
