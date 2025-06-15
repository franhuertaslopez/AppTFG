package com.example.proyecto.MainMenuFragments

import android.annotation.SuppressLint
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.GestureDetector
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import android.view.animation.AccelerateDecelerateInterpolator
import android.view.animation.AnimationUtils
import android.widget.Toast
import androidx.cardview.widget.CardView
import androidx.fragment.app.Fragment
import com.example.proyecto.R
import com.example.proyecto.databinding.FragmentHomeBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

class HomeFragment : Fragment() {

    private var _binding: FragmentHomeBinding? = null
    private val binding get() = _binding!!

    private lateinit var cards: List<CardView>

    private val handler = Handler(Looper.getMainLooper())
    private var currentIndex = 0

    companion object {
        private var savedIndex = 0
        private var hasAnimated = false
    }

    private lateinit var messages: List<String>
    private lateinit var gestureDetector: GestureDetector

    private var selectedCardIndex: Int? = null

    private val delayMillis: Long = 4000

    private val bannerRunnable = object : Runnable {
        override fun run() {
            animateBannerTextForward()
            handler.postDelayed(this, delayMillis)
        }
    }

    @SuppressLint("ClickableViewAccessibility")
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentHomeBinding.inflate(inflater, container, false)
        val rootView = binding.root

        cards = listOf(
            binding.cardChallenge,
            binding.cardAchievements,
            binding.cardFocus,
            binding.cardChecklist
        )

        loadUserName()
        initBannerMessages()
        initGestureDetection()
        setupCardClickListeners()
        setupOutsideClickToResetCards(rootView)

        if (!hasAnimated) {
            animateCards()
            hasAnimated = true
        } else {
            cards.forEach {
                it.alpha = 1f
                it.translationX = 0f
                it.scaleX = 1f
                it.scaleY = 1f
            }
        }

