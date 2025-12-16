package com.example.nextrep.models.data

import androidx.room.Database
import androidx.room.RoomDatabase
import com.example.nextrep.models.dao.ExerciseDao
import com.example.nextrep.models.dao.WorkoutSetDao
import com.example.nextrep.models.entity.ExerciseEntity
import com.example.nextrep.models.entity.WorkoutSetEntity

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