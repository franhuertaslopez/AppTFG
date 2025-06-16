package com.example.proyecto.Options

import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.proyecto.Authentication.LoginActivity
import com.example.proyecto.FitnessChallenge.FitnessChallenge
import com.example.proyecto.R
import com.example.proyecto.Model.Menu
import com.example.proyecto.NutritionRecipes.NutritionRecipes
import com.example.proyecto.StretchingAndMobility.StretchingActivity
import com.example.proyecto.SupportAndTips.SupportAndTipsActivity
import com.example.proyecto.TrainingPlan.TrainingPlanActivity
import com.flaviofaria.kenburnsview.KenBurnsView
import com.squareup.picasso.Picasso

class OptionsAdapter(val context: Context?, private val options_list: ArrayList<Menu>) : RecyclerView.Adapter<OptionsAdapter.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val v = LayoutInflater.from(parent.context).inflate(R.layout.modes_layout, parent, false)
        return ViewHolder(v)
    }

    override fun getItemCount(): Int {
        return options_list.size
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val option = options_list[position]

        // Cargar la imagen usando Picasso
        Picasso.get().load(option.url).into(holder.optionImage)

        // Configurar los textos
        holder.optionTitle.text = option.title
        holder.optionDescription.text = option.description

        // Configurar el OnClickListener para abrir la actividad correspondiente
        holder.itemView.setOnClickListener {

            val context = holder.itemView.context
            val trainingPlanTitle = context.getString(R.string.training_plan)
            //val progressTrackingTitle = context.getString(R.string.progress_tracking)
            val fitnessChallengeTitle = context.getString(R.string.fitness_challenge)
            val equipmentRoutinesTitle = context.getString(R.string.equipment_based_routine)
            val stretchingMobilityTitle = context.getString(R.string.stretching_movility)
            val warmupCooldownTitle = context.getString(R.string.warm_up_cool_down)
            val nutritionRecipesTitle = context.getString(R.string.nutrition_recipes)
            val meditationMindfulnessTitle = context.getString(R.string.meditation_mindfulness)
            val expressWorkoutsTitle = context.getString(R.string.express_workouts)
            val communityGroupsTitle = context.getString(R.string.community_groups)
            val goalSettingTitle = context.getString(R.string.goal_setting)
            val workoutStatisticsTitle = context.getString(R.string.workout_statistics)
            val exploreWorkoutsTitle = context.getString(R.string.explore_workouts)
            val supportTipsTitle = context.getString(R.string.support_tips)

            val intent = when (option.title) {
                trainingPlanTitle -> Intent(context, TrainingPlanActivity::class.java)
                //progressTrackingTitle -> Intent(context, LoginActivity::class.java)
                fitnessChallengeTitle -> Intent(context, FitnessChallenge::class.java)
                //equipmentRoutinesTitle -> Intent(context, LoginActivity::class.java)
                stretchingMobilityTitle -> Intent(context, StretchingActivity::class.java)
                warmupCooldownTitle -> Intent(context, LoginActivity::class.java)
                nutritionRecipesTitle -> Intent(context, NutritionRecipes::class.java)
                meditationMindfulnessTitle -> Intent(context, LoginActivity::class.java)
                expressWorkoutsTitle -> Intent(context, LoginActivity::class.java)
                communityGroupsTitle -> Intent(context, LoginActivity::class.java)
                //goalSettingTitle -> Intent(context, LoginActivity::class.java)
                workoutStatisticsTitle -> Intent(context, LoginActivity::class.java)
                exploreWorkoutsTitle -> Intent(context, LoginActivity::class.java)
                supportTipsTitle -> Intent(context, SupportAndTipsActivity::class.java)
                else -> null
            }

            // Verificar si el intent no es nulo y empezar la actividad
            intent?.let {
                context?.startActivity(it)
            }
        }
    }

    class ViewHolder(v: View) : RecyclerView.ViewHolder(v) {
        // Referencias a las vistas del layout
        val optionImage: KenBurnsView = v.findViewById(R.id.optionImage)
        val optionTitle: TextView = v.findViewById(R.id.optionTitle)
        val optionDescription: TextView = v.findViewById(R.id.optionDescription)
    }
}