        return rootView
    }

    private fun loadUserName() {
        val uid = FirebaseAuth.getInstance().currentUser?.uid
        if (uid != null) {
            val db = FirebaseFirestore.getInstance()
            db.collection("users").document(uid).get()
                .addOnSuccessListener { document ->
                    val name = document?.getString("name").orEmpty()
                    binding.welcomeText.text = getString(R.string.welcome_user, name.ifEmpty { "Usuario" })
                }
                .addOnFailureListener {
                    binding.welcomeText.text = getString(R.string.welcome_user, "Usuario")
                }
        } else {
            binding.welcomeText.text = getString(R.string.welcome_user, "Usuario")
        }
    }

    private fun initBannerMessages() {
        messages = listOf(
            getString(R.string.banner_message_1),
            getString(R.string.banner_message_2),
            getString(R.string.banner_message_3),
            getString(R.string.banner_message_4),
            getString(R.string.banner_message_5),
            getString(R.string.banner_message_6)
        )

        currentIndex = savedIndex

        binding.bannerText.post {
            binding.bannerText.text = messages[currentIndex]
            handler.postDelayed(bannerRunnable, delayMillis)
        }
    }

    @SuppressLint("ClickableViewAccessibility")
    private fun initGestureDetection() {
        gestureDetector = GestureDetector(requireContext(), object : GestureDetector.SimpleOnGestureListener() {
            private val SWIPE_THRESHOLD = 100
            private val SWIPE_VELOCITY_THRESHOLD = 100

            override fun onFling(e1: MotionEvent?, e2: MotionEvent, velocityX: Float, velocityY: Float): Boolean {
                if (e1 == null) return false
                val diffX = e2.x - e1.x
                val diffY = e2.y - e1.y

                if (kotlin.math.abs(diffX) > kotlin.math.abs(diffY) &&
                    kotlin.math.abs(diffX) > SWIPE_THRESHOLD &&
                    kotlin.math.abs(velocityX) > SWIPE_VELOCITY_THRESHOLD
                ) {
                    if (diffX > 0) onSwipeRight() else onSwipeLeft()
                    return true
                }
                return false
            }
        })

        binding.bannerText.setOnTouchListener { _, event ->
            gestureDetector.onTouchEvent(event)
            true
        }
    }

    private fun setupCardClickListeners() {
        cards.forEachIndexed { index, card ->
            card.setOnClickListener {
                if (selectedCardIndex == index) {
                    when (index) {
                        0 -> openChallenge()
                        1 -> openAchievements()
                        2 -> openFocus()
                        3 -> openChecklist()
                    }
                } else {
                    animateCardSelection(index)
                }
            }
        }
    }

    private fun setupOutsideClickToResetCards(rootView: View) {
        rootView.setOnClickListener {
            if (selectedCardIndex != null) resetCardAnimations()
        }
    }

    private fun animateBannerTextForward() {
        val width = binding.bannerText.width.toFloat()
        if (width == 0f) {
            currentIndex = (currentIndex + 1) % messages.size
            binding.bannerText.text = messages[currentIndex]
            return
        }

        binding.bannerText.animate()
            .translationX(-width)
            .alpha(0f)
            .setDuration(500)
            .withEndAction {
                currentIndex = (currentIndex + 1) % messages.size
                binding.bannerText.text = messages[currentIndex]
                binding.bannerText.translationX = width
                binding.bannerText.animate()
                    .translationX(0f)
                    .alpha(1f)
                    .setDuration(500)
                    .setInterpolator(AccelerateDecelerateInterpolator())
                    .start()
            }.start()
    }

    private fun onSwipeLeft() {
        currentIndex = (currentIndex + 1) % messages.size
        animateBannerTextWithDirection(forward = true)
        resetAutoScroll()
    }

    private fun onSwipeRight() {
        currentIndex = if (currentIndex - 1 < 0) messages.size - 1 else currentIndex - 1
        animateBannerTextWithDirection(forward = false)
        resetAutoScroll()
    }

    private fun animateBannerTextWithDirection(forward: Boolean) {
        val width = binding.bannerText.width.toFloat()
        if (width == 0f) {
            binding.bannerText.text = messages[currentIndex]
            return
        }

        val outX = if (forward) -width else width
        val inX = if (forward) width else -width

        binding.bannerText.animate()
            .translationX(outX)
            .alpha(0f)
            .setDuration(300)
            .withEndAction {
                binding.bannerText.text = messages[currentIndex]
                binding.bannerText.translationX = inX
                binding.bannerText.animate()
                    .translationX(0f)
                    .alpha(1f)
                    .setDuration(300)
                    .setInterpolator(AccelerateDecelerateInterpolator())
                    .start()
            }.start()
    }

    private fun resetAutoScroll() {
        handler.removeCallbacks(bannerRunnable)
        handler.postDelayed(bannerRunnable, delayMillis)
    }

    private fun animateCards() {
        cards.forEachIndexed { index, card ->
            card.alpha = 1f
            val anim = AnimationUtils.loadAnimation(requireContext(), R.anim.fade_in)
            anim.startOffset = (index * 150).toLong()
            card.startAnimation(anim)
        }
    }

    private fun animateCardSelection(selectedIndex: Int) {
        selectedCardIndex = selectedIndex
        cards.forEachIndexed { index, card ->
            if (index == selectedIndex) {
                card.animate()
                    .scaleX(1.1f)
                    .scaleY(1.1f)
                    .alpha(1f)
                    .translationY(0f)
                    .setDuration(300)
                    .setInterpolator(AccelerateDecelerateInterpolator())
                    .start()
            } else {
                card.animate()
                    .scaleX(0.9f)
                    .scaleY(0.9f)
                    .alpha(0.5f)
                    .translationY(10f * (index - selectedIndex)) // efecto leve
                    .setDuration(300)
                    .setInterpolator(AccelerateDecelerateInterpolator())
                    .start()
            }
        }
    }

    private fun resetCardAnimations() {
        selectedCardIndex = null
        cards.forEach { card ->
            card.animate()
                .scaleX(1f)
                .scaleY(1f)
                .alpha(1f)
                .translationY(0f)
                .setDuration(300)
                .setInterpolator(AccelerateDecelerateInterpolator())
                .start()
        }
    }

    private fun openChallenge() {
        Toast.makeText(requireContext(), "Abrir Challenge", Toast.LENGTH_SHORT).show()
    }

    private fun openAchievements() {
        Toast.makeText(requireContext(), "Abrir Achievements", Toast.LENGTH_SHORT).show()
    }

    private fun openFocus() {
        Toast.makeText(requireContext(), "Abrir Focus", Toast.LENGTH_SHORT).show()
    }

    private fun openChecklist() {
        Toast.makeText(requireContext(), "Abrir Checklist", Toast.LENGTH_SHORT).show()
    }

    override fun onPause() {
        super.onPause()
        savedIndex = currentIndex
        handler.removeCallbacks(bannerRunnable)
    }

    override fun onResume() {
        super.onResume()
        handler.postDelayed(bannerRunnable, delayMillis)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        handler.removeCallbacks(bannerRunnable)
        _binding = null
    }
}
