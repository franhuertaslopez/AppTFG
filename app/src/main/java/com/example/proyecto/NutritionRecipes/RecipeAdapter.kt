package com.example.proyecto.NutritionRecipes

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.AnimationUtils
import androidx.recyclerview.widget.RecyclerView
import com.example.proyecto.R
import com.example.proyecto.databinding.ItemRecipeBinding

class RecipeAdapter(
    private var recipes: List<Recipe>,
    private val onRecipeClick: ((Recipe) -> Unit)? = null,
    private val onFavoriteClick: ((Recipe) -> Unit)? = null
) : RecyclerView.Adapter<RecipeAdapter.RecipeViewHolder>() {

    private var expandedPosition = -1

    inner class RecipeViewHolder(val binding: ItemRecipeBinding) :
        RecyclerView.ViewHolder(binding.root)

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

            favoriteIcon.setImageResource(
                if (recipe.isFavorite) R.drawable.baseline_favorite_24
                else R.drawable.baseline_favorite_border_24
            )

            val popAnimation = AnimationUtils.loadAnimation(favoriteIcon.context, R.anim.heart_pop)

            favoriteIcon.setOnClickListener {
                recipe.isFavorite = !recipe.isFavorite

                favoriteIcon.setImageResource(
                    if (recipe.isFavorite) R.drawable.baseline_favorite_24
                    else R.drawable.baseline_favorite_border_24
                )
                favoriteIcon.startAnimation(popAnimation)

                onFavoriteClick?.invoke(recipe)
            }

            root.setOnClickListener {
                val previousExpanded = expandedPosition
                expandedPosition = if (isExpanded) -1 else position

                // Solo notifica los ítems afectados para optimizar:
                if (previousExpanded != -1) notifyItemChanged(previousExpanded)
                notifyItemChanged(position)

                onRecipeClick?.invoke(recipe)
            }
        }
    }

    override fun getItemCount(): Int = recipes.size

    fun updateList(newList: List<Recipe>) {
        recipes = newList
        expandedPosition = -1
        notifyDataSetChanged()
    }

    fun animateItems(recyclerView: RecyclerView) {
        recyclerView.addOnLayoutChangeListener(object : View.OnLayoutChangeListener {
            override fun onLayoutChange(
                v: View,
                left: Int,
                top: Int,
                right: Int,
                bottom: Int,
                oldLeft: Int,
                oldTop: Int,
                oldRight: Int,
                oldBottom: Int
            ) {
                recyclerView.removeOnLayoutChangeListener(this)
                val animation = AnimationUtils.loadAnimation(recyclerView.context, R.anim.slide_in_left)
                for (i in 0 until recyclerView.childCount) {
                    val child = recyclerView.getChildAt(i)
                    child.startAnimation(animation)
                }
            }
        })
    }
}
