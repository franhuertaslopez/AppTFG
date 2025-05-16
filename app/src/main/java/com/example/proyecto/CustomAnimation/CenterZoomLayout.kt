package com.example.proyecto.CustomAnimation

import android.content.Context
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import kotlin.math.abs
import kotlin.math.min

class CenterZoomLayout : LinearLayoutManager {
    private val mShrinkAmount = 0.15f // Cuánto se reducirá el tamaño
    private val mShrinkDistance = 0.9f // Qué tan lejos está la distancia para empezar a reducir el tamaño

    constructor(context: Context) : super(context)

    constructor(context: Context, orientation: Int, reverseLayout: Boolean) :
            super(context, orientation, reverseLayout)

    override fun scrollHorizontallyBy(
        dx: Int,
        recycler: RecyclerView.Recycler?,
        state: RecyclerView.State?
    ): Int {
        // Si estamos en modo horizontal, calculamos el desplazamiento
        if (orientation == HORIZONTAL) {
            val scrolled = super.scrollHorizontallyBy(dx, recycler, state)

            // Definimos el punto medio y el rango de zoom
            val midpoint = width / 2f
            val d0 = 0f
            val d1 = mShrinkDistance * midpoint
            val s0 = 1f
            val s1 = 1f - mShrinkAmount

            // Animación para cada hijo visible en el RecyclerView
            for (i in 0 until childCount) {
                val child = getChildAt(i)
                val chilMidPoint = (getDecoratedRight(child!!) + getDecoratedLeft(child)) / 2f
                val d = min(d1, abs(midpoint - chilMidPoint))
                val scale = s0 + (s1 - s0) * (d - d0) / (d1 - d0)

                // Asegúrate de que la escala no se vuelva demasiado pequeña (por ejemplo, no menos de 0.85)
                val finalScale = scale.coerceAtLeast(0.85f)  // Escala mínima ajustada para mayor nitidez

                // Aplicamos solo la escala, sin cambiar la opacidad
                child.scaleX = finalScale
                child.scaleY = finalScale
            }
            return scrolled
        } else {
            return 0
        }
    }
}
