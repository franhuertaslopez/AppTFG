package com.example.proyecto.TrainingPlan

import android.animation.AnimatorSet
import android.animation.ObjectAnimator
import android.app.AlertDialog
import android.content.Intent
import android.graphics.PorterDuff
import android.graphics.drawable.Drawable
import android.os.Bundle
import android.os.Handler
import android.os.SystemClock
import android.view.LayoutInflater
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.proyecto.MenuActivity
import com.example.proyecto.Model.DailyRoutine
import com.example.proyecto.R
import com.example.proyecto.databinding.ActivityRoutineBinding
import com.example.proyecto.databinding.CustomDialogTrainingSummaryBinding

class RoutineActivity : AppCompatActivity() {

    private lateinit var binding: ActivityRoutineBinding
    private val handler = Handler()

    private var isRunning = false

    private var startTimeExercise = 0L
    private var pauseOffsetExercise = 0L

    private var startTimeGeneral = 0L
    private var pauseOffsetGeneral = 0L

    private lateinit var adapter: ExerciseAdapter
    private var currentExerciseIndex = 0
    private var totalExercises = 0

    // Guardamos los tiempos por ejercicio en milisegundos
    private val exerciseTimes = mutableListOf<Long>()

    private var routineExercises: List<String> = emptyList()

    // Runnable para actualizar el cronómetro del ejercicio actual
    private val updateExerciseTimerRunnable = object : Runnable {
        override fun run() {
            val elapsed = SystemClock.elapsedRealtime() - startTimeExercise + pauseOffsetExercise
            val minutes = (elapsed / 1000 / 60).toInt()
            val seconds = (elapsed / 1000 % 60).toInt()
            val centiseconds = ((elapsed % 1000) / 10).toInt()
            binding.chronometer.text = String.format("%02d:%02d:%02d", minutes, seconds, centiseconds)
            handler.postDelayed(this, 10)
        }
    }

