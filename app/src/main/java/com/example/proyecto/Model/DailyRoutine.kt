package com.example.proyecto.Model

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class DailyRoutine(
    val day: String,
    val exercises: List<TrainingPlan>
) : Parcelable