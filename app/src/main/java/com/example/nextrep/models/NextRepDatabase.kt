package com.example.nextrep.data

import androidx.room.Database
import androidx.room.RoomDatabase
import com.example.nextrep.models.WorkoutSetEntity
import com.example.nextrep.models.ExerciseEntity


@Database(
    entities = [
        WorkoutSetEntity::class,
        ExerciseEntity::class
   ],

    version = 2,
    exportSchema = false
)
abstract class NextRepDatabase : RoomDatabase() {
    abstract fun workoutSetDao(): WorkoutSetDao
    abstract fun exerciseDao(): ExerciseDao

}