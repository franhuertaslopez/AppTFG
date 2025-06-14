package com.example.proyecto.Data

import com.example.proyecto.Model.DailyRoutine
import com.example.proyecto.Model.TrainingPlan

object BaseTrainingPlans {

    fun getTrainingPlan(level: String, goal: String): List<DailyRoutine> {
        return when (level) {
            "beginner" -> when (goal) {
                "weight_loss" -> beginnerWeightLossPlan
                "muscle_gain" -> beginnerMuscleGainPlan
                "maintenance" -> beginnerMaintenancePlan
                else -> emptyList()
            }
            "intermediate" -> when (goal) {
                "weight_loss" -> intermediateWeightLossPlan
                "muscle_gain" -> intermediateMuscleGainPlan
                "maintenance" -> intermediateMaintenancePlan
                else -> emptyList()
            }
            "advanced" -> when (goal) {
                "weight_loss" -> advancedWeightLossPlan
                "muscle_gain" -> advancedMuscleGainPlan
                "maintenance" -> advancedMaintenancePlan
                else -> emptyList()
            }
            else -> emptyList()
        }
    }

    // -------------------- BEGINNER --------------------
    private val beginnerWeightLossPlan = listOf(
        DailyRoutine("Monday", listOf(
            TrainingPlan("Cardio (cinta)", "20 min", "Full Body"),
            TrainingPlan("Sentadillas", "3x15", "Piernas"),
            TrainingPlan("Plancha", "3x30s", "Core")
        )),
        DailyRoutine("Tuesday", listOf(
            TrainingPlan("Bicicleta estática", "25 min", "Cardio"),
            TrainingPlan("Zancadas", "3x12", "Piernas"),
            TrainingPlan("Crunches", "3x20", "Abdomen")
        )),
        DailyRoutine("Wednesday", listOf(
            TrainingPlan("Caminata rápida", "30 min", "Cardio"),
            TrainingPlan("Step-ups", "3x15", "Piernas"),
            TrainingPlan("Superman", "3x20s", "Espalda baja")
        )),
        DailyRoutine("Thursday", listOf(
            TrainingPlan("HIIT suave", "20 min", "Cardio"),
            TrainingPlan("Mountain climbers", "3x30s", "Core"),
            TrainingPlan("Jumping jacks", "3x20", "Full Body")
        )),
        DailyRoutine("Friday", listOf(
            TrainingPlan("Elíptica", "25 min", "Cardio"),
            TrainingPlan("Sentadillas sumo", "3x15", "Piernas"),
            TrainingPlan("Plank lateral", "3x20s", "Core")
        )),
        DailyRoutine("Saturday", listOf(
            TrainingPlan("Trote suave", "20 min", "Cardio"),
            TrainingPlan("Abdominales bicicleta", "3x20", "Core"),
            TrainingPlan("Estiramientos", "10 min", "Recuperación")
        ))
    )

    private val beginnerMuscleGainPlan = listOf(
        DailyRoutine("Monday", listOf(
            TrainingPlan("Flexiones", "3x10", "Pecho"),
            TrainingPlan("Press militar con mancuernas", "3x10", "Hombros"),
            TrainingPlan("Sentadillas con peso", "3x12", "Piernas")
        )),
        DailyRoutine("Tuesday", listOf(
            TrainingPlan("Curl bíceps", "3x12", "Brazos"),
            TrainingPlan("Fondos en banco", "3x10", "Tríceps"),
            TrainingPlan("Remo con banda", "3x15", "Espalda")
        )),
        DailyRoutine("Wednesday", listOf(
            TrainingPlan("Peso muerto rumano", "3x10", "Piernas"),
            TrainingPlan("Flexiones declinadas", "3x10", "Pecho superior"),
            TrainingPlan("Elevaciones laterales", "3x12", "Hombros")
        )),
        DailyRoutine("Thursday", listOf(
            TrainingPlan("Sentadilla búlgara", "3x12", "Piernas"),
            TrainingPlan("Remo con banda", "3x15", "Espalda"),
            TrainingPlan("Plancha con toque de hombros", "3x30s", "Core")
        )),
        DailyRoutine("Friday", listOf(
            TrainingPlan("Flexiones diamante", "3x8", "Tríceps"),
            TrainingPlan("Curl martillo", "3x12", "Brazos"),
            TrainingPlan("Crunches", "3x20", "Abdomen")
        )),
        DailyRoutine("Saturday", listOf(
            TrainingPlan("Circuito cuerpo completo", "3 rondas", "Full Body"),
            TrainingPlan("Remo renegado", "3x12", "Espalda"),
            TrainingPlan("Estiramientos", "10 min", "Movilidad")
        ))
    )

