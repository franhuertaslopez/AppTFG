package com.example.proyecto.Authentication

import android.content.Intent
import android.os.Bundle
import android.view.animation.AnimationUtils
import android.view.animation.DecelerateInterpolator
import com.example.proyecto.Language_Theme.BaseActivity
import com.example.proyecto.R
import com.example.proyecto.databinding.ActivityResetPassBinding
import com.google.firebase.auth.FirebaseAuth

class ResetPassActivity : BaseActivity() {

    private lateinit var binding: ActivityResetPassBinding
    private lateinit var firebaseAuth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {

        super.onCreate(savedInstanceState)
        binding = ActivityResetPassBinding.inflate(layoutInflater)
        setContentView(binding.root)

        firebaseAuth = FirebaseAuth.getInstance()

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

        sendButton.setOnClickListener {
            val email = emailField.text.toString()

            if (email.isNotEmpty()) {
                firebaseAuth.sendPasswordResetEmail(email).addOnCompleteListener {
                    if (it.isSuccessful) {
                        val intent = Intent(this, ResetPassAnimationActivity::class.java)
                        startActivity(intent)
                    }
                }
            } else {
                emailField.startAnimation(shake)

                emailField.setText("")
                emailField.hint = getString(R.string.reset_pass_email)
                emailField.setHintTextColor(resources.getColor(R.color.error_red, null))
            }

            emailField.setOnClickListener {
                emailField.hint = getString(R.string.email)
                emailField.setHintTextColor(resources.getColor(R.color.default_hint, null))
            }
        }

        backToLoginButton.setOnClickListener {
            val intent = Intent(this, LoginActivity::class.java)
            startActivity(intent)
            finish()
        }
    }
}
