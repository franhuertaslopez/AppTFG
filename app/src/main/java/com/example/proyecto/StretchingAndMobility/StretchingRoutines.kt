package com.example.proyecto.StretchingAndMobility

import android.content.Context
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import java.io.InputStreamReader
import java.util.Locale

data class Routine(
    val title: String,
    val description: String,
    val steps: List<String>
)

object StretchingRoutines {

    // Eliminamos la caché para forzar la carga siempre
    fun loadRoutines(context: Context): List<Routine> {
        val locale = Locale.getDefault().language
        val filename = when (locale) {
            "es" -> "stretching_routines_es.json"
            "fr" -> "stretching_routines_fr.json"
            else -> "stretching_routines_en.json" // default inglés si no es español o francés
        }

        val assetManager = context.assets
        val inputStream = assetManager.open(filename)
        val reader = InputStreamReader(inputStream)

        val listRoutineType = object : TypeToken<List<Routine>>() {}.type
        val routines = Gson().fromJson<List<Routine>>(reader, listRoutineType)

        reader.close()
        return routines
    }
}
