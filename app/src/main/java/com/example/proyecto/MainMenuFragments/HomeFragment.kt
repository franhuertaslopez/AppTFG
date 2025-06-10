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
import android.widget.TextView
import androidx.fragment.app.Fragment
import com.example.proyecto.R

class HomeFragment : Fragment() {

    private lateinit var bannerText: TextView
    private val handler = Handler(Looper.getMainLooper())
    private var currentIndex = 0

    companion object {
        private var savedIndex = 0
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

    @SuppressLint("ClickableViewAccessibility")
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val rootView = inflater.inflate(R.layout.fragment_home, container, false)
        bannerText = rootView.findViewById(R.id.bannerText)

        messages = listOf(
            getString(R.string.banner_message_1),
            getString(R.string.banner_message_2),
            getString(R.string.banner_message_3),
            getString(R.string.banner_message_4),
            getString(R.string.banner_message_5),
            getString(R.string.banner_message_6)
        )

        currentIndex = savedIndex
        bannerText.text = messages[currentIndex]

        // Configurar GestureDetector para detectar swipes
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

        // Asignar listener táctil al TextView para detectar gestos
        bannerText.setOnTouchListener { _, event ->
            gestureDetector.onTouchEvent(event)
            true
        }

        handler.postDelayed(bannerRunnable, delayMillis)

        return rootView
    }

    // Animación automática (avanzar)
    private fun animateBannerTextForward() {
        val width = bannerText.width.toFloat()

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

    // Swipe hacia la izquierda (siguiente mensaje)
    private fun onSwipeLeft() {
        currentIndex = (currentIndex + 1) % messages.size
        animateBannerTextWithDirection(forward = true)
        resetAutoScroll()
    }

    // Swipe hacia la derecha (mensaje anterior)
    private fun onSwipeRight() {
        currentIndex = if (currentIndex - 1 < 0) messages.size - 1 else currentIndex - 1
        animateBannerTextWithDirection(forward = false)
        resetAutoScroll()
    }

    // Animación con dirección para swipe
    private fun animateBannerTextWithDirection(forward: Boolean) {
        val width = bannerText.width.toFloat()

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

    override fun onDestroyView() {
        super.onDestroyView()
        handler.removeCallbacks(bannerRunnable)
        savedIndex = currentIndex
    }
}
