package com.example.mp2.Entityes

import android.arch.persistence.room.ColumnInfo
import android.arch.persistence.room.Entity
import android.arch.persistence.room.PrimaryKey

@Entity
data class EntityNotes(
    @PrimaryKey(autoGenerate = true) var id: Int = 0,
    @ColumnInfo(name = "notes_caption") var notes_caption : String? = ""
)