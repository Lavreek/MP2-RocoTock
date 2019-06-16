package com.example.mp2.Daos

import android.arch.persistence.room.*
import com.example.mp2.Entityes.EntityGoal
import com.example.mp2.Entityes.EntityTasks

@Dao
interface GoalDao {

    @get:Query("SELECT * FROM entitygoal ")
    val getGoals : List<EntityGoal>

    @get:Query("Update EntityGoal Set goal_progress = (Select count(id_entityGoal) From EntityTasks Where id = id_entityGoal)")
    val Upsss222 : Int

//    @Query("UPDATE entitygoal SET goal_progress = (SELECT Count(id_entitygoal) FROM entitytasks WHERE entitytasks.id_entitygoal = :id) WHERE :id IN (SELECT id_entitygoal FROM entitytasks WHERE :id = entitytasks.id_entitygoal)")
//    fun setProgress(id: Int) : Int

//    @Query("UPDATE entitygoal SET goal_progress = (SELECT AVG(task_progress) FROM entitytasks Left join entitygoal on entitygoal.id = entitytasks.id_entitygoal GROUP BY entitygoal.id)")
//    fun setProgress(id: Int) : Int
    @Query("Select count(id_entityGoal) From EntityTasks Where :id = id_entityGoal")
    fun getCountTaskForGoal(id :Int) : Int

    @Query("UPDATE entitygoal SET goal_progress = (Select AVG(task_progress) From EntityTasks Where :id = id_entityGoal) Where id = :id")// Where :id = (Select id_entityGoal From entitytasks where id_entityGoal = :id)")//  Where :id In (SELECT id_entitygoal FROM entitytasks Where :id = id_entitygoal)  //where id_entitygoal in (Select *From entitygoal where (Update en)) entitygoal SET goal_progress = (SELECT AVG(task_progress) FROM entitytasks WHERE entitytasks.id_entitygoal = id) WHERE id IN (SELECT id_entitygoal FROM entitytasks WHERE id = entitytasks.id_entitygoal)")
    fun setProgress(id: Int)

    @Query("SELECT id FROM entitygoal WHERE goal_caption = :caption")
    fun getGoalsByCaption(caption: String) : Int

    @Query("SELECT * from entitygoal WHERE goal_caption = :caption")
    fun getGoalByCaption(caption: String?) : EntityGoal

    @Query("SELECT * from entitygoal WHERE id = :id")
    fun getGoalById(id: Int) : EntityGoal

    @Query("SELECT * from entitytasks WHERE id_entityGoal = :id")
    fun getGoalTasksById(id: Int) : List<EntityTasks>

    @Insert
    fun insert(entityGoal: EntityGoal)

    @Update
    fun update(entityGoal: EntityGoal)

    @Delete
    fun delete(entityGoal: EntityGoal)

    @Query("Delete from entitygoal")
    fun deleteTable()
}