    private val beginnerMaintenancePlan = listOf(
        DailyRoutine("Monday", listOf(
            TrainingPlan("Caminar rápido", "30 min", "Cardio"),
            TrainingPlan("Estiramientos", "10 min", "Movilidad")
        )),
        DailyRoutine("Tuesday", listOf(
            TrainingPlan("Ciclismo suave", "30 min", "Cardio"),
            TrainingPlan("Abdominales básicos", "3x15", "Core")
        )),
        DailyRoutine("Wednesday", listOf(
            TrainingPlan("Movilidad articular", "15 min", "Movilidad"),
            TrainingPlan("Flexiones suaves", "3x8", "Pecho")
        )),
        DailyRoutine("Thursday", listOf(
            TrainingPlan("Yoga suave", "30 min", "Movilidad"),
            TrainingPlan("Sentadillas", "3x15", "Piernas")
        )),
        DailyRoutine("Friday", listOf(
            TrainingPlan("Caminata", "40 min", "Cardio"),
            TrainingPlan("Crunches", "3x20", "Core")
        )),
        DailyRoutine("Saturday", listOf(
            TrainingPlan("Movilidad con foam roller", "15 min", "Recuperación"),
            TrainingPlan("Rutina ligera full body", "2 rondas", "General")
        ))
    )

    // -------------------- INTERMEDIATE --------------------
    private val intermediateWeightLossPlan = listOf(
        DailyRoutine("Monday", listOf(
            TrainingPlan("HIIT", "25 min", "Cardio"),
            TrainingPlan("Burpees", "3x12", "Full Body"),
            TrainingPlan("Mountain climbers", "3x30s", "Core"),
            TrainingPlan("Jumping jacks", "3x20", "Full Body")
        )),
        DailyRoutine("Tuesday", listOf(
            TrainingPlan("Cardio funcional", "30 min", "Cardio"),
            TrainingPlan("Squat jumps", "3x15", "Piernas"),
            TrainingPlan("Plank con rotación", "3x30s", "Core"),
            TrainingPlan("Step-ups con peso", "3x12", "Piernas")
        )),
        DailyRoutine("Wednesday", listOf(
            TrainingPlan("Ciclismo", "30 min", "Cardio"),
            TrainingPlan("Lunges con salto", "3x12", "Piernas"),
            TrainingPlan("Crunches con peso", "3x20", "Abdomen"),
            TrainingPlan("Superman", "3x20s", "Espalda baja")
        )),
        DailyRoutine("Thursday", listOf(
            TrainingPlan("Entrenamiento cruzado", "40 min", "Full Body"),
            TrainingPlan("Kettlebell swings", "3x15", "Espalda/Piernas"),
            TrainingPlan("Plank lateral", "3x30s", "Core"),
            TrainingPlan("Mountain climbers", "3x30s", "Core")
        )),
        DailyRoutine("Friday", listOf(
            TrainingPlan("Cinta inclinada", "25 min", "Cardio"),
            TrainingPlan("Step-ups con peso", "3x12", "Piernas"),
            TrainingPlan("Superman", "3x20s", "Espalda baja"),
            TrainingPlan("Burpees", "3x12", "Full Body")
        )),
        DailyRoutine("Saturday", listOf(
            TrainingPlan("Body combat", "30 min", "Cardio"),
            TrainingPlan("Circuito HIIT", "3 rondas", "Full Body"),
            TrainingPlan("Estiramientos", "10 min", "Movilidad"),
            TrainingPlan("Jumping jacks", "3x20", "Full Body")
        ))
    )

