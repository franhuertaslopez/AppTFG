package com.example.proyecto.TrainingPlanActivities

import android.content.Intent
import android.os.Bundle
import android.widget.RadioButton
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.proyecto.Data.BaseTrainingPlans
import com.example.proyecto.Model.DailyRoutine
import com.example.proyecto.R
import com.example.proyecto.databinding.ActivityTrainingPlanBinding
import java.time.LocalDate
import java.time.format.TextStyle
import java.util.Locale

class TrainingPlanActivity : AppCompatActivity() {

    private lateinit var binding: ActivityTrainingPlanBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityTrainingPlanBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.btnShowRoutine.setOnClickListener {
            val selectedLevelId = binding.rgLevels.checkedRadioButtonId
            val selectedGoalId = binding.rgGoals.checkedRadioButtonId

            if (selectedLevelId == -1 || selectedGoalId == -1) {
                Toast.makeText(this, "Por favor selecciona tu nivel y objetivo", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            // Obtén el texto del botón seleccionado
            val selectedLevelText = findViewById<android.widget.RadioButton>(selectedLevelId).text.toString()
            val selectedGoalText = findViewById<android.widget.RadioButton>(selectedGoalId).text.toString()

            // Mapea los textos al formato esperado
            val level = when (selectedLevelText.lowercase()) {
                "principiante" -> "beginner"
                "intermedio" -> "intermediate"
                "avanzado" -> "advanced"
                else -> ""
            }

            val goal = when (selectedGoalText.lowercase()) {
                "pérdida de peso" -> "weight_loss"
                "ganancia muscular" -> "muscle_gain"
                "mantenimiento" -> "maintenance"
                else -> ""
            }

            // Día actual en inglés (ej: "Monday")
            val currentDay = java.time.LocalDate.now()
                .dayOfWeek
                .getDisplayName(java.time.format.TextStyle.FULL, java.util.Locale.ENGLISH)
                .replaceFirstChar { it.uppercaseChar() }

            val fullPlan = BaseTrainingPlans.getTrainingPlan(level, goal)
            val todayRoutine = fullPlan.find { it.day == currentDay }

            if (todayRoutine != null) {
                val intent = Intent(this, RoutineActivity::class.java)
                intent.putExtra("routine", todayRoutine)
                startActivity(intent)
            } else {
                Toast.makeText(this, "Hoy no tienes entrenamiento asignado ($currentDay)", Toast.LENGTH_SHORT).show()
            }
        }
    }
}
