package com.example.proyecto

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.View
import android.view.animation.DecelerateInterpolator
import android.widget.Toast
import com.example.proyecto.Language_Theme.BaseActivity
import com.example.proyecto.databinding.ActivityLogInBinding
import com.google.firebase.auth.FirebaseAuth

class LoginActivity : BaseActivity() {

    private lateinit var binding: ActivityLogInBinding
    private lateinit var firebaseAuth: FirebaseAuth
    private val prefs by lazy { getSharedPreferences("user_prefs", MODE_PRIVATE) }

    private val minLoadingTime = 2000L
    private var loginCompleted = false
    private var loadingStartedAt = 0L

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        firebaseAuth = FirebaseAuth.getInstance()

        val keepLoggedIn = prefs.getBoolean("keep_logged_in", false)

        if (firebaseAuth.currentUser != null && keepLoggedIn) {
            startActivity(Intent(this, MenuActivity::class.java))
            finish()
            return
        }

        binding = ActivityLogInBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.keepLoggedInCheckbox.isChecked = keepLoggedIn

        val loginCard = binding.loginCardView
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

        binding.loginButton.setOnClickListener {
            val email = binding.loginEmail.text.toString()
            val password = binding.password.text.toString()

            if (email.isNotEmpty() && password.isNotEmpty()) {
                showLoadingOverlay()
                loginCompleted = false
                loadingStartedAt = System.currentTimeMillis()

                firebaseAuth.signInWithEmailAndPassword(email, password).addOnCompleteListener {
                    loginCompleted = true
                    val elapsed = System.currentTimeMillis() - loadingStartedAt
                    val remaining = minLoadingTime - elapsed

                    if (remaining > 0) {
                        Handler(Looper.getMainLooper()).postDelayed({
                            hideLoadingOverlay()
                            processLoginResult(it.isSuccessful)
                        }, remaining)
                    } else {
                        hideLoadingOverlay()
                        processLoginResult(it.isSuccessful)
                    }
                }
            } else {
                Toast.makeText(this, getString(R.string.fields_cannot_be_empty), Toast.LENGTH_SHORT).show()
            }
        }

        binding.notYetRegistered.setOnClickListener {
            val signUpIntent = Intent(this, SingUpActivity::class.java)
            startActivity(signUpIntent)
            finish()
        }

        binding.resetPassword.setOnClickListener {
            val resetIntent = Intent(this, ResetPassActivity::class.java)
            startActivity(resetIntent)
        }
    }

    private fun processLoginResult(success: Boolean) {
        if (success) {
            // Guardar preferencia del checkbox al hacer login
            prefs.edit().putBoolean("keep_logged_in", binding.keepLoggedInCheckbox.isChecked).apply()

            val intent = Intent(this, MenuActivity::class.java)
            startActivity(intent)
            finish()
        } else {
            Toast.makeText(this, getString(R.string.incorrect_email_password), Toast.LENGTH_SHORT).show()
        }
    }

    private fun showLoadingOverlay() {
        binding.loadingOverlay.apply {
            visibility = View.VISIBLE
            animate().alpha(1f).setDuration(200).start()
        }
        binding.lottieAnimationView.apply {
            playAnimation()
            visibility = View.VISIBLE
        }
    }

    private fun hideLoadingOverlay() {
        binding.loadingOverlay.animate()
            .alpha(0f)
            .setDuration(200)
            .withEndAction {
                binding.loadingOverlay.visibility = View.GONE
                binding.lottieAnimationView.cancelAnimation()
                binding.lottieAnimationView.visibility = View.GONE
            }.start()
    }
}

