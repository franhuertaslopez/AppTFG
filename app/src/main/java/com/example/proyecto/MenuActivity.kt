package com.example.proyecto

import android.animation.AnimatorSet
import android.animation.ObjectAnimator
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.MenuItem
import android.view.View
import android.widget.Button
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.Fragment
import com.example.proyecto.Authentication.LoginActivity
import com.example.proyecto.MainMenuFragments.HomeFragment
import com.example.proyecto.MainMenuFragments.ProfileFragment
import com.example.proyecto.MainMenuFragments.SettingsFragment
import com.example.proyecto.Language_Theme.BaseActivity
import com.example.proyecto.databinding.MenuActivityBinding
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.android.material.navigation.NavigationView
import com.google.firebase.auth.FirebaseAuth

class MenuActivity : BaseActivity(), NavigationView.OnNavigationItemSelectedListener {

    private lateinit var binding: MenuActivityBinding
    private var pulseAnimatorSet: AnimatorSet? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = MenuActivityBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Mostrar Home al iniciar
        if (savedInstanceState == null) {
            binding.bottomNavigation.selectedItemId = R.id.bottom_home
            replaceFragment(HomeFragment())
        }

        setupBottomNavigation()
        binding.floatingActionButton.setOnClickListener {
            animateFab(binding.floatingActionButton)
        }

        setupBorderPulseAnimation()
    }

    private fun setupBottomNavigation() {
        binding.bottomNavigation.setOnItemSelectedListener { item ->
            when (item.itemId) {
                R.id.bottom_home -> replaceFragment(HomeFragment())
                R.id.bottom_profile -> replaceFragment(ProfileFragment())
                R.id.bottom_settings -> replaceFragment(SettingsFragment())
                R.id.bottom_logOut -> {
                    showLogoutConfirmationDialog()
                    false
                }
                else -> false
            }
        }
    }

    private fun replaceFragment(fragment: Fragment): Boolean {
        supportFragmentManager.beginTransaction()
            .replace(R.id.fragment_container, fragment)
            .commit()
        return true
    }

    private fun animateFab(fab: FloatingActionButton) {
        val jumpHeight = -100f

        val jumpUp = ObjectAnimator.ofFloat(fab, View.TRANSLATION_Y, 0f, jumpHeight).apply {
            duration = 300
        }

        val jumpDown = ObjectAnimator.ofFloat(fab, View.TRANSLATION_Y, jumpHeight, 0f).apply {
            duration = 200
        }

        val rotateY = ObjectAnimator.ofFloat(fab, View.ROTATION_Y, 0f, 360f).apply {
            duration = 500
        }

        AnimatorSet().apply {
            playTogether(AnimatorSet().apply {
                playSequentially(jumpUp, jumpDown)
            }, rotateY)

            addListener(object : android.animation.Animator.AnimatorListener {
                override fun onAnimationEnd(animation: android.animation.Animator) {
                    startActivity(Intent(this@MenuActivity, ContentRecyclerViewActivity::class.java))
                    finish()
                }

                override fun onAnimationStart(animation: android.animation.Animator) {}
                override fun onAnimationCancel(animation: android.animation.Animator) {}
                override fun onAnimationRepeat(animation: android.animation.Animator) {}
            })

            start()
        }
    }

    private fun showLogoutConfirmationDialog() {
        val dialogView = layoutInflater.inflate(R.layout.custom_dialog_logout, null)
        val dialog = AlertDialog.Builder(this)
            .setView(dialogView)
            .create()

        dialogView.findViewById<Button>(R.id.btnCancel).setOnClickListener { dialog.dismiss() }
        dialogView.findViewById<Button>(R.id.btnConfirm).setOnClickListener {
            logout()
            dialog.dismiss()
        }

        dialog.show()
    }

    private fun logout() {
        FirebaseAuth.getInstance().signOut()

        getSharedPreferences("user_prefs", Context.MODE_PRIVATE)
            .edit()
            .putBoolean("keep_logged_in", false)
            .apply()

        startActivity(Intent(this, LoginActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        })
        finish()
    }

    private fun setupBorderPulseAnimation() {
        val view = binding.animatedBorder

        val scaleX = ObjectAnimator.ofFloat(view, View.SCALE_X, 1f, 1.1f).apply {
            duration = 1000
            repeatCount = ObjectAnimator.INFINITE
            repeatMode = ObjectAnimator.REVERSE
        }

        val scaleY = ObjectAnimator.ofFloat(view, View.SCALE_Y, 1f, 1.1f).apply {
            duration = 1000
            repeatCount = ObjectAnimator.INFINITE
            repeatMode = ObjectAnimator.REVERSE
        }

        pulseAnimatorSet = AnimatorSet().apply {
            playTogether(scaleX, scaleY)
        }
    }

    fun showBorderPulse() {
        val view = binding.animatedBorder
        if (view.visibility != View.VISIBLE) {
            view.visibility = View.VISIBLE
            pulseAnimatorSet?.start()
        }
    }

    fun hideBorderPulse() {
        val view = binding.animatedBorder
        if (view.visibility == View.VISIBLE) {
            pulseAnimatorSet?.cancel()
            view.visibility = View.GONE
        }
    }

    override fun onNavigationItemSelected(item: MenuItem): Boolean = false
}
