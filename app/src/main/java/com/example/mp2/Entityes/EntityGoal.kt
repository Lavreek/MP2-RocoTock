package com.example.mp2.Entityes

import android.arch.persistence.room.ColumnInfo
import android.arch.persistence.room.Entity
import android.arch.persistence.room.Index
import android.arch.persistence.room.PrimaryKey

@Entity(indices = [Index(value = ["goal_caption"], unique = true)])
data class EntityGoal(
    @PrimaryKey(autoGenerate = true) var id: Int = 0,
    @ColumnInfo(name = "goal_status") var goal_status : Int = 1,
    @ColumnInfo(name = "goal_caption") var goal_caption : String? = "",
    @ColumnInfo(name = "goal_progress") var goal_progress : Int = 0
)