package com.example.proyecto.Authentication

import android.content.Intent
import android.os.Bundle
import android.view.animation.DecelerateInterpolator
import android.widget.Toast
import com.example.proyecto.Language_Theme.BaseActivity
import com.example.proyecto.R
import com.example.proyecto.databinding.ActivitySingUpBinding
import com.google.firebase.auth.FirebaseAuth
import java.util.*

class SingUpActivity : BaseActivity() {

    private lateinit var binding: ActivitySingUpBinding
    private lateinit var firebaseAuth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {

        super.onCreate(savedInstanceState)
        binding = ActivitySingUpBinding.inflate(layoutInflater)
        setContentView(binding.root)

        firebaseAuth = FirebaseAuth.getInstance()

        val loginCard = binding.singUpCardView
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

        binding.singUpButton.setOnClickListener {
            val email = binding.singUpEmail.text.toString()
            val password = binding.singUpPassword.text.toString()
            val confirmPasword = binding.singUpConfirm.text.toString()

            if (email.isNotEmpty() && password.isNotEmpty() && confirmPasword.isNotEmpty()) {
                if (password == confirmPasword) {

                    firebaseAuth.createUserWithEmailAndPassword(email, password).addOnCompleteListener {
                        if (it.isSuccessful) {
                            val intent = Intent(this, LoginActivity::class.java)
                            startActivity(intent)
                        } else {
                            Toast.makeText(this, it.exception.toString(), Toast.LENGTH_SHORT).show()
                        }
                    }
                } else {
                    Toast.makeText(this, getString(R.string.password_dissmatch), Toast.LENGTH_SHORT).show()
                }
            } else {
                Toast.makeText(this, getString(R.string.fields_cannot_be_empty), Toast.LENGTH_SHORT).show()
            }
        }
        binding.notYetRegistered.setOnClickListener {
            val loginIntent = Intent(this, LoginActivity::class.java)
            startActivity(loginIntent)
            finish()
        }
    }
}
