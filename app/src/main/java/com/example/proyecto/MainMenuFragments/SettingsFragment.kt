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
import com.example.proyecto.NotificationWorker.Worker
import java.util.*
import java.util.concurrent.TimeUnit

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
        val languageAdapter = object : ArrayAdapter<String>(requireContext(), android.R.layout.simple_list_item_1, languages) {
            override fun getFilter() = object : android.widget.Filter() {
                override fun performFiltering(constraint: CharSequence?) = FilterResults().apply {
                    values = languages
                    count = languages.size
                }
                override fun publishResults(constraint: CharSequence?, results: FilterResults?) {
                    notifyDataSetChanged()
                }
            }
        }
        binding.autoLanguage.setAdapter(languageAdapter)
        binding.autoLanguage.setText(languages[selectedLanguagePosition], false)

        val themes = listOf("FerreFit", "Blue", "Purple", "Green")
        val themeAdapter = object : ArrayAdapter<String>(requireContext(), android.R.layout.simple_list_item_1, themes) {
            override fun getFilter() = object : android.widget.Filter() {
                override fun performFiltering(constraint: CharSequence?) = FilterResults().apply {
                    values = themes
                    count = themes.size
                }
                override fun publishResults(constraint: CharSequence?, results: FilterResults?) {
                    notifyDataSetChanged()
                }
            }
        }
        binding.autoTheme.setAdapter(themeAdapter)
        binding.autoTheme.setText(themes[selectedThemePosition], false)

        binding.autoLanguage.setOnClickListener { binding.autoLanguage.showDropDown() }
        binding.autoLanguage.setOnFocusChangeListener { _, hasFocus -> if (hasFocus) binding.autoLanguage.showDropDown() }

        binding.autoTheme.setOnClickListener { binding.autoTheme.showDropDown() }
        binding.autoTheme.setOnFocusChangeListener { _, hasFocus -> if (hasFocus) binding.autoTheme.showDropDown() }

        binding.switchNotifications.isChecked = notificationsEnabled

        Handler(Looper.getMainLooper()).post {
            binding.autoLanguage.clearFocus()
        }

        binding.switchNotifications.setOnCheckedChangeListener { _, isChecked ->
            notificationsEnabled = isChecked
            saveNotificationPreference(isChecked)
            if (isChecked) {
                schedulePeriodicNotification()
                requestOrShowActivationNotification()
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

    private fun requestOrShowActivationNotification() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU &&
            ActivityCompat.checkSelfPermission(requireContext(), Manifest.permission.POST_NOTIFICATIONS)
            != PackageManager.PERMISSION_GRANTED
        ) {
            requestPermissions(arrayOf(Manifest.permission.POST_NOTIFICATIONS), 1001)
        } else {
            showActivationNotification()
        }
    }

    private fun showActivationNotification() {
        val notification = NotificationCompat.Builder(requireContext(), CHANNEL_ID)
            .setSmallIcon(R.drawable.app_logo)
            .setContentTitle(getString(R.string.notifications_enabled_title))
            .setContentText(getString(R.string.notifications_enabled_text))
            .setAutoCancel(true)
            .build()

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU &&
            ActivityCompat.checkSelfPermission(requireContext(), Manifest.permission.POST_NOTIFICATIONS)
            != PackageManager.PERMISSION_GRANTED
        ) {
            requestPermissions(arrayOf(Manifest.permission.POST_NOTIFICATIONS), 1001)
        } else {
            NotificationManagerCompat.from(requireContext()).notify(99, notification)
        }
    }

    @Deprecated("Deprecated in Java")
    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == 1001 && grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            showActivationNotification()
        } else {
            Toast.makeText(requireContext(), "Permiso de notificaciones denegado", Toast.LENGTH_SHORT).show()
        }
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
        requireActivity().getSharedPreferences("settings", Context.MODE_PRIVATE)
            .edit().putString("language", language).apply()
    }

    private fun saveThemePreference(theme: String) {
        requireActivity().getSharedPreferences("settings", Context.MODE_PRIVATE)
            .edit().putString("theme", theme).apply()
    }

    private fun saveNotificationPreference(enabled: Boolean) {
        val prefs = requireActivity().getSharedPreferences("settings", Context.MODE_PRIVATE)
        with(prefs.edit()) {
            putBoolean("notifications_enabled", enabled)
            if (enabled) {
                putLong("notifications_start_time", System.currentTimeMillis())
            } else {
                remove("notifications_start_time")
            }
            apply()
        }
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
