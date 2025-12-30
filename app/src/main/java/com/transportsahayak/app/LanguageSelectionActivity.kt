package com.transportsahayak.app

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity

class LanguageSelectionActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Check if language already selected (Optional: skip if needed)
        // val prefs = getSharedPreferences("AppSettings", Context.MODE_PRIVATE)
        // if (prefs.contains("Language")) { ... }

        setContentView(R.layout.activity_language_selection)

        findViewById<Button>(R.id.btnEnglish).setOnClickListener { setLanguage("en") }
        findViewById<Button>(R.id.btnHindi).setOnClickListener { setLanguage("hi") }
        findViewById<Button>(R.id.btnMarathi).setOnClickListener { setLanguage("mr") }
    }

    private fun setLanguage(langCode: String) {
        val prefs = getSharedPreferences("AppSettings", Context.MODE_PRIVATE)
        prefs.edit().putString("My_Lang", langCode).apply()

        // Use LocaleHelper to set locale here (assuming you have the helper class)
        // LocaleHelper.setLocale(this, langCode)

        startActivity(Intent(this, ServiceSelectionActivity::class.java))
        finish()
    }
}