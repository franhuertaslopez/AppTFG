package com.example.proyecto.MainMenuFragments

import android.animation.ObjectAnimator
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
import android.widget.ProgressBar
import android.widget.TextView
import android.widget.Toast
import androidx.cardview.widget.CardView
import androidx.fragment.app.Fragment
import com.example.proyecto.R
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

class HomeFragment : Fragment() {

    private lateinit var bannerText: TextView
    private lateinit var welcomeText: TextView
    private lateinit var progressBar: ProgressBar
    private lateinit var cards: List<CardView>

    private val handler = Handler(Looper.getMainLooper())
    private var currentIndex = 0

    companion object {
        private var savedIndex = 0
        private var hasAnimated = false
    }

    private lateinit var messages: List<String>

    private val delayMillis: Long = 4000 // ⏱ Cada 4 segundos

    private val bannerRunnable = object : Runnable {
        override fun run() {
            animateBannerTextForward()
            handler.postDelayed(this, delayMillis)
        }
    }

    private lateinit var gestureDetector: GestureDetector

    private var selectedCardIndex: Int? = null

    @SuppressLint("ClickableViewAccessibility")
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val rootView = inflater.inflate(R.layout.fragment_home, container, false)

        bannerText = rootView.findViewById(R.id.bannerText)
        welcomeText = rootView.findViewById(R.id.welcomeText)
        progressBar = rootView.findViewById(R.id.progressBar)
        cards = listOf(
            rootView.findViewById(R.id.cardChallenge),
            rootView.findViewById(R.id.cardAchievements),
            rootView.findViewById(R.id.cardFocus),
            rootView.findViewById(R.id.cardChecklist)
        )

        val currentUser = FirebaseAuth.getInstance().currentUser
        val uid = currentUser?.uid

        if (uid != null) {
            val db = FirebaseFirestore.getInstance()
            db.collection("users").document(uid).get()
                .addOnSuccessListener { document ->
                    val name = document?.getString("name")
                    if (!name.isNullOrEmpty()) {
                        welcomeText.text = getString(R.string.welcome_user, name)
                    } else {
                        welcomeText.text = getString(R.string.welcome_user, "Usuario")
                    }
                }
                .addOnFailureListener {
                    welcomeText.text = getString(R.string.welcome_user, "Usuario")
                }
        } else {
            welcomeText.text = getString(R.string.welcome_user, "Usuario")
        }

        messages = listOf(
            getString(R.string.banner_message_1),
            getString(R.string.banner_message_2),
            getString(R.string.banner_message_3),
            getString(R.string.banner_message_4),
            getString(R.string.banner_message_5),
            getString(R.string.banner_message_6)
        )

        currentIndex = savedIndex

        bannerText.post {
            bannerText.text = messages[currentIndex]
            handler.postDelayed(bannerRunnable, delayMillis)
        }

        gestureDetector = GestureDetector(requireContext(), object : GestureDetector.SimpleOnGestureListener() {

            private val SWIPE_THRESHOLD = 100
            private val SWIPE_VELOCITY_THRESHOLD = 100

            override fun onFling(
                e1: MotionEvent?,
                e2: MotionEvent,
                velocityX: Float,
                velocityY: Float
            ): Boolean {
                if (e1 == null) return false

                val diffX = e2.x - e1.x
                val diffY = e2.y - e1.y

                if (kotlin.math.abs(diffX) > kotlin.math.abs(diffY)) {
                    if (kotlin.math.abs(diffX) > SWIPE_THRESHOLD && kotlin.math.abs(velocityX) > SWIPE_VELOCITY_THRESHOLD) {
                        if (diffX > 0) {
                            onSwipeRight()
                        } else {
                            onSwipeLeft()
                        }
                        return true
                    }
                }
                return false
            }
        })

        bannerText.setOnTouchListener { _, event ->
            gestureDetector.onTouchEvent(event)
            true
        }

        // Listener para el efecto carrusel vertical en las tarjetas
        cards.forEachIndexed { index, card ->
            card.setOnClickListener {
                if (selectedCardIndex == index) {
                    // Segunda vez que se pulsa: acceder a la funcionalidad real
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

        // Detectar clicks fuera de las tarjetas para resetear animación
        rootView.setOnClickListener {
            if (selectedCardIndex != null) {
                resetCardAnimations()
            }
        }

        // Aquí la animación que pediste, solo la primera vez que carga el fragment:
        if (!hasAnimated) {
            animateProgressBar()
            animateCards()
            hasAnimated = true
        } else {
            // Si ya se animó antes, mostramos todo tal cual, sin animaciones
            progressBar.alpha = 1f
            progressBar.progress = progressBar.progress // mantiene el valor actual
            cards.forEach {
                it.alpha = 1f
                it.translationX = 0f
                it.scaleX = 1f
                it.scaleY = 1f
            }
        }

        return rootView
    }

    private fun animateBannerTextForward() {
        val width = bannerText.width.toFloat()

        if (width == 0f) {
            currentIndex = (currentIndex + 1) % messages.size
            bannerText.text = messages[currentIndex]
            return
        }

        bannerText.animate()
            .translationX(-width)
            .alpha(0f)
            .setDuration(500)
            .withEndAction {
                currentIndex = (currentIndex + 1) % messages.size
                bannerText.text = messages[currentIndex]

                bannerText.translationX = width
                bannerText.animate()
                    .translationX(0f)
                    .alpha(1f)
                    .setDuration(500)
                    .setInterpolator(AccelerateDecelerateInterpolator())
                    .start()
            }
            .start()
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
        val width = bannerText.width.toFloat()

        if (width == 0f) {
            bannerText.text = messages[currentIndex]
            return
        }

        val outTranslationX = if (forward) -width else width
        val inTranslationX = if (forward) width else -width

        bannerText.animate()
            .translationX(outTranslationX)
            .alpha(0f)
            .setDuration(300)
            .withEndAction {
                bannerText.text = messages[currentIndex]
                bannerText.translationX = inTranslationX
                bannerText.animate()
                    .translationX(0f)
                    .alpha(1f)
                    .setDuration(300)
                    .setInterpolator(AccelerateDecelerateInterpolator())
                    .start()
            }
            .start()
    }

    private fun resetAutoScroll() {
        handler.removeCallbacks(bannerRunnable)
        handler.postDelayed(bannerRunnable, delayMillis)
    }

    private fun animateProgressBar() {
        progressBar.alpha = 0f
        val targetProgress = progressBar.progress
        progressBar.progress = 0

        // Fade in la progress bar
        progressBar.animate()
            .alpha(1f)
            .setDuration(500)
            .start()

        // Animar progreso de 0 al valor actual en 1500ms
        val animator = ObjectAnimator.ofInt(progressBar, "progress", 0, targetProgress)
        animator.duration = 1500
        animator.interpolator = AccelerateDecelerateInterpolator()
        animator.start()
    }

    private fun animateCards() {
        cards.forEachIndexed { index, card ->
            card.alpha = 1f // para que no haya parpadeo al aparecer
            val animation = AnimationUtils.loadAnimation(requireContext(), R.anim.slide_in_left)
            animation.startOffset = (index * 150).toLong() // cascada con delay
            card.startAnimation(animation)
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
                    .translationY(((index - selectedIndex)).toFloat())
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
}
