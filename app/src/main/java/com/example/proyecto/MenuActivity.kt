package com.example.proyecto

import android.animation.AnimatorSet
import android.animation.ObjectAnimator
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.MenuItem
import androidx.fragment.app.Fragment
import com.example.proyecto.databinding.MenuActivityBinding
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.android.material.navigation.NavigationView

class MenuActivity : AppCompatActivity(), NavigationView.OnNavigationItemSelectedListener {

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
            val fragment = when (menuItem.itemId) {
                R.id.bottom_home -> HomeFragment()
                R.id.bottom_profile -> ProfileFragment()
                R.id.bottom_settings -> SettingsFragment()
                R.id.bottom_logOut -> LogOutFragment()
                else -> null
            }
            fragment?.let {
                replaceFragment(it)
            }
            true
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
        TODO("Not yet implemented")
    }
}
