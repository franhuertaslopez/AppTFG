package com.example.proyecto.TrainingPlan

import android.animation.ValueAnimator
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.example.proyecto.Model.TrainingPlan
import com.example.proyecto.R
import com.example.proyecto.databinding.ItemExerciseBinding

class ExerciseAdapter(
    private val exerciseList: List<TrainingPlan>
) : RecyclerView.Adapter<ExerciseAdapter.ExerciseViewHolder>() {

    // Índice del ejercicio actual
    private var currentExerciseIndex = 0

    // Índices de ejercicios completados
    private val completedExercises = mutableSetOf<Int>()

    inner class ExerciseViewHolder(private val binding: ItemExerciseBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(exercise: TrainingPlan, position: Int) {
            binding.tvExerciseName.text = exercise.name
            binding.tvReps.text = "${exercise.reps} repeticiones"
            binding.tvTargetArea.text = "Área objetivo: ${exercise.targetArea}"

            // Estado visual según posición
            when {
                position == currentExerciseIndex -> {
                    // Ejercicio actual: fondo naranja + escala + texto negro
                    //binding.root.setBackgroundColor(ContextCompat.getColor(binding.root.context, R.color.purple_200))
                    animateScale(binding.root, 1f, 1.05f)
                    binding.tvExerciseName.setTextColor(ContextCompat.getColor(binding.root.context, R.color.black))
                    binding.tvReps.setTextColor(ContextCompat.getColor(binding.root.context, R.color.black))
                    binding.tvTargetArea.setTextColor(ContextCompat.getColor(binding.root.context, R.color.black))
                    binding.root.alpha = 1f
                }
                completedExercises.contains(position) -> {
                    // Ejercicio completado: fondo gris claro + escala normal + texto gris
                    binding.root.setBackgroundColor(ContextCompat.getColor(binding.root.context, R.color.gray))
                    animateScale(binding.root, binding.root.scaleX, 1f)
                    val grayColor = ContextCompat.getColor(binding.root.context, R.color.gray_dark)
                    binding.tvExerciseName.setTextColor(grayColor)
                    binding.tvReps.setTextColor(grayColor)
                    binding.tvTargetArea.setTextColor(grayColor)
                    binding.root.alpha = 0.6f
                }
                else -> {
                    // Ejercicios pendientes: fondo blanco + escala normal + texto negro
                    binding.root.setBackgroundColor(ContextCompat.getColor(binding.root.context, R.color.white))
                    animateScale(binding.root, binding.root.scaleX, 1f)
                    binding.tvExerciseName.setTextColor(ContextCompat.getColor(binding.root.context, R.color.black))
                    binding.tvReps.setTextColor(ContextCompat.getColor(binding.root.context, R.color.black))
                    binding.tvTargetArea.setTextColor(ContextCompat.getColor(binding.root.context, R.color.black))
                    binding.root.alpha = 1f
                }
            }
        }

        private fun animateScale(view: android.view.View, from: Float, to: Float) {
            val animator = ValueAnimator.ofFloat(from, to)
            animator.duration = 300
            animator.addUpdateListener { animation ->
                val scale = animation.animatedValue as Float
                view.scaleX = scale
                view.scaleY = scale
            }
            animator.start()
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ExerciseViewHolder {
        val binding = ItemExerciseBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ExerciseViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ExerciseViewHolder, position: Int) {
        holder.bind(exerciseList[position], position)
    }

    override fun getItemCount(): Int = exerciseList.size

    // Método para avanzar al siguiente ejercicio
    fun nextExercise() {
        if (currentExerciseIndex < exerciseList.size) {
            completedExercises.add(currentExerciseIndex)
            currentExerciseIndex++
            notifyDataSetChanged()
        }
    }

    // Método para resetear la rutina (si quieres)
    fun reset() {
        currentExerciseIndex = 0
        completedExercises.clear()
        notifyDataSetChanged()
    }
}
