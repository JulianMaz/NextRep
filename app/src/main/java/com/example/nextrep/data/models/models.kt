package com.example.nextrep.data.models

import androidx.room.Embedded
import androidx.room.Entity
import androidx.room.Junction
import androidx.room.PrimaryKey
import androidx.room.Relation

@Entity(tableName = "exercises")
data class Exercise(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val name: String,
    val description: String,
    val series: Int,
    val repetitions: Int,
    val photoUri: String? = null
)


@Entity(tableName = "sessions")
data class Session(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val name: String,
    val date: String
)

@Entity(
    tableName = "session_exercise_crossref",
    primaryKeys = ["sessionId", "exerciseId"]
)
data class SessionExerciseCrossRef(
    val sessionId: Int,
    val exerciseId: Int
)

data class SessionWithExercises(
    @Embedded val session: Session,
    @Relation(
        parentColumn = "id",
        entity = Exercise::class,
        entityColumn = "id",
        associateBy = Junction(
            value = SessionExerciseCrossRef::class,
            parentColumn = "sessionId",
            entityColumn = "exerciseId"
        )
    )
    val exercises: List<Exercise>
)