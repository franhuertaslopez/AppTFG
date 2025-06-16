package com.example.proyecto.NutritionRecipes

data class Recipe(
    val id: String,
    val title: String,
    val description: String,
    val nutrients: String,
    var isExpanded: Boolean = false,
    var isFavorite: Boolean = false
)


