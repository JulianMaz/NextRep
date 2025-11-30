package com.example.nextrep.data

import androidx.room.Database
import androidx.room.RoomDatabase
import com.example.nextrep.models.WorkoutSetEntity

@Database(
    entities = [WorkoutSetEntity::class],
    version = 1,
    exportSchema = false
)
abstract class NextRepDatabase : RoomDatabase() {
    abstract fun workoutSetDao(): WorkoutSetDao
}