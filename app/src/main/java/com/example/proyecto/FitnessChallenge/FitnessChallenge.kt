package com.example.proyecto.FitnessChallenge

import android.animation.ObjectAnimator
import android.content.Context
import android.os.Bundle
import android.view.View
import android.view.animation.AlphaAnimation
import android.widget.Toast
import com.example.proyecto.Language_Theme.BaseActivity
import com.example.proyecto.databinding.ActivityFitnessChallengeBinding
import java.text.SimpleDateFormat
import java.util.*

class FitnessChallenge : BaseActivity() {

    private lateinit var binding: ActivityFitnessChallengeBinding

    private var weeklyDays = 0
    private val totalWeekly = 7

    private var monthlyDays = 0
    private val totalMonthly = 30

    private val PREFS = "fitness_prefs"
    private val KEY_SELECTED = "selected_challenge"
    private val KEY_WEEKLY_DAYS = "dias_semanal"
    private val KEY_MONTHLY_DAYS = "dias_mensual"
    private val KEY_LAST_WEEKLY = "last_marked_semanal"
    private val KEY_LAST_MONTHLY = "last_marked_mensual"

    private lateinit var prefs: android.content.SharedPreferences

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityFitnessChallengeBinding.inflate(layoutInflater)
        setContentView(binding.root)

        prefs = getSharedPreferences(PREFS, Context.MODE_PRIVATE)

        resetAll()

        weeklyDays = prefs.getInt(KEY_WEEKLY_DAYS, 0)
        monthlyDays = prefs.getInt(KEY_MONTHLY_DAYS, 0)
        val selected = prefs.getString(KEY_SELECTED, null)

        configureInitialState(selected)
        updateUI()
        updateButtons()

