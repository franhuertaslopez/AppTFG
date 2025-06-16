package com.example.proyecto.StretchingAndMobility

import android.graphics.Color
import android.os.Bundle
import android.view.animation.Animation
import android.view.animation.AnimationUtils
import android.widget.TextView
import com.example.proyecto.Language_Theme.BaseActivity
import com.example.proyecto.R
import com.example.proyecto.databinding.ActivityStretchingBinding
import java.util.Locale

class StretchingActivity : BaseActivity() {

    private lateinit var binding: ActivityStretchingBinding
    private lateinit var routines: List<Routine>
    private var currentIndex = 0
    private var currentLanguage = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityStretchingBinding.inflate(layoutInflater)
        setContentView(binding.root)

        currentLanguage = Locale.getDefault().language
        loadRoutinesForCurrentLanguage()

        if (routines.isNotEmpty()) {
            showRoutineAtIndex(currentIndex)
        }

        binding.btnGenerateRoutine.setOnClickListener {
            currentIndex = (currentIndex + 1) % routines.size
            showRoutineAtIndex(currentIndex)
        }

        binding.btnBack.setOnClickListener {
            finish()
        }
    }

    override fun onResume() {
        super.onResume()
        // Comprobar si el idioma cambió mientras la actividad no estaba visible
        val newLanguage = Locale.getDefault().language
        if (newLanguage != currentLanguage) {
            currentLanguage = newLanguage
            loadRoutinesForCurrentLanguage()
            currentIndex = 0
            if (routines.isNotEmpty()) {
                showRoutineAtIndex(currentIndex)
            }
        }
    }

    private fun loadRoutinesForCurrentLanguage() {
        routines = StretchingRoutines.loadRoutines(this)
    }

    private fun showRoutineAtIndex(index: Int) {
        val slideOut = AnimationUtils.loadAnimation(this, R.anim.slide_out_left)
        val slideIn = AnimationUtils.loadAnimation(this, R.anim.slide_in_right)

        binding.cardRoutine.startAnimation(slideOut)

        slideOut.setAnimationListener(object : Animation.AnimationListener {
            override fun onAnimationStart(animation: Animation?) {}
            override fun onAnimationRepeat(animation: Animation?) {}

            override fun onAnimationEnd(animation: Animation?) {
                val routine = routines[index]
                binding.tvRoutineTitle.text = routine.title
                binding.tvRoutineDescription.text = routine.description

                val container = binding.stepsContainer
                container.removeAllViews()

                routine.steps.forEachIndexed { i, step ->
                    val stepView = TextView(this@StretchingActivity).apply {
                        text = "${i + 1}. $step"
                        textSize = 16f
                        setTextColor(Color.parseColor("#333333"))
                        setPadding(0, 12, 0, 12)
                    }
                    container.addView(stepView)
                }

                binding.cardRoutine.startAnimation(slideIn)
            }
        })
    }
}
