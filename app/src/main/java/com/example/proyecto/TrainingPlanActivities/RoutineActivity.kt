package com.example.proyecto.TrainingPlanActivities

import android.os.Bundle
import android.os.Handler
import android.os.SystemClock
import androidx.appcompat.app.AppCompatActivity
import com.example.proyecto.Model.DailyRoutine
import com.example.proyecto.databinding.ActivityRoutineBinding

class RoutineActivity : AppCompatActivity() {

    private lateinit var binding: ActivityRoutineBinding

    private var isRunning = false
    private var startTime = 0L
    private var pauseOffset = 0L
    private val handler = Handler()

    private val updateTimerRunnable = object : Runnable {
        override fun run() {
            val elapsed = SystemClock.elapsedRealtime() - startTime + pauseOffset
            val totalSeconds = (elapsed / 1000).toInt()
            val minutes = totalSeconds / 60
            val seconds = totalSeconds % 60
            val centiseconds = ((elapsed % 1000) / 10).toInt()

            binding.chronometer.text = String.format("%02d:%02d.%02d", minutes, seconds, centiseconds)

            handler.postDelayed(this, 10)
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityRoutineBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val dailyRoutine = intent.getParcelableExtra<DailyRoutine>("routine")

        if (dailyRoutine != null) {
            binding.tvDay.text = dailyRoutine.day
            binding.tvExercises.text = dailyRoutine.exercises.joinToString(separator = "\n") {
                "• ${it.name} - ${it.reps} [${it.targetArea}]"
            }
        } else {
            binding.tvDay.text = "No routine data"
            binding.tvExercises.text = ""
        }

        binding.btnStartPause.setOnClickListener {
            if (isRunning) {
                // Pausar
                pauseOffset += SystemClock.elapsedRealtime() - startTime
                handler.removeCallbacks(updateTimerRunnable)
                binding.btnStartPause.text = "Resume"
                isRunning = false
            } else {
                // Iniciar o reanudar
                startTime = SystemClock.elapsedRealtime()
                handler.post(updateTimerRunnable)
                binding.btnStartPause.text = "Pause"
                isRunning = true
            }
        }

        binding.btnReset.setOnClickListener {
            handler.removeCallbacks(updateTimerRunnable)
            pauseOffset = 0L
            binding.chronometer.text = "00:00.00"
            binding.btnStartPause.text = "Start"
            isRunning = false
        }

        binding.btnCancel.setOnClickListener {
            handler.removeCallbacks(updateTimerRunnable)
            pauseOffset = 0L
            binding.chronometer.text = "00:00.00"
            binding.btnStartPause.text = "Start"
            isRunning = false
            // Aquí añades la acción que cancela la rutina, por ejemplo:
            finish()  // Cierra la actividad
        }
    }
}
