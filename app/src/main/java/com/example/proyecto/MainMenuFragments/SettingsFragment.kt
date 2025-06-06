package com.example.proyecto.MainMenuFragments

import android.Manifest
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.fragment.app.Fragment
import androidx.work.*
import com.example.proyecto.R
import com.example.proyecto.databinding.FragmentSettingsBinding
import java.util.*
import java.util.concurrent.TimeUnit
import com.example.proyecto.NotificationWorker.Worker

class SettingsFragment : Fragment() {

    private var _binding: FragmentSettingsBinding? = null
    private val binding get() = _binding!!
    private lateinit var notificationManager: NotificationManager
    private val CHANNEL_ID = "default_channel"
    private val WORK_TAG = "periodic_notification_work"

    companion object {
        var selectedLanguagePosition: Int = 0
        var currentLanguage: String = "en"
        var selectedThemePosition: Int = 0
        var currentTheme: String = "FerreFit"
        var notificationsEnabled: Boolean = false
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentSettingsBinding.inflate(inflater, container, false)
        val view = binding.root

        notificationManager = requireActivity().getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                CHANNEL_ID,
                "Default Channel",
                NotificationManager.IMPORTANCE_DEFAULT
            )
            notificationManager.createNotificationChannel(channel)
        }

        loadPreferences()

        val languages = listOf("English", "Español", "Français")
        val languageAdapter = ArrayAdapter(requireContext(), android.R.layout.simple_list_item_1, languages)
        binding.autoLanguage.setAdapter(languageAdapter)
        binding.autoLanguage.setText(languages[selectedLanguagePosition], false)

        val themes = listOf("FerreFit", "Blue", "Purple", "Green")
        val themeAdapter = ArrayAdapter(requireContext(), android.R.layout.simple_list_item_1, themes)
        binding.autoTheme.setAdapter(themeAdapter)
        binding.autoTheme.setText(themes[selectedThemePosition], false)

        binding.switchNotifications.isChecked = notificationsEnabled

        Handler(Looper.getMainLooper()).post {
            binding.autoLanguage.clearFocus()
        }

        binding.switchNotifications.setOnCheckedChangeListener { _, isChecked ->
            notificationsEnabled = isChecked
            saveNotificationPreference(isChecked)
            if (isChecked) {
                schedulePeriodicNotification()
            } else {
                cancelPeriodicNotification()
                disableNotifications()
            }
        }

        binding.btnHelpSupport.setOnClickListener {
            Toast.makeText(requireContext(), "En desarrollo", Toast.LENGTH_SHORT).show()
        }

        binding.autoLanguage.setOnItemClickListener { _, _, position, _ ->
            val selectedLang = when (position) {
                0 -> "en"
                1 -> "es"
                2 -> "fr"
                else -> "en"
            }
            if (selectedLang != currentLanguage) {
                selectedLanguagePosition = position
                currentLanguage = selectedLang
                saveLanguagePreference(selectedLang)
                setLocale(selectedLang)
            }
        }

        binding.autoTheme.setOnItemClickListener { _, _, position, _ ->
            val selectedTheme = themes[position]
            if (selectedTheme != currentTheme) {
                selectedThemePosition = position
                currentTheme = selectedTheme
                saveThemePreference(selectedTheme)
                applyTheme(selectedTheme)
            }
        }

        binding.autoLanguage.setOnClickListener {
            binding.autoLanguage.showDropDown()
        }

        binding.autoTheme.setOnClickListener {
            binding.autoTheme.showDropDown()
        }

        return view
    }
    private fun schedulePeriodicNotification() {
        val constraints = Constraints.Builder()
            .setRequiresBatteryNotLow(true)
            .build()

        val periodicWorkRequest = PeriodicWorkRequestBuilder<Worker>(15, TimeUnit.MINUTES)
            .setConstraints(constraints)
            .addTag(WORK_TAG)
            .build()

        WorkManager.getInstance(requireContext()).enqueueUniquePeriodicWork(
            WORK_TAG,
            ExistingPeriodicWorkPolicy.REPLACE,
            periodicWorkRequest
        )
    }

    private fun cancelPeriodicNotification() {
        WorkManager.getInstance(requireContext()).cancelAllWorkByTag(WORK_TAG)
    }

    private fun disableNotifications() {
        NotificationManagerCompat.from(requireContext()).cancelAll()
    }

    private fun loadPreferences() {
        val prefs = requireActivity().getSharedPreferences("settings", Context.MODE_PRIVATE)
        currentLanguage = prefs.getString("language", "en") ?: "en"
        currentTheme = prefs.getString("theme", "FerreFit") ?: "FerreFit"
        notificationsEnabled = prefs.getBoolean("notifications_enabled", false)

        selectedLanguagePosition = when (currentLanguage) {
            "en" -> 0
            "es" -> 1
            "fr" -> 2
            else -> 0
        }

        selectedThemePosition = when (currentTheme) {
            "FerreFit" -> 0
            "Blue" -> 1
            "Purple" -> 2
            "Green" -> 3
            else -> 0
        }
    }

    private fun saveLanguagePreference(language: String) {
        val prefs = requireActivity().getSharedPreferences("settings", Context.MODE_PRIVATE)
        prefs.edit().putString("language", language).apply()
    }

    private fun saveThemePreference(theme: String) {
        val prefs = requireActivity().getSharedPreferences("settings", Context.MODE_PRIVATE)
        prefs.edit().putString("theme", theme).apply()
    }

    private fun saveNotificationPreference(enabled: Boolean) {
        val prefs = requireActivity().getSharedPreferences("settings", Context.MODE_PRIVATE)
        prefs.edit().putBoolean("notifications_enabled", enabled).apply()
    }

    private fun setLocale(language: String) {
        val locale = Locale(language)
        Locale.setDefault(locale)
        val config = requireContext().resources.configuration
        config.setLocale(locale)
        requireActivity().baseContext.resources.updateConfiguration(config, requireActivity().baseContext.resources.displayMetrics)
        requireActivity().recreate()
    }

    private fun applyTheme(theme: String) {
        requireActivity().recreate()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
