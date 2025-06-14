package com.example.proyecto.Model

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class TrainingPlan(
    val name: String,
    val reps: String,
    val targetArea: String
) : Parcelable
