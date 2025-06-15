package com.example.proyecto.NutritionRecipes

data class Recipe(
    val title: String,
    val description: String,
    val nutrients: String, // Ej: "Alta proteína"
    var isExpanded: Boolean = false
)

