package com.example.mp2.Daos

import android.arch.persistence.room.*
import com.example.mp2.Entityes.EntityNotes

@Dao
interface NoteDao {

    @get:Query("SELECT * FROM entitynotes ")
    val getNotes : List<EntityNotes>

    @Query("SELECT * from entitynotes WHERE notes_caption = :caption")
    fun getNoteByCaption(caption: String?) : EntityNotes

    @Insert
    fun insert(entityNotes: EntityNotes)

    @Update
    fun update(entityNotes: EntityNotes)

    @Delete
    fun delete(entityNotes: EntityNotes)

    @Query("Delete from entitynotes")
    fun deleteTable()
}