    private val intermediateMuscleGainPlan = listOf(
        DailyRoutine("Monday", listOf(
            TrainingPlan("Press banca", "4x10", "Pecho"),
            TrainingPlan("Press militar", "4x10", "Hombros"),
            TrainingPlan("Fondos de tríceps", "4x10", "Tríceps"),
            TrainingPlan("Elevaciones laterales", "4x12", "Hombros")
        )),
        DailyRoutine("Tuesday", listOf(
            TrainingPlan("Sentadilla frontal", "4x8", "Piernas"),
            TrainingPlan("Hip thrust", "4x10", "Glúteos"),
            TrainingPlan("Peso muerto rumano", "4x10", "Piernas"),
            TrainingPlan("Curl bíceps", "4x10", "Brazos")
        )),
        DailyRoutine("Wednesday", listOf(
            TrainingPlan("Dominadas asistidas", "4x8", "Espalda"),
            TrainingPlan("Remo con barra", "4x10", "Espalda"),
            TrainingPlan("Curl martillo", "4x10", "Brazos"),
            TrainingPlan("Pull-over con mancuerna", "3x12", "Espalda")
        )),
        DailyRoutine("Thursday", listOf(
            TrainingPlan("Press banca inclinado", "4x10", "Pecho superior"),
            TrainingPlan("Remo renegado", "3x10", "Espalda/Core"),
            TrainingPlan("Crunches con peso", "3x20", "Core"),
            TrainingPlan("Plancha con toque de hombros", "3x30s", "Core")
        )),
        DailyRoutine("Friday", listOf(
            TrainingPlan("Peso muerto", "4x6", "Piernas/Espalda"),
            TrainingPlan("Curl bíceps", "4x10", "Brazos"),
            TrainingPlan("Fondos de tríceps", "4x10", "Tríceps"),
            TrainingPlan("Elevaciones laterales", "4x12", "Hombros")
        )),
        DailyRoutine("Saturday", listOf(
            TrainingPlan("Circuito fuerza", "3 rondas", "Full Body"),
            TrainingPlan("Estiramientos", "10 min", "Movilidad"),
            TrainingPlan("Remo renegado", "3x10", "Espalda/Core"),
            TrainingPlan("Hip thrust", "4x10", "Glúteos")
        ))
    )

    private val intermediateMaintenancePlan = beginnerMaintenancePlan

