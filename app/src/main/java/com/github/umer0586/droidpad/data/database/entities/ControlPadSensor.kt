package com.github.umer0586.droidpad.data.database.entities

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.PrimaryKey

@Entity(
    foreignKeys = [
        ForeignKey(
            entity = ControlPad::class,
            parentColumns = ["id"],
            childColumns = ["controlPadId"],
            onDelete = ForeignKey.CASCADE
        )
    ]
)
data class ControlPadSensor(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val controlPadId: Long,
    val sensorType: Int
)