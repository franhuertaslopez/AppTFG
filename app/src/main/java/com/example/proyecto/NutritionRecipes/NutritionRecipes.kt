package com.example.proyecto.NutritionRecipes

import android.content.Context
import android.os.Bundle
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.proyecto.Language_Theme.BaseActivity
import com.example.proyecto.databinding.ActivityNutritionRecipesBinding

class NutritionRecipes : BaseActivity() {

    private lateinit var binding: ActivityNutritionRecipesBinding
    private lateinit var adapter: RecipeAdapter

    private lateinit var allRecipes: List<Recipe>
    private var filteredRecipes = mutableListOf<Recipe>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityNutritionRecipesBinding.inflate(layoutInflater)
        setContentView(binding.root)

        allRecipes = loadRecipesFromAssets()
        filteredRecipes = allRecipes.toMutableList()

        adapter = RecipeAdapter(filteredRecipes)
        binding.recipesRecyclerView.layoutManager = LinearLayoutManager(this)
        binding.recipesRecyclerView.adapter = adapter

        setupSearchFilter()
    }

    private fun getCurrentLanguage(): String {
        val prefs = getSharedPreferences("settings", Context.MODE_PRIVATE)
        return prefs.getString("language", "en") ?: "en"
    }

    private fun loadRecipesFromAssets(): List<Recipe> {
        val recipes = mutableListOf<Recipe>()
        val language = getCurrentLanguage()

        // Define el nombre del archivo según el idioma
        val fileName = when (language) {
            "es" -> "recipes_es.txt"
            "fr" -> "recipes_fr.txt"
            else -> "recipes_en.txt" // Por defecto inglés
        }

        try {
            val inputStream = assets.open(fileName)
            inputStream.bufferedReader().useLines { lines ->
                lines.forEach { line ->
                    val parts = line.split("|")
                    if (parts.size == 3) {
                        val category = parts[0].trim()
                        val title = parts[1].trim()
                        val description = parts[2].trim()
                        recipes.add(Recipe(title, description, category))
                    }
                }
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
        return recipes
    }


    private fun setupSearchFilter() {
        binding.searchView.setOnQueryTextListener(object : androidx.appcompat.widget.SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean {
                filterRecipes(query)
                return true
            }

            override fun onQueryTextChange(newText: String?): Boolean {
                filterRecipes(newText)
                return true
            }
        })
    }

    private fun filterRecipes(query: String?) {
        val text = query?.trim()?.lowercase() ?: ""
        filteredRecipes = if (text.isEmpty()) {
            allRecipes.toMutableList()
        } else {
            allRecipes.filter {
                it.title.lowercase().contains(text) || it.nutrients.lowercase().contains(text)
            }.toMutableList()
        }
        adapter.updateList(filteredRecipes)
    }
}
