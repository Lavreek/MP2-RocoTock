package com.example.mp2

import com.example.mp2.Entityes.EntityGoal
import com.example.mp2.Entityes.EntityNotes
import com.example.mp2.Entityes.EntityTasks
import com.example.mp2.GoalsLogic.GoalRecordEdit
import com.example.mp2.NotesLogic.NotesRecordEdit
import com.example.mp2.TasksLogic.TaskRecordEdit
import io.reactivex.Observable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import java.util.*

class HideAway {
    fun checkTime(st_day:Int, st_month:Int, st_year:Int, et_day:Int, et_month:Int, et_year:Int) : Boolean {
        val c = Calendar.getInstance()
        val c_year = c.get(Calendar.YEAR)
        val c_month = c.get(Calendar.MONTH)
        val c_day = c.get(Calendar.DAY_OF_MONTH)

        if (st_day != 0) {
            if (st_day < c_day || st_month < c_month || st_year < c_year) {
                return false
            }
        }
        if (et_day != 0) {
            if (et_day < c_day || et_month < c_month || et_year < c_year) {
                return false
            }
        }
        return true
    }

    fun returnStatus(result: Int) : String {
        if (result == 0) {
            return " - "
        }
        if (result == 1) {
            return " ~ "
        }
        if (result == 2) {
            return " + "
        }
        return ""
    }

    fun setArray(nodes :Array<String>, current :String) : Array<String?> {
        val RetArray = arrayOfNulls<String>(nodes.size)
        RetArray[0] = current
        var i = 1

        nodes.forEach {
            if (it != current) {
                RetArray[i] = it
                i++
            }
        }
        return RetArray
    }

    fun setNonArray(nodes :Array<String?>, current :String) : Array<String?> {
        val RetArray = arrayOfNulls<String>(nodes.size)
        RetArray[0] = current
        var i = 1

        nodes.forEach {
            if (it != current) {
                RetArray[i] = it
                i++
            }
        }
        return RetArray
    }

    fun deleteNoteRecord() {
        Observable.fromCallable {
            val note = NotesRecordEdit.CurrentSelectedNote as EntityNotes
            with(MainActivity.ADB?.notesDao()) {
                this?.delete(note)
            }
        }.subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread()).subscribe()
    }

    fun deleteGoalRecord() {
        Observable.fromCallable {
            val goal = GoalRecordEdit.CurrentSelectedGoal as EntityGoal
            with(MainActivity.ADB?.goalDao()) {
                this?.delete(goal)
            }
        }.subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread()).subscribe()
    }

    fun deleteTaskRecord() {
        Observable.fromCallable {
            val task = TaskRecordEdit.CurrentSelectedTask as EntityTasks
            with(MainActivity.ADB?.employeeDao()) {
                this?.delete(task)
            }
        }.subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread()).subscribe()
    }
}