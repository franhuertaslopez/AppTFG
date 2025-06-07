package com.example.proyecto

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.proyecto.Options.OptionsAdapterClass
import com.example.proyecto.Options.OptionsDataClass

class ___EjemploDeItems : AppCompatActivity() {

    private lateinit var recyclerView: RecyclerView
    private lateinit var dataList:ArrayList<OptionsDataClass>
    lateinit var imageList:Array<Int>
    lateinit var titleList:Array<String>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.___activity_ejemplodeitems)

        imageList = arrayOf(
            R.drawable.plan_de_entrenamiento,
            R.drawable.seguimiento_de_progreso,
            R.drawable.desafio_fitness,
            R.drawable.rutinas_por_equipamiento,
            R.drawable.estiramientos_movilidad,
            R.drawable.nutricion_y_recetas,
            R.drawable.meditacion,
            R.drawable.entrenamiento_express,
            R.drawable.comunidad_grupos,
            R.drawable.configuracion_metas,
            R.drawable.estadisticas_entrenamiento
        )

        titleList = arrayOf(
            "Plan de entrenamiento",
            "Seguimiento de progreso",
            "Desafío fitness",
            "Rutinas por equipamiento",
            "Estiramientos y movilidad",
            "Nutrición y recetas",
            "Meditación",
            "Entrenamiento express",
            "Comunidad y grupos",
            "Configuración y metas",
            "Estadísticas"
        )

        recyclerView = findViewById(R.id.options_list)
        recyclerView.layoutManager = LinearLayoutManager(this)
        recyclerView.setHasFixedSize(true)

        dataList = arrayListOf<OptionsDataClass>()
        getData()
    }

    private fun getData(){
        for (i in imageList.indices){
            val dataClass = OptionsDataClass(imageList[i], titleList[i])
            dataList.add(dataClass)
        }
        recyclerView.adapter = OptionsAdapterClass(dataList)
    }
}