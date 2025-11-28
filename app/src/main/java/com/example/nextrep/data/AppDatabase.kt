package com.example.nextrep.data

import android.app.Application
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.sqlite.db.SupportSQLiteDatabase
import com.example.nextrep.data.exercise.ExerciseDao
import com.example.nextrep.data.models.Exercise
import com.example.nextrep.data.models.Session
import com.example.nextrep.data.session.SessionDao
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import com.example.nextrep.data.models.SessionExerciseCrossRef

@Database(
    entities = [
        Exercise::class,
        Session::class,
        SessionExerciseCrossRef::class
    ],
    version = 3,
    exportSchema = false
)
abstract class AppDatabase : RoomDatabase() {

    abstract fun exerciseDao(): ExerciseDao
    abstract fun sessionDao(): SessionDao

    companion object {

        @Volatile
        private var INSTANCE: AppDatabase? = null

        fun buildDatabase(app: Application): AppDatabase {
            return INSTANCE ?: synchronized(this) {

                val instance = Room.databaseBuilder(
                    app,
                    AppDatabase::class.java,
                    "nextrep-db"
                )
                    .fallbackToDestructiveMigration()
                    .addCallback(object : RoomDatabase.Callback() {

                        override fun onCreate(db: SupportSQLiteDatabase) {
                            super.onCreate(db)

                            // Utiliser INSTANCE (elle sera initialisée juste après)
                            INSTANCE?.let { database ->
                                CoroutineScope(Dispatchers.IO).launch {
                                    val dao = database.exerciseDao()
                                    dao.insertExercise(
                                        Exercise(
                                            name = "Bench Press",
                                            description = "Test bench",
                                            series = 4,
                                            repetitions = 8
                                        )
                                    )
                                    dao.insertExercise(
                                        Exercise(
                                            name = "Squat",
                                            description = "Test squat",
                                            series = 3,
                                            repetitions = 10
                                        )
                                    )
                                }
                            }
                        }
                    })
                    .build()

                INSTANCE = instance
                instance
            }
        }
    }
}
