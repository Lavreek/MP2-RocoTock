package com.example.mp2.Entityes

import android.arch.persistence.room.ColumnInfo
import android.arch.persistence.room.Entity
import android.arch.persistence.room.PrimaryKey

@Entity
data class EntityTasks(
    @PrimaryKey(autoGenerate = true) var id: Int = 0,
    @ColumnInfo(name = "login") var login : String? = "",
    @ColumnInfo(name = "caption") var caption : String? = "",
    @ColumnInfo(name = "efficiency") var efficiency : String? = "",
    @ColumnInfo(name = "status") var status : String? = "В процессе",
    @ColumnInfo(name = "importance") var importance : String? = "Нет",
    @ColumnInfo(name = "task_progress") var task_progress : Int = 0,
    @ColumnInfo(name = "tag") var tag : String? = "",
    @ColumnInfo(name = "creation_time") var creation_time : String,
    @ColumnInfo(name = "completion_time_day") var completion_time_day : Int = 0,
    @ColumnInfo(name = "completion_time_month") var completion_time_month : Int = 0,
    @ColumnInfo(name = "completion_time_year") var completion_time_year : Int = 0,
    @ColumnInfo(name = "start_time_day") var start_time_day : Int,
    @ColumnInfo(name = "start_time_month") var start_time_month : Int,
    @ColumnInfo(name = "start_time_year") var start_time_year : Int,
    @ColumnInfo(name = "end_time_day") var end_time_day : Int,
    @ColumnInfo(name = "end_time_month") var end_time_month : Int,
    @ColumnInfo(name = "end_time_year") var end_time_year : Int,
    @ColumnInfo(name = "id_entityGoal") var id_entitygoal : Int = 0
)