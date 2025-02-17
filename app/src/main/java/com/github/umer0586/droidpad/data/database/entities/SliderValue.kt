package com.github.umer0586.droidpad.data.database.entities

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.PrimaryKey
import kotlinx.serialization.Serializable


@Serializable
@Entity(
    foreignKeys = [

        ForeignKey(
            entity = ControlPad::class,
            parentColumns = ["id"],
            childColumns = ["controlPadId"],
            onDelete = ForeignKey.CASCADE
        ),

        ForeignKey(
            entity = ControlPadItem::class,
            parentColumns = ["id"],
            childColumns = ["controlPadItemId"],
            onDelete = ForeignKey.CASCADE
        )
    ]
)
data class SliderValue(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val controlPadId: Long,
    // Here controlPadItem id is controlPadItem with type = SLIDER
    val controlPadItemId: Long,
    val value: Float
)