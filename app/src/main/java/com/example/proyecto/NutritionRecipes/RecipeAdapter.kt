package com.example.proyecto.NutritionRecipes

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.proyecto.databinding.ItemRecipeBinding

class RecipeAdapter(
    private var recipes: List<Recipe>,
    private val onRecipeClick: ((Recipe) -> Unit)? = null
) : RecyclerView.Adapter<RecipeAdapter.RecipeViewHolder>() {

    private var expandedPosition = -1  // Ninguno expandido inicialmente

    inner class RecipeViewHolder(val binding: ItemRecipeBinding) : RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecipeViewHolder {
        val binding = ItemRecipeBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return RecipeViewHolder(binding)
    }

    override fun onBindViewHolder(holder: RecipeViewHolder, @SuppressLint("RecyclerView") position: Int) {
        val recipe = recipes[position]
        val isExpanded = position == expandedPosition

        with(holder.binding) {
            recipeTitle.text = recipe.title
            recipeNutrients.text = recipe.nutrients
            recipeDescription.text = recipe.description

            expandableLayout.visibility = if (isExpanded) View.VISIBLE else View.GONE
            expandIcon.rotation = if (isExpanded) 180f else 0f

            root.setOnClickListener {
                val previousExpanded = expandedPosition
                if (isExpanded) {
                    // Si se pulsa el mismo expandido, lo colapsamos
                    expandedPosition = -1
                } else {
                    // Abrimos el nuevo y cerramos el anterior
                    expandedPosition = position
                }
                notifyItemChanged(previousExpanded)
                notifyItemChanged(position)
                onRecipeClick?.invoke(recipe)
            }
        }
    }

    override fun getItemCount(): Int = recipes.size

    fun updateList(newList: List<Recipe>) {
        recipes = newList
        expandedPosition = -1  // Resetear expansión al actualizar la lista
        notifyDataSetChanged()
    }
}

