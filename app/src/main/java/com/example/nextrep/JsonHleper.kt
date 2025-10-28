package com.example.nextrep.data

import android.content.Context
import com.example.nextrep.models.Exercise
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import java.io.File

object JsonHelper {
    private const val FILE_NAME = "exercises.json"

    fun saveExercise(context: Context, exercise: Exercise) {
        val exercises = loadExercises(context).toMutableList()
        exercises.add(exercise)
        val jsonString = Gson().toJson(exercises)
        File(context.filesDir, FILE_NAME).writeText(jsonString)
    }

    fun loadExercises(context: Context): List<Exercise> {
        val file = File(context.filesDir, FILE_NAME)
        if (!file.exists()) return emptyList()
        val json = file.readText()
        val type = object : TypeToken<List<Exercise>>() {}.type
        return Gson().fromJson(json, type)
    }
}
