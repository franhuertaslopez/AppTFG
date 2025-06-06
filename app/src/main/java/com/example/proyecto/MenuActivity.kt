package com.example.proyecto

import android.animation.AnimatorSet
import android.animation.ObjectAnimator
import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.MenuItem
import android.widget.Button
import androidx.fragment.app.Fragment
import com.example.proyecto.Language_Theme.BaseActivity
import com.example.proyecto.databinding.MenuActivityBinding
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.android.material.navigation.NavigationView
import com.google.firebase.auth.FirebaseAuth
import java.util.Locale

class MenuActivity : BaseActivity(), NavigationView.OnNavigationItemSelectedListener {

    private lateinit var binding: MenuActivityBinding

    private fun animateFabJumpAndSpin(fab: FloatingActionButton) {
        val originalY = fab.translationY

        fab.animate().cancel()
        fab.clearAnimation()

        val jumpHeight = -100f

        val jumpUp = ObjectAnimator.ofFloat(fab, "translationY", originalY, originalY + jumpHeight)
        val jumpDown = ObjectAnimator.ofFloat(fab, "translationY", originalY + jumpHeight, originalY)
        val rotateY = ObjectAnimator.ofFloat(fab, "rotationY", 0f, 360f)

        jumpUp.duration = 300
        jumpDown.duration = 200
        rotateY.duration = 500

        val jump = AnimatorSet().apply {
            playSequentially(jumpUp, jumpDown)
        }

        val fullAnim = AnimatorSet().apply {
            playTogether(jump, rotateY)
            addListener(object : android.animation.AnimatorListenerAdapter() {
                override fun onAnimationEnd(animation: android.animation.Animator) {
                    fab.translationY = originalY
                    fab.rotationY = 0f

                    val intent = Intent(this@MenuActivity, ContentRecyclerViewActivity::class.java)
                    startActivity(intent)
                    finish()
                }
            })
        }

        fullAnim.start()
    }

    override fun onCreate(savedInstanceState: Bundle?) {

        super.onCreate(savedInstanceState)
        binding = MenuActivityBinding.inflate(layoutInflater)
        setContentView(binding.root)

        if (savedInstanceState == null) {
            binding.bottomNavigation.selectedItemId = R.id.bottom_home
            replaceFragment(HomeFragment())
        }

        binding.bottomNavigation.setOnNavigationItemSelectedListener { menuItem ->
            when (menuItem.itemId) {
                R.id.bottom_home -> {
                    replaceFragment(HomeFragment())
                    true
                }
                R.id.bottom_profile -> {
                    replaceFragment(ProfileFragment())
                    true
                }
                R.id.bottom_settings -> {
                    replaceFragment(SettingsFragment())
                    true
                }
                R.id.bottom_logOut -> {
                    showLogoutConfirmationDialog()
                    false
                }
                else -> false
            }
        }


        binding.floatingActionButton.setOnClickListener {
            animateFabJumpAndSpin(binding.floatingActionButton)
        }
    }

    private fun replaceFragment(fragment: Fragment) {
        supportFragmentManager.beginTransaction()
            .replace(R.id.fragment_container, fragment)
            .commit()
    }

    override fun onNavigationItemSelected(item: MenuItem): Boolean {
        return false
    }

    private fun showLogoutConfirmationDialog() {
        val dialogView = layoutInflater.inflate(R.layout.custom_dialog_logout, null)

        val dialog = AlertDialog.Builder(this)
            .setView(dialogView)
            .create()

        dialogView.findViewById<Button>(R.id.btnCancel).setOnClickListener {
            dialog.dismiss()
        }

        dialogView.findViewById<Button>(R.id.btnConfirm).setOnClickListener {
            logout()
            dialog.dismiss()
        }

        dialog.show()
    }

    private fun logout() {
        FirebaseAuth.getInstance().signOut()

        val prefs = getSharedPreferences("user_prefs", Context.MODE_PRIVATE)
        prefs.edit().putBoolean("keep_logged_in", false).apply()

        val intent = Intent(this, LoginActivity::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        startActivity(intent)
        finish()
    }

}
