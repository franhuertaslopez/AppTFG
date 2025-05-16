package com.example.proyecto.Adapter

import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.proyecto.LoginActivity
import com.example.proyecto.R
import com.example.proyecto.Model.Menu
import com.example.proyecto.PlanActivities.TrainingPlanActivity
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
            val intent = when (option.title) {
                "Training Plan" -> Intent(context, TrainingPlanActivity::class.java)
                "Progress Tracking" -> Intent(context, LoginActivity::class.java)
                "Fitness Challenge" -> Intent(context, LoginActivity::class.java)
                "Equipment-Based Routines" -> Intent(context, LoginActivity::class.java)
                "Stretching & Mobility" -> Intent(context, LoginActivity::class.java)
                "Warm-Up & Cool-Down" -> Intent(context, LoginActivity::class.java)
                "Nutrition & Recipes" -> Intent(context, LoginActivity::class.java)
                "Meditation & Mindfulness" -> Intent(context, LoginActivity::class.java)
                "Express Workouts" -> Intent(context, LoginActivity::class.java)
                "Community & Groups" -> Intent(context, LoginActivity::class.java)
                "Goal Setting" -> Intent(context, LoginActivity::class.java)
                "Workout Statistics" -> Intent(context, LoginActivity::class.java)
                "Explore Workouts" -> Intent(context, LoginActivity::class.java)
                "Support & Tips" -> Intent(context, LoginActivity::class.java)
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
