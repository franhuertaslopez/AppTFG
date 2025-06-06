package com.example.proyecto.Language_Theme

import android.content.Context
import android.content.res.Configuration
import androidx.appcompat.app.AppCompatActivity
import com.example.proyecto.R
import java.util.*

open class BaseActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: android.os.Bundle?) {
        applySavedLanguage()
        applySavedTheme()
        super.onCreate(savedInstanceState)
    }

    private fun applySavedLanguage() {
        val prefs = getSharedPreferences("settings", Context.MODE_PRIVATE)
        val language = prefs.getString("language", "en") ?: "en"
        val locale = Locale(language)
        Locale.setDefault(locale)
        val config = Configuration()
        config.setLocale(locale)
        baseContext.resources.updateConfiguration(config, baseContext.resources.displayMetrics)
    }

    private fun applySavedTheme() {
        val prefs = getSharedPreferences("settings", Context.MODE_PRIVATE)
        val theme = prefs.getString("theme", "FerreFit") ?: "FerreFit"

        when (theme) {
            "FerreFit" -> setTheme(R.style.Theme_Proyecto)
            "Blue" -> setTheme(R.style.Theme_Blue)
            "Purple" -> setTheme(R.style.Theme_Purple)
            "Green" -> setTheme(R.style.Theme_Green)
            else -> setTheme(R.style.Theme_Proyecto)
        }
    }
}