    // Runnable para actualizar el cronómetro general de la rutina
    private val updateGeneralTimerRunnable = object : Runnable {
        override fun run() {
            val elapsed = SystemClock.elapsedRealtime() - startTimeGeneral + pauseOffsetGeneral
            val minutes = (elapsed / 1000 / 60).toInt()
            val seconds = (elapsed / 1000 % 60).toInt()
            val centiseconds = ((elapsed % 1000) / 10).toInt()
            binding.generalChronometer.text = String.format("%02d:%02d:%02d", minutes, seconds, centiseconds)
            handler.postDelayed(this, 10)
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityRoutineBinding.inflate(layoutInflater)
        setContentView(binding.root)

        intent.getParcelableExtra<DailyRoutine>("routine")?.let { routine ->
            binding.tvDay.text = routine.day
            adapter = ExerciseAdapter(routine.exercises)
            binding.tvExercises.layoutManager = LinearLayoutManager(this)

            binding.tvExercises.adapter = adapter

            binding.tvExercises.post {
                val layoutManager = binding.tvExercises.layoutManager ?: return@post
                val itemCount = adapter.itemCount

                for (i in 0 until itemCount) {
                    val view = layoutManager.findViewByPosition(i) ?: continue
                    val animation = android.view.animation.AnimationUtils.loadAnimation(this, R.anim.slide_in_left)
                    animation.startOffset = (i * 100).toLong() // Delay incremental en ms (100 ms entre cada)
                    view.startAnimation(animation)
                }
            }


            totalExercises = routine.exercises.size
            routineExercises = routine.exercises.map { it.name }
            exerciseTimes.clear()
            repeat(totalExercises) { exerciseTimes.add(0L) }
            updateProgressBar()
        } ?: run {
            binding.tvDay.text = "No routine data"
        }

        binding.btnStartPause.setOnClickListener { toggleTimer() }
        binding.btnReset.setOnClickListener { resetRoutine() }
        binding.btnCancel.setOnClickListener { cancelRoutine() }
        binding.btnNextExercise.setOnClickListener { nextExercise() }

        resetUI()
    }

    private fun toggleTimer() {
        if (isRunning) {
            // Pausar ambos cronómetros y guardar tiempos

            val elapsedExercise = SystemClock.elapsedRealtime() - startTimeExercise
            exerciseTimes[currentExerciseIndex] += elapsedExercise
            pauseOffsetExercise += elapsedExercise
            handler.removeCallbacks(updateExerciseTimerRunnable)

            val elapsedGeneral = SystemClock.elapsedRealtime() - startTimeGeneral
            pauseOffsetGeneral += elapsedGeneral
            handler.removeCallbacks(updateGeneralTimerRunnable)

            binding.btnStartPause.text = "Resume"
        } else {
            // Iniciar ambos cronómetros
            startTimeExercise = SystemClock.elapsedRealtime()
            handler.post(updateExerciseTimerRunnable)

            startTimeGeneral = SystemClock.elapsedRealtime()
            handler.post(updateGeneralTimerRunnable)

            binding.btnStartPause.text = "Pause"
        }
        isRunning = !isRunning
    }

    private fun resetRoutine() {
        handler.removeCallbacks(updateExerciseTimerRunnable)
        handler.removeCallbacks(updateGeneralTimerRunnable)

        pauseOffsetExercise = 0L
        pauseOffsetGeneral = 0L

        binding.chronometer.text = "00:00:00"
        binding.generalChronometer.text = "00:00:00"
        binding.btnStartPause.text = "Start"
        isRunning = false

        currentExerciseIndex = 0
        exerciseTimes.replaceAll { 0L }
        updateProgressBar()
        adapter.reset()
        binding.tvExercises.scrollToPosition(0)

        resetUI()
    }

    private fun cancelRoutine() {
        handler.removeCallbacks(updateExerciseTimerRunnable)
        handler.removeCallbacks(updateGeneralTimerRunnable)
        finish()
    }

    private fun nextExercise() {
        if (!isRunning) return

        val elapsedExercise = SystemClock.elapsedRealtime() - startTimeExercise
        exerciseTimes[currentExerciseIndex] += elapsedExercise

        if (currentExerciseIndex < totalExercises - 1) {
            currentExerciseIndex++
            adapter.nextExercise()
            binding.tvExercises.smoothScrollToPosition(currentExerciseIndex)
            pauseOffsetExercise = 0L
            startTimeExercise = SystemClock.elapsedRealtime()
            updateProgressBar()
            if (currentExerciseIndex == totalExercises - 1) {
                binding.btnNextExercise.text = "Finalizar"
            }
        } else if (currentExerciseIndex == totalExercises - 1) {
            completeRoutine()
        }
    }

    private fun completeRoutine() {
        if (isRunning) {
            val elapsedExercise = SystemClock.elapsedRealtime() - startTimeExercise
            exerciseTimes[currentExerciseIndex] += elapsedExercise
        }

        currentExerciseIndex++
        updateProgressBar(complete = true)

        binding.btnNextExercise.apply {
            isEnabled = false
            text = "Completado"
            setBackgroundColor(ContextCompat.getColor(context, android.R.color.darker_gray))
            alpha = 0.5f
        }

        handler.removeCallbacks(updateExerciseTimerRunnable)
        handler.removeCallbacks(updateGeneralTimerRunnable)
        isRunning = false
        binding.btnStartPause.text = "Start"
    }

    private fun updateProgressBar(complete: Boolean = false) {
        val progress = when {
            complete -> 100
            currentExerciseIndex >= totalExercises -> 100
            else -> (currentExerciseIndex * 100) / totalExercises
        }

        ObjectAnimator.ofInt(binding.progressBar, "progress", binding.progressBar.progress, progress).apply {
            duration = 500
            addListener(object : android.animation.Animator.AnimatorListener {
                override fun onAnimationEnd(animation: android.animation.Animator) {
                    if (progress == 100) {
                        animateProgressBarCompletion()
                        handler.postDelayed({ showSummaryDialog() }, 1000)
                    }
                }
                override fun onAnimationStart(animation: android.animation.Animator) {}
                override fun onAnimationCancel(animation: android.animation.Animator) {}
                override fun onAnimationRepeat(animation: android.animation.Animator) {}
            })
            start()
        }
    }

    private fun animateProgressBarCompletion() {
        val progressDrawable: Drawable = binding.progressBar.progressDrawable.mutate()
        progressDrawable.setColorFilter(
            ContextCompat.getColor(this, R.color.green),
            PorterDuff.Mode.SRC_IN
        )
        binding.progressBar.progressDrawable = progressDrawable

        val scaleXUp = ObjectAnimator.ofFloat(binding.progressBar, "scaleX", 1f, 1.2f)
        val scaleYUp = ObjectAnimator.ofFloat(binding.progressBar, "scaleY", 1f, 1.2f)
        val scaleXDown = ObjectAnimator.ofFloat(binding.progressBar, "scaleX", 1.2f, 1f)
        val scaleYDown = ObjectAnimator.ofFloat(binding.progressBar, "scaleY", 1.2f, 1f)

        AnimatorSet().apply {
            playSequentially(
                AnimatorSet().apply { playTogether(scaleXUp, scaleYUp); duration = 100 },
                AnimatorSet().apply { playTogether(scaleXDown, scaleYDown); duration = 100 }
            )
            start()
        }
    }

    private fun showSummaryDialog() {
        val dialogBinding = CustomDialogTrainingSummaryBinding.inflate(LayoutInflater.from(this))

        dialogBinding.llExerciseList.removeAllViews()
        routineExercises.forEachIndexed { index, exerciseName ->
            val timeMs = exerciseTimes.getOrNull(index) ?: 0L
            val timeText = formatTime(timeMs)
            val itemView = TextView(this).apply {
                text = "$exerciseName - $timeText"
                setPadding(16, 16, 16, 16)
                textSize = 16f
            }
            dialogBinding.llExerciseList.addView(itemView)
        }

        // Obtener el tiempo total del cronómetro general (como texto ya formateado)
        val totalTimeText = binding.generalChronometer.text.toString()
        dialogBinding.tvTotalTime.text = "Tiempo total de sesión: $totalTimeText"

        dialogBinding.tvCompleted.text = "Entrenamiento completado"
        dialogBinding.tvCompleted.setTextColor(ContextCompat.getColor(this, R.color.green))

        val dialog = AlertDialog.Builder(this)
            .setView(dialogBinding.root)
            .setCancelable(false)
            .create()

        dialogBinding.btnClose.setOnClickListener {
            dialog.dismiss()
            goToMainMenu()
        }

        dialog.show()
    }


    private fun formatTime(milliseconds: Long): String {
        val totalSeconds = milliseconds / 1000
        val minutes = totalSeconds / 60
        val seconds = totalSeconds % 60
        val centiseconds = ((milliseconds % 1000) / 10).toInt()
        return String.format("%02d:%02d:%02d", minutes, seconds, centiseconds)
    }

    private fun goToMainMenu() {
        val intent = Intent(this, MenuActivity::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        startActivity(intent)
        finish()
    }

    private fun resetUI() {
        binding.btnNextExercise.text = "Siguiente"
        binding.btnNextExercise.isEnabled = true
        binding.btnNextExercise.alpha = 1f
        binding.progressBar.progress = 0

        ContextCompat.getDrawable(this, R.color.yellow)
    }
}
