package com.example.proyecto

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.animation.AnimationUtils
import android.view.animation.DecelerateInterpolator
import com.example.proyecto.databinding.ActivityResetPassBinding
import com.google.firebase.auth.FirebaseAuth

class ResetPassActivity : AppCompatActivity() {

    private lateinit var binding: ActivityResetPassBinding
    private lateinit var firebaseAuth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityResetPassBinding.inflate(layoutInflater)
        setContentView(binding.root)

        firebaseAuth = FirebaseAuth.getInstance()

        //Animación pop_up
        val loginCard = binding.resetCardView
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

        val emailField = binding.accountEmail
        val sendButton = binding.sendEmailButton
        val backToLoginButton = binding.backToLoginButton

        val shake = AnimationUtils.loadAnimation(this, R.anim.shake)

        sendButton.setOnClickListener{

            val email = emailField.text.toString()

            if (email.isNotEmpty()){

                firebaseAuth.sendPasswordResetEmail(email).addOnCompleteListener{
                    if (it.isSuccessful){
                        val intent = Intent(this, ResetPassAnimationActivity::class.java)
                        startActivity(intent)
                    }
                }
            } else {
                emailField.startAnimation(shake)

                emailField.setText("") // Por si el usuario escribió espacios
                emailField.hint = "Please enter your email"
                emailField.setHintTextColor(resources.getColor(R.color.error_red, null))

                //Toast.makeText(this, "Fields cannot be empty", Toast.LENGTH_SHORT).show()
            }

            emailField.setOnClickListener {
                emailField.hint = "E-mail"
                emailField.setHintTextColor(resources.getColor(R.color.default_hint, null)) // Color habitual
            }
        }

        backToLoginButton.setOnClickListener{
            val intent = Intent(this, LoginActivity::class.java)
            startActivity(intent)
            finish()
        }
    }
}