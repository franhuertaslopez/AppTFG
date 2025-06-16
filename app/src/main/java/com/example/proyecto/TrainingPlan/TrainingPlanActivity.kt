package com.example.proyecto.TrainingPlan

import android.content.Intent
import android.os.Bundle
import android.view.animation.Animation
import android.view.animation.AnimationUtils
import android.widget.RadioButton
import android.widget.Toast
import com.example.proyecto.Data.BaseTrainingPlans
import com.example.proyecto.Language_Theme.BaseActivity
import com.example.proyecto.R
import com.example.proyecto.databinding.ActivityTrainingPlanBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import java.time.LocalDate
import java.time.format.TextStyle
import java.util.Locale

class TrainingPlanActivity : BaseActivity() {

    private lateinit var binding: ActivityTrainingPlanBinding
    private lateinit var auth: FirebaseAuth
    private lateinit var firestore: FirebaseFirestore

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityTrainingPlanBinding.inflate(layoutInflater)
        setContentView(binding.root)

        auth = FirebaseAuth.getInstance()
        firestore = FirebaseFirestore.getInstance()

        // Animaciones
        val slideInLeft = AnimationUtils.loadAnimation(this, R.anim.slide_in_left)
        val zoomIn = AnimationUtils.loadAnimation(this, R.anim.button_zoom) // tu animación de zoom_in.xml

        // Animar levelChooser
        binding.levelChooser.startAnimation(slideInLeft)

        // Animar goalChooser, y al acabar lanzar animación del botón
        slideInLeft.setAnimationListener(object : Animation.AnimationListener {
            override fun onAnimationStart(animation: Animation?) {}

            override fun onAnimationEnd(animation: Animation?) {
                // Cuando termine slideInLeft (última animación), animamos el botón
                binding.btnShowRoutine.startAnimation(zoomIn)
                binding.btnBackRoutine.startAnimation(zoomIn)
                // Opcional: hacer visible el botón aquí si está oculto inicialmente
                binding.btnShowRoutine.alpha = 1f
                binding.btnBackRoutine.alpha = 1f
            }

            override fun onAnimationRepeat(animation: Animation?) {}
        })

        binding.goalChooser.startAnimation(slideInLeft)

        // Opcional: esconder el botón inicialmente (si quieres que "aparezca" con la animación)
        binding.btnShowRoutine.alpha = 0f
        binding.btnBackRoutine.alpha = 0f

        loadUserTrainingPreferences()

        binding.btnShowRoutine.setOnClickListener {
            val selectedLevelId = binding.rgLevels.checkedRadioButtonId
            val selectedGoalId = binding.rgGoals.checkedRadioButtonId

            if (selectedLevelId == -1 || selectedGoalId == -1) {
                Toast.makeText(this, "Por favor selecciona tu nivel y objetivo", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            val selectedLevelText = binding.root.findViewById<RadioButton>(selectedLevelId).text.toString()
            val selectedGoalText = binding.root.findViewById<RadioButton>(selectedGoalId).text.toString()

            val level = when (selectedLevelText.lowercase()) {
                "principiante" -> "beginner"
                "intermedio" -> "intermediate"
                "avanzado" -> "advanced"
                else -> ""
            }

            val goal = when (selectedGoalText.lowercase()) {
                "pérdida de peso" -> "weight_loss_goal"
                "ganancia muscular" -> "muscle_gain_goal"
                "mantenimiento" -> "maintenance_goal"
                else -> ""
            }

            val currentDay = LocalDate.now()
                .dayOfWeek
                .getDisplayName(TextStyle.FULL, Locale.ENGLISH)
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

        binding.btnBackRoutine.setOnClickListener{
            finish()
        }
    }

    private fun loadUserTrainingPreferences() {
        val user = auth.currentUser
        if (user == null) {
            Toast.makeText(this, "Usuario no autenticado", Toast.LENGTH_SHORT).show()
            return
        }

        firestore.collection("users").document(user.uid).get()
            .addOnSuccessListener { doc ->
                if (doc != null && doc.exists()) {
                    val goalKey = doc.getString("goal") ?: ""
                    val levelKey = doc.getString("level") ?: ""

                    val goalRadioButtonId = when (goalKey) {
                        "weight_loss_goal" -> binding.rbWeightLoss.id
                        "muscle_gain_goal" -> binding.rbMuscleGain.id
                        "maintenance_goal" -> binding.rbMaintenance.id
                        else -> -1
                    }

                    val levelRadioButtonId = when (levelKey) {
                        "beginner" -> binding.rbBeginner.id
                        "intermediate" -> binding.rbIntermediate.id
                        "advanced" -> binding.rbAdvanced.id
                        else -> -1
                    }

                    if (goalRadioButtonId != -1) {
                        binding.rgGoals.check(goalRadioButtonId)
                    }
                    if (levelRadioButtonId != -1) {
                        binding.rgLevels.check(levelRadioButtonId)
                    }
                } else {
                    Toast.makeText(this, "No se encontraron datos del usuario", Toast.LENGTH_SHORT).show()
                }
            }
            .addOnFailureListener { e ->
                Toast.makeText(this, "Error al cargar datos: ${e.message}", Toast.LENGTH_SHORT).show()
            }
    }
}