        binding.btnChooseWeekly.setOnClickListener {
            val selected = prefs.getString(KEY_SELECTED, null)
            if (selected != null && selected != "weekly") {
                Toast.makeText(this, "Complete the current challenge before selecting another.", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }
            if (weeklyDays >= totalWeekly) {
                Toast.makeText(this, "You already completed the weekly challenge, select another one.", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }
            selectChallengeWithAnimation("weekly")
        }

        binding.btnChooseMonthly.setOnClickListener {
            val selected = prefs.getString(KEY_SELECTED, null)
            if (selected != null && selected != "monthly") {
                Toast.makeText(this, "Complete the current challenge before selecting another.", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }
            if (monthlyDays >= totalMonthly) {
                Toast.makeText(this, "You already completed the monthly challenge, select another one.", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }
            selectChallengeWithAnimation("monthly")
        }

        binding.btnMarkWeekly.setOnClickListener { markDay("weekly") }
        binding.btnMarkMonthly.setOnClickListener { markDay("monthly") }

        binding.btnBack.setOnClickListener {
            finish()
        }
    }

    private fun configureInitialState(type: String?) {
        when (type) {
            "weekly" -> {
                binding.overlayWeekly.visibility = View.GONE
                binding.overlayMonthly.visibility = View.VISIBLE

                showProgressAndButtons(true, false)
                enableButtons(true, false)

                binding.btnChooseMonthly.visibility = View.GONE
                binding.btnChooseMonthly.visibility = View.GONE
            }
            "monthly" -> {
                binding.overlayWeekly.visibility = View.VISIBLE
                binding.overlayMonthly.visibility = View.GONE

                showProgressAndButtons(false, true)
                enableButtons(false, true)

                binding.btnChooseMonthly.visibility = View.GONE
                binding.btnChooseMonthly.visibility = View.GONE
            }
            else -> {
                binding.overlayWeekly.visibility = View.VISIBLE
                binding.overlayMonthly.visibility = View.VISIBLE

                showProgressAndButtons(false, false)
                enableButtons(false, false)

                binding.btnChooseMonthly.visibility = View.VISIBLE
                binding.btnChooseMonthly.isEnabled = true
                binding.btnChooseMonthly.visibility = View.VISIBLE
                binding.btnChooseMonthly.isEnabled = true
            }
        }
    }

    private fun showProgressAndButtons(weekly: Boolean, monthly: Boolean) {
        binding.progressWeekly.visibility = if (weekly) View.VISIBLE else View.GONE
        binding.tvWeeklyDays.visibility = if (weekly) View.VISIBLE else View.GONE
        binding.btnMarkWeekly.visibility = if (weekly) View.VISIBLE else View.GONE

        binding.progressMonthly.visibility = if (monthly) View.VISIBLE else View.GONE
        binding.tvMonthlyDays.visibility = if (monthly) View.VISIBLE else View.GONE
        binding.btnMarkMonthly.visibility = if (monthly) View.VISIBLE else View.GONE
    }

    private fun enableButtons(weekly: Boolean, monthly: Boolean) {
        binding.btnMarkWeekly.isEnabled = weekly
        binding.btnMarkMonthly.isEnabled = monthly
    }

    private fun selectChallengeWithAnimation(type: String) {
        val selectedOverlay = if (type == "weekly") binding.overlayWeekly else binding.overlayMonthly
        val unselectedOverlay = if (type == "weekly") binding.overlayMonthly else binding.overlayWeekly

        val progressWeekly = (type == "weekly")
        val progressMonthly = !progressWeekly

        AlphaAnimation(1f, 0f).apply {
            duration = 500
            fillAfter = true
            setAnimationListener(object : android.view.animation.Animation.AnimationListener {
                override fun onAnimationStart(animation: android.view.animation.Animation?) {}
                override fun onAnimationRepeat(animation: android.view.animation.Animation?) {}
                override fun onAnimationEnd(animation: android.view.animation.Animation?) {
                    selectedOverlay.visibility = View.GONE
                    unselectedOverlay.visibility = View.VISIBLE

                    showProgressAndButtons(progressWeekly, progressMonthly)
                    enableButtons(progressWeekly, progressMonthly)

                    prefs.edit().putString(KEY_SELECTED, type).apply()
                    Toast.makeText(this@FitnessChallenge, "Challenge $type selected", Toast.LENGTH_SHORT).show()
                    updateButtons()
                }
            })
        }.also { selectedOverlay.startAnimation(it) }
    }

    private fun markDay(type: String) {
        val keyDays = if (type == "weekly") KEY_WEEKLY_DAYS else KEY_MONTHLY_DAYS
        val keyLast = if (type == "weekly") KEY_LAST_WEEKLY else KEY_LAST_MONTHLY
        val total = if (type == "weekly") totalWeekly else totalMonthly

        var days = if (type == "weekly") weeklyDays else monthlyDays

        if (!canMarkToday(keyLast)) {
            Toast.makeText(this, "You can only mark once per day.", Toast.LENGTH_SHORT).show()
            return
        }
        if (days >= total) {
            Toast.makeText(this, "Challenge $type completed!", Toast.LENGTH_SHORT).show()
            return
        }

        days++
        prefs.edit().putInt(keyDays, days).apply()
        saveTodayDate(keyLast)

        if (type == "weekly") weeklyDays = days else monthlyDays = days

        updateUI()
        updateButtons()
        Toast.makeText(this, "Day $type marked!", Toast.LENGTH_SHORT).show()
        checkCompleteChallenge(type)
    }

    private fun updateUI() {
        val weeklyProgress = (weeklyDays * 100) / totalWeekly
        animateProgressBar(binding.progressWeekly, weeklyProgress)
        binding.tvWeeklyDays.text = "$weeklyDays of $totalWeekly days completed"

        val monthlyProgress = (monthlyDays * 100) / totalMonthly
        animateProgressBar(binding.progressMonthly, monthlyProgress)
        binding.tvMonthlyDays.text = "$monthlyDays of $totalMonthly days completed"
    }

    private fun updateButtons() {
        val today = getTodayDate()
        val selected = prefs.getString(KEY_SELECTED, null)
        val lastWeekly = prefs.getString(KEY_LAST_WEEKLY, "")
        val lastMonthly = prefs.getString(KEY_LAST_MONTHLY, "")

        binding.btnMarkWeekly.isEnabled = selected == "weekly" && lastWeekly != today && weeklyDays < totalWeekly
        binding.btnMarkMonthly.isEnabled = selected == "monthly" && lastMonthly != today && monthlyDays < totalMonthly

        when (selected) {
            "weekly" -> {
                binding.btnChooseWeekly.visibility = View.VISIBLE
                binding.btnChooseWeekly.isEnabled = true
                // Ocultamos el botón mensual completamente
                binding.btnChooseMonthly.visibility = View.GONE
            }
            "monthly" -> {
                binding.btnChooseMonthly.visibility = View.VISIBLE
                binding.btnChooseMonthly.isEnabled = true
                // Ocultamos el botón semanal completamente
                binding.btnChooseWeekly.visibility = View.GONE
            }
            else -> {
                // Ningún desafío seleccionado, ambos botones visibles y habilitados
                binding.btnChooseWeekly.visibility = View.VISIBLE
                binding.btnChooseWeekly.isEnabled = true
                binding.btnChooseMonthly.visibility = View.VISIBLE
                binding.btnChooseMonthly.isEnabled = true
            }
        }
    }



    private fun canMarkToday(keyLast: String) = prefs.getString(keyLast, "") != getTodayDate()

    private fun saveTodayDate(keyLast: String) = prefs.edit().putString(keyLast, getTodayDate()).apply()

    private fun getTodayDate(): String = SimpleDateFormat("yyyyMMdd", Locale.getDefault()).format(Date())

    private fun checkCompleteChallenge(type: String) {
        when (type) {
            "weekly" -> if (weeklyDays >= totalWeekly) {
                Toast.makeText(this, "You have completed the weekly challenge! You can now select another challenge.", Toast.LENGTH_LONG).show()
                prefs.edit().remove(KEY_SELECTED).apply()
                configureInitialState(null)
                updateUI()
                updateButtons()
            }
            "monthly" -> if (monthlyDays >= totalMonthly) {
                Toast.makeText(this, "Congratulations! You have completed the monthly challenge.", Toast.LENGTH_LONG).show()
                prefs.edit().remove(KEY_SELECTED).apply()
                configureInitialState(null)
                updateUI()
                updateButtons()
            }
        }
    }

    private fun resetAll() {
        prefs.edit().clear().apply()
        weeklyDays = 0
        monthlyDays = 0
        configureInitialState(null)
        updateUI()
        updateButtons()
        Toast.makeText(this, "Progress reset", Toast.LENGTH_SHORT).show()
    }

    private fun animateProgressBar(progressBar: android.widget.ProgressBar, targetProgress: Int) {
        val animator = ObjectAnimator.ofInt(progressBar, "progress", progressBar.progress, targetProgress)
        animator.duration = 150 // Duración de la animación en milisegundos
        animator.start()
    }

}
