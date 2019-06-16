package com.example.mp2.Daos

import android.arch.persistence.room.*
import com.example.mp2.Entityes.EntityGoal
import com.example.mp2.Entityes.EntityTasks

@Dao
interface TaskDao {

    @get:Query("SELECT * FROM entitytasks ")
    val getTasks : List<EntityTasks>

    @get:Query("SELECT DISTINCT tag FROM entitytasks")
    val getEntityTags : List<String>

    @Query("SELECT * FROM entitytasks WHERE tag = :tag")
    fun getTagEntity(tag: String?) : List<EntityTasks>

    @Query("SELECT count(id) FROM entitytasks WHERE tag = :tag and status = 'Выполнено'")
    fun getCountTagEntity(tag: String?) : Int

    @get:Query("SELECT count(id) FROM entitytasks WHERE status = 'В процессе'")
    val getCurrentCount : Long

    @get:Query("SELECT count(id) FROM entitytasks WHERE status = 'Выполнено'")
    val getCompletedCount : Long

    @get:Query("SELECT count(id) FROM entitytasks WHERE status = 'Провалено'")
    val getFallenCount : Long

    @get:Query("SELECT count(id) FROM entitytasks WHERE task_progress > 50")
    val getProgressCount : Long

    @Query("UPDATE entitytasks SET status = 'Провалено' WHERE end_time_day < :day and end_time_month < :month and end_time_year < :year and end_time_day <> 0 and status <> 'Выполнено'")
    fun changeEntityStatus(day: Int, month: Int, year: Int)

    @Query("SELECT * FROM entitytasks WHERE login = :login and start_time_day = :day and start_time_month = :month and start_time_year = :year and status = :status")
    fun getTodayEntity(login: String?, day: Int, month: Int, year: Int, status: String?) : List<EntityTasks>

    @Query("SELECT * FROM entitytasks WHERE status = :status")
    fun getStatusEntity(status: String?) : List<EntityTasks>

    @Query("SELECT * FROM entitytasks WHERE start_time_day = :day and start_time_month = :month and start_time_year = :year")
    fun getDateEntity(day: Int, month: Int, year: Int) : List<EntityTasks>

    @Query("SELECT * from entitytasks WHERE importance = :importance and caption LIKE :caption")
    fun getTaskByCaption(importance: String?, caption: String?) : EntityTasks

    @Query("SELECT count(id) from entitytasks WHERE ABS(:day - completion_time_day) < 4")
    fun getCompletedForThreeDays(day: Int) : Int

    @Query("SELECT count(id) from entitytasks WHERE ABS(:day - completion_time_day) < 8")
    fun getCompletedForWeek(day: Int) : Int

    @Query("SELECT count(id) from entitytasks WHERE completion_time_month = :month")
    fun getCompletedForMonth(month: Int) : Int

    @get:Query("SELECT AVG(goal_progress) from EntityGoal")
    val getAVGGoals : Int

    @get:Query("SELECT * FROM EntityGoal")
    val getBestGoal : List<EntityGoal>

    @get:Query("SELECT count(id) FROM EntityGoal")
    val getCountGoal : Int

//    @get:Query("SELECT count(id), goal_caption FROM entitytasks Inner Join entitygoal on entitytasks.id_entityGoal = entitygoal.id GROUP BY id_entityGoal")
//    val getCountTaskByGoal: List<Any>

    @Insert
    fun insert(entityTasks: EntityTasks)

    @Update
    fun update(entityTasks: EntityTasks)

    @Delete
    fun delete(entityTasks: EntityTasks)

    @Query("Delete from entitytasks")
    fun deleteTable()
}