    // -------------------- ADVANCED --------------------
    private val advancedWeightLossPlan = listOf(
        DailyRoutine("Monday", listOf(
            TrainingPlan("HIIT Avanzado", "30 min", "Cardio"),
            TrainingPlan("Sprints", "10x100m", "Explosivo"),
            TrainingPlan("Burpees con salto", "3x20", "Full Body"),
            TrainingPlan("Jump lunges", "3x15", "Piernas"),
            TrainingPlan("Mountain climbers", "3x45s", "Core")
        )),
        DailyRoutine("Tuesday", listOf(
            TrainingPlan("Battle ropes", "5x30s", "Full Body"),
            TrainingPlan("Box jumps", "3x15", "Pliometría"),
            TrainingPlan("Plank con pesas", "3x30s", "Core"),
            TrainingPlan("Kettlebell swings", "4x20", "Full Body")
        )),
        DailyRoutine("Wednesday", listOf(
            TrainingPlan("Ciclismo intensidad", "45 min", "Cardio"),
            TrainingPlan("Lunges con peso", "4x12", "Piernas"),
            TrainingPlan("Crunches con peso", "4x25", "Abdomen"),
            TrainingPlan("Superman avanzado", "4x30s", "Espalda baja")
        )),
        DailyRoutine("Thursday", listOf(
            TrainingPlan("CrossFit WOD", "30-40 min", "Full Body"),
            TrainingPlan("Snatch con barra", "4x6", "Fuerza/Explosividad"),
            TrainingPlan("Plancha lateral con peso", "3x45s", "Core"),
            TrainingPlan("Mountain climbers rápidos", "3x45s", "Core")
        )),
        DailyRoutine("Friday", listOf(
            TrainingPlan("Trote con intervalos", "30 min", "Cardio"),
            TrainingPlan("Sentadillas profundas", "4x15", "Piernas"),
            TrainingPlan("Peso muerto convencional", "4x8", "Piernas/Espalda"),
            TrainingPlan("Burpees con salto", "4x20", "Full Body")
        )),
        DailyRoutine("Saturday", listOf(
            TrainingPlan("Circuito avanzado de fuerza", "4 rondas", "Full Body"),
            TrainingPlan("Estiramientos dinámicos", "15 min", "Movilidad"),
            TrainingPlan("Remo renegado con peso", "4x12", "Espalda/Core"),
            TrainingPlan("Hip thrust con peso", "4x12", "Glúteos")
        ))
    )

    private val advancedMuscleGainPlan = listOf(
        DailyRoutine("Monday", listOf(
            TrainingPlan("Press banca pesado", "5x5", "Pecho"),
            TrainingPlan("Press militar pesado", "5x5", "Hombros"),
            TrainingPlan("Fondos con peso", "4x10", "Tríceps"),
            TrainingPlan("Elevaciones laterales pesadas", "4x15", "Hombros")
        )),
        DailyRoutine("Tuesday", listOf(
            TrainingPlan("Sentadilla trasera", "5x5", "Piernas"),
            TrainingPlan("Hip thrust con barra", "5x8", "Glúteos"),
            TrainingPlan("Peso muerto rumano pesado", "5x8", "Piernas"),
            TrainingPlan("Curl bíceps barra", "4x12", "Brazos")
        )),
        DailyRoutine("Wednesday", listOf(
            TrainingPlan("Dominadas con lastre", "5x8", "Espalda"),
            TrainingPlan("Remo con barra pesado", "5x10", "Espalda"),
            TrainingPlan("Curl martillo con mancuernas", "4x12", "Brazos"),
            TrainingPlan("Pull-over con mancuerna pesado", "4x12", "Espalda")
        )),
        DailyRoutine("Thursday", listOf(
            TrainingPlan("Press banca inclinado pesado", "5x10", "Pecho superior"),
            TrainingPlan("Remo renegado con peso", "4x12", "Espalda/Core"),
            TrainingPlan("Crunches con peso elevado", "4x25", "Core"),
            TrainingPlan("Plancha con toque de hombros pesado", "4x45s", "Core")
        )),
        DailyRoutine("Friday", listOf(
            TrainingPlan("Peso muerto convencional pesado", "5x6", "Piernas/Espalda"),
            TrainingPlan("Curl bíceps barra pesada", "4x12", "Brazos"),
            TrainingPlan("Fondos con peso", "4x12", "Tríceps"),
            TrainingPlan("Elevaciones laterales pesadas", "4x15", "Hombros")
        )),
        DailyRoutine("Saturday", listOf(
            TrainingPlan("Circuito avanzado fuerza", "5 rondas", "Full Body"),
            TrainingPlan("Estiramientos avanzados", "15 min", "Movilidad"),
            TrainingPlan("Remo renegado con peso elevado", "5x12", "Espalda/Core"),
            TrainingPlan("Hip thrust con barra pesada", "5x12", "Glúteos")
        ))
    )

    private val advancedMaintenancePlan = intermediateMaintenancePlan

}
