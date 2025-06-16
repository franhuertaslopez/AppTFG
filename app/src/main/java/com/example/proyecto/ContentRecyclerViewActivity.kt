package com.example.proyecto

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.LinearSnapHelper
import androidx.recyclerview.widget.RecyclerView
import com.example.proyecto.Options.OptionsAdapter
import com.example.proyecto.CustomAnimation.CenterZoomLayout
import com.example.proyecto.Model.Menu

class ContentRecyclerViewActivity : AppCompatActivity() {

    @SuppressLint("MissingInflatedId")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_content_recycler_view)

        val options = findViewById<RecyclerView>(R.id.itemsRecycler)

        if (options == null) {
            Log.e("RecyclerViewError", "RecyclerView no encontrado!")
            return
        }

        val layoutManager = CenterZoomLayout(this)

        val trainingPlan = getString(R.string.training_plan)
        val trainingPlanDesc = getString(R.string.training_plan_desc)
        //val progressTracking = getString(R.string.progress_tracking)
        //val progressTrackingDesc = getString(R.string.progress_tracking_desc)
        val fitnessChallenge = getString(R.string.fitness_challenge)
        val fitnessChallengeDesc = getString(R.string.fitness_challenge_desc)
        //val equipmentBasedRoutines = getString(R.string.equipment_based_routine)
        //val equipmentBasedRoutinesDesc = getString(R.string.equipment_based_routine_desc)
        val stretchingMobility = getString(R.string.stretching_movility)
        val stretchingMobilityDesc = getString(R.string.stretching_movility_desc)
        val warmUpCoolDown = getString(R.string.warm_up_cool_down)
        val warmUpCoolDownDesc = getString(R.string.warm_up_cool_down_desc)
        val nutritionRecipes = getString(R.string.nutrition_recipes)
        val nutritionRecipesDesc = getString(R.string.nutrition_recipes_desc)
        val meditationMindfulness = getString(R.string.meditation_mindfulness)
        val meditationMindfulnessDesc = getString(R.string.meditation_mindfulness_desc)
        val expressWorkouts = getString(R.string.express_workouts)
        val expressWorkoutsDesc = getString(R.string.express_workouts_desc)
        val communityGroups = getString(R.string.community_groups)
        val communityGroupsDesc = getString(R.string.community_groups_desc)
        //val goalSetting = getString(R.string.goal_setting)
        //val goalSettingDesc = getString(R.string.goal_setting_desc)
        val workoutStatistics = getString(R.string.workout_statistics)
        val workoutStatisticsDesc = getString(R.string.workout_statistics_desc)
        val exploreWorkouts = getString(R.string.explore_workouts)
        val exploreWorkoutsDesc = getString(R.string.explore_workouts_desc)
        val supportTips = getString(R.string.support_tips)
        val supportTipsDesc = getString(R.string.support_tips_desc)

        layoutManager.orientation = LinearLayoutManager.HORIZONTAL
        layoutManager.reverseLayout = true
        layoutManager.stackFromEnd = true
        options.layoutManager = layoutManager

        val snapHelper = LinearSnapHelper()
        snapHelper.attachToRecyclerView(options)
        options.isNestedScrollingEnabled = false

        val plans = ArrayList<Menu>()

        plans.add(Menu(trainingPlan, trainingPlanDesc, "https://cdn.pixabay.com/photo/2017/04/25/20/18/woman-2260736_1280.jpg"))
        //plans.add(Menu(progressTracking,progressTrackingDesc, "https://images.pexels.com/photos/5714504/pexels-photo-5714504.jpeg?auto=compress&cs=tinysrgb&w=600"))
        plans.add(Menu(fitnessChallenge, fitnessChallengeDesc, "https://images.pexels.com/photos/841130/pexels-photo-841130.jpeg?auto=compress&cs=tinysrgb&w=600"))
        //plans.add(Menu(equipmentBasedRoutines, equipmentBasedRoutinesDesc, "https://images.pexels.com/photos/4753996/pexels-photo-4753996.jpeg?auto=compress&cs=tinysrgb&w=600"))
        plans.add(Menu(stretchingMobility, stretchingMobilityDesc, "https://images.pexels.com/photos/8436691/pexels-photo-8436691.jpeg?auto=compress&cs=tinysrgb&w=600"))
        plans.add(Menu(warmUpCoolDown, warmUpCoolDownDesc, "https://images.pexels.com/photos/4754001/pexels-photo-4754001.jpeg?auto=compress&cs=tinysrgb&w=600"))
        plans.add(Menu(nutritionRecipes, nutritionRecipesDesc, "https://images.pexels.com/photos/9004736/pexels-photo-9004736.jpeg?auto=compress&cs=tinysrgb&w=600"))
        plans.add(Menu(meditationMindfulness, meditationMindfulnessDesc, "https://images.pexels.com/photos/4325466/pexels-photo-4325466.jpeg?auto=compress&cs=tinysrgb&w=600"))
        plans.add(Menu(expressWorkouts, expressWorkoutsDesc, "https://images.pexels.com/photos/1103830/pexels-photo-1103830.jpeg?auto=compress&cs=tinysrgb&w=600"))
        plans.add(Menu(communityGroups, communityGroupsDesc, "https://images.pexels.com/photos/8980049/pexels-photo-8980049.jpeg?auto=compress&cs=tinysrgb&w=600"))
        //plans.add(Menu(goalSetting, goalSettingDesc, "https://images.pexels.com/photos/6551070/pexels-photo-6551070.jpeg?auto=compress&cs=tinysrgb&w=600"))
        plans.add(Menu(workoutStatistics, workoutStatisticsDesc, "https://images.pexels.com/photos/5816283/pexels-photo-5816283.jpeg?auto=compress&cs=tinysrgb&w=600"))
        plans.add(Menu(exploreWorkouts, exploreWorkoutsDesc, "https://images.pexels.com/photos/260352/pexels-photo-260352.jpeg?auto=compress&cs=tinysrgb&w=600"))
        plans.add(Menu(supportTips, supportTipsDesc, "https://images.pexels.com/photos/7653099/pexels-photo-7653099.jpeg?auto=compress&cs=tinysrgb&w=600"))

        plans.reverse()

        if (options.adapter == null) {
            options.adapter = OptionsAdapter(this, plans)
        }
    }

    @Deprecated("Deprecated in Java")
    @SuppressLint("MissingSuperCall")
    override fun onBackPressed() {
        val intent = Intent(this, MenuActivity::class.java)
        startActivity(intent)
        finish()
    }
}
