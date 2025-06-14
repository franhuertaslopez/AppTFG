package com.example.proyecto.Authentication

import android.app.DatePickerDialog
import android.content.Intent
import android.os.Bundle
import android.view.View
import android.view.animation.DecelerateInterpolator
import android.widget.ArrayAdapter
import android.widget.Toast
import com.example.proyecto.Language_Theme.BaseActivity
import com.example.proyecto.R
import com.example.proyecto.databinding.ActivitySingUpBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import java.util.Calendar

class SignUpActivity : BaseActivity() {

    private lateinit var binding: ActivitySingUpBinding
    private lateinit var firebaseAuth: FirebaseAuth
    private lateinit var firestore: FirebaseFirestore

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySingUpBinding.inflate(layoutInflater)
        setContentView(binding.root)

        firebaseAuth = FirebaseAuth.getInstance()
        firestore = FirebaseFirestore.getInstance()

        setupAnimation()
        setupListeners()
        setupGoalSpinner()
        setupLevelSpinner()  // <--- Añadido setup del spinner nivel
    }

    private fun setupAnimation() {
        val loginCard = binding.signUpCardView
        loginCard.scaleX = 0.8f
        loginCard.scaleY = 0.8f
        loginCard.alpha = 0f
        loginCard.animate()
            .scaleX(1f)
            .scaleY(1f)
            .alpha(1f)
            .setDuration(300)
            .setInterpolator(DecelerateInterpolator())
            .start()
    }

    private fun setupListeners() {
        binding.signUpButton.setOnClickListener {
            clearErrors()
            val userData = getUserInput()

            if (validateInput(userData)) {
                registerUser(userData)
            }
        }

        binding.notYetRegistered.setOnClickListener {
            goToLogin()
        }

        binding.signUpBirthDate.setOnClickListener {
            showDatePicker()
        }
    }

    private fun setupGoalSpinner() {
        val goals = listOf(
            "Ganar músculo y definir abdomen",
            "Aumentar masa muscular sin proteína en polvo",
            "Perder grasa abdominal y ganar hombros"
        )
        val adapter = ArrayAdapter(
            this,
            android.R.layout.simple_spinner_item,
            goals
        )
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        binding.signUpGoal.adapter = adapter

        binding.signUpGoal.setOnItemSelectedListener(object : android.widget.AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: android.widget.AdapterView<*>, view: View?, position: Int, id: Long) {
                val selectedGoal = parent.getItemAtPosition(position).toString()
                Toast.makeText(this@SignUpActivity, "Objetivo seleccionado: $selectedGoal", Toast.LENGTH_SHORT).show()
            }

            override fun onNothingSelected(parent: android.widget.AdapterView<*>) {}
        })
    }

    private fun setupLevelSpinner() {
        val levels = listOf("Principiante", "Intermedio", "Avanzado")
        val adapter = ArrayAdapter(
            this,
            android.R.layout.simple_spinner_item,
            levels
        )
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        binding.signUpLevel.adapter = adapter

        binding.signUpLevel.setOnItemSelectedListener(object : android.widget.AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: android.widget.AdapterView<*>, view: View?, position: Int, id: Long) {
                val selectedLevel = parent.getItemAtPosition(position).toString()
                Toast.makeText(this@SignUpActivity, "Nivel seleccionado: $selectedLevel", Toast.LENGTH_SHORT).show()
            }

            override fun onNothingSelected(parent: android.widget.AdapterView<*>) {}
        })
    }

    private fun clearErrors() {
        // Limpiar errores si estás usando TextInputLayout
    }

    private data class UserData(
        val email: String,
        val password: String,
        val confirmPassword: String,
        val name: String,
        val surname: String,
        val birthDate: String,
        val weight: String,
        val goal: String,
        val level: String  // <-- añadido nivel
    )

    private fun getUserInput(): UserData {
        return UserData(
            email = binding.signUpEmail.text.toString().trim(),
            password = binding.signUpPassword.text.toString().trim(),
            confirmPassword = binding.signUpConfirm.text.toString().trim(),
            name = binding.signUpName.text.toString().trim(),
            surname = binding.signUpSurname.text.toString().trim(),
            birthDate = binding.signUpBirthDate.text.toString().trim(),
            weight = binding.signUpWeight.text.toString().trim(),
            goal = binding.signUpGoal.selectedItem?.toString() ?: "",
            level = binding.signUpLevel.selectedItem?.toString() ?: ""  // <--- recogemos nivel
        )
    }

    private fun validateInput(userData: UserData): Boolean {
        if (userData.email.isEmpty()) {
            showToast("Por favor, introduce tu email")
            return false
        }
        if (!android.util.Patterns.EMAIL_ADDRESS.matcher(userData.email).matches()) {
            showToast("Introduce un email válido")
            return false
        }
        if (userData.password.isEmpty()) {
            showToast("Introduce una contraseña")
            return false
        }
        if (userData.password.length < 6) {
            showToast("La contraseña debe tener al menos 6 caracteres")
            return false
        }
        if (userData.confirmPassword.isEmpty()) {
            showToast("Confirma tu contraseña")
            return false
        }
        if (userData.password != userData.confirmPassword) {
            showToast(getString(R.string.password_dissmatch))
            return false
        }
        if (userData.name.isEmpty()) {
            showToast("Introduce tu nombre")
            return false
        }
        if (userData.surname.isEmpty()) {
            showToast("Introduce tus apellidos")
            return false
        }
        if (userData.birthDate.isEmpty()) {
            showToast("Selecciona tu fecha de nacimiento")
            return false
        }
        if (userData.weight.isEmpty()) {
            showToast("Introduce tu peso")
            return false
        }
        val weightDouble = userData.weight.toDoubleOrNull()
        if (weightDouble == null || weightDouble <= 0) {
            showToast("Introduce un peso válido")
            return false
        }
        if (userData.goal.isEmpty()) {
            showToast("Selecciona un objetivo")
            return false
        }
        if (userData.level.isEmpty()) {
            showToast("Selecciona un nivel")
            return false
        }
        return true
    }

    private fun registerUser(userData: UserData) {
        binding.signUpButton.isEnabled = false

        firebaseAuth.createUserWithEmailAndPassword(userData.email, userData.password)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    val user = firebaseAuth.currentUser
                    val uid = user?.uid

                    if (uid == null) {
                        showToast("Error: usuario no encontrado después del registro")
                        binding.signUpButton.isEnabled = true
                        return@addOnCompleteListener
                    }

                    saveUserDataToFirestore(uid, userData)
                } else {
                    binding.signUpButton.isEnabled = true
                    showToast(task.exception?.localizedMessage ?: "Error al registrar usuario")
                }
            }
    }

    private fun saveUserDataToFirestore(uid: String, userData: UserData) {
        val userMap = hashMapOf(
            "name" to userData.name,
            "surname" to userData.surname,
            "birthDate" to userData.birthDate,
            "weight" to userData.weight.toDouble(),
            "goal" to goalToKey(userData.goal),
            "level" to levelToKey(userData.level),
            "email" to userData.email
        )


        firestore.collection("users").document(uid)
            .set(userMap)
            .addOnSuccessListener {
                sendVerificationEmail()
            }
            .addOnFailureListener { e ->
                binding.signUpButton.isEnabled = true
                showToast("Error al guardar datos: ${e.message}")
            }
    }

    private fun sendVerificationEmail() {
        val user = firebaseAuth.currentUser
        user?.sendEmailVerification()
            ?.addOnCompleteListener { task ->
                binding.signUpButton.isEnabled = true
                if (task.isSuccessful) {
                    Toast.makeText(
                        this,
                        "Correo de verificación enviado. Por favor, revisa tu bandeja de entrada.",
                        Toast.LENGTH_LONG
                    ).show()
                    goToLogin()
                } else {
                    showToast("No se pudo enviar el correo de verificación.")
                }
            }
    }

    private fun goToLogin() {
        val intent = Intent(this, LoginActivity::class.java)
        startActivity(intent)
        finish()
    }

    private fun showToast(message: String) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
    }

    private fun showDatePicker() {
        val calendar = Calendar.getInstance()

        // Obtener la fecha máxima permitida (hoy - 16 años)
        calendar.add(Calendar.YEAR, -16)
        val maxDate = calendar.timeInMillis

        val year = calendar.get(Calendar.YEAR)
        val month = calendar.get(Calendar.MONTH)
        val day = calendar.get(Calendar.DAY_OF_MONTH)

        val datePickerDialog = DatePickerDialog(
            this,
            { _, selectedYear, selectedMonth, selectedDay ->
                val formattedDate = String.format("%02d/%02d/%04d", selectedDay, selectedMonth + 1, selectedYear)
                binding.signUpBirthDate.setText(formattedDate)
            },
            year, month, day
        )

        datePickerDialog.datePicker.maxDate = maxDate
        datePickerDialog.show()
    }

    private fun goalToKey(goal: String): String {
        return when (goal) {
            "Ganar músculo y definir abdomen",
            "Gain muscle and define abs",
            "Gagner du muscle et définir les abdos" -> "gain_muscle_abs"

            "Aumentar masa muscular sin proteína en polvo",
            "Gain mass without protein powder",
            "Augmenter la masse sans protéines en poudre" -> "gain_mass_no_protein"

            "Perder grasa abdominal y ganar hombros",
            "Lose abdominal fat and gain shoulders",
            "Perdre la graisse abdominale et gagner des épaules" -> "lose_fat_gain_shoulders"

            else -> "unknown_goal"
        }
    }

    private fun levelToKey(level: String): String {
        return when (level) {
            "Principiante", "Beginner", "Débutant" -> "beginner"
            "Intermedio", "Intermediate", "Intermédiaire" -> "intermediate"
            "Avanzado", "Advanced", "Avancé" -> "advanced"
            else -> "unknown_level"
        }
    }
}
