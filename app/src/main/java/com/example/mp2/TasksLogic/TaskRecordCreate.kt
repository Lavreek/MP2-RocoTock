package com.example.mp2.TasksLogic

import android.app.DatePickerDialog
import android.content.Intent
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.view.View
import android.widget.ArrayAdapter
import android.widget.Toast
import com.example.mp2.*
import com.example.mp2.Entityes.EntityTasks
import io.reactivex.Observable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import kotlinx.android.synthetic.main.activity_task_record_create.*
import org.jetbrains.anko.noAnimation
import java.util.*
import java.util.Calendar.getInstance as CurrentTime


class TaskRecordCreate : AppCompatActivity() {

    private val centime: Calendar = Calendar.getInstance()
    private val c_year = centime.get(Calendar.YEAR)
    private val c_month = centime.get(Calendar.MONTH)
    private val c_day = centime.get(Calendar.DAY_OF_MONTH)
    private var st_day = c_day
    private var st_month = c_month
    private var st_year = c_year
    private var et_day : Int = 0
    private var et_month : Int = 0
    private var et_year : Int = 0

    private var CTime: Boolean = true
    private val DatabaseHandler = MainActivity.ADB?.employeeDao()

    private var GoalID : Int = 0
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_task_record_create)

        setGoalSpinner()
        setPrioritySpinnerMenu()
        setTagTextView()
        setButtonStartTimeMethod()
        setButtonEndTimeMethod()
        trc_fab_add.setOnClickListener {
            setInsertMethod()
        }
    }

    fun gotoHome(v :View) { startActivity(Intent(this, MainActivity::class.java).noAnimation()) }

    fun gotoCalendar(v :View) { startActivity(Intent(this, SearchCalendars::class.java).noAnimation()) }

    fun gotoSearch(v :View){ startActivity(Intent(this, SearchTask::class.java).noAnimation()) }

    fun gotoPersonalAccount(v :View) { startActivity(Intent(this, PersonalAccount::class.java).noAnimation()) }

    private fun setButtonStartTimeMethod() {
        val c = Calendar.getInstance()
        val year = c.get(Calendar.YEAR)
        val month = c.get(Calendar.MONTH)
        val day = c.get(Calendar.DAY_OF_MONTH)

        trc_editText_start_time.setOnClickListener {
            val dpd = DatePickerDialog(this, DatePickerDialog.OnDateSetListener {  view, myear, mmonth, mday ->
                trc_editText_start_time.setText("" + mday + "." + mmonth.plus(1) + "." + myear)
                st_day = mday
                st_month = mmonth
                st_year = myear
            }, year, month, day)
            dpd.show()

        }
    }

    private fun setButtonEndTimeMethod() {
        val c = Calendar.getInstance()
        val year = c.get(Calendar.YEAR)
        val month = c.get(Calendar.MONTH)
        val day = c.get(Calendar.DAY_OF_MONTH)

        trc_editText_endtime.setOnClickListener {
            val dpd = DatePickerDialog(this, DatePickerDialog.OnDateSetListener {  view, myear, mmonth, mday ->
                trc_editText_endtime.setText("" + mday + "." + mmonth.plus(1) + "." + myear)
                et_day = mday
                et_month = mmonth
                et_year = myear
            }, year, month, day)
            dpd.show()
        }
    }

    private fun setInsertMethod() {
        val help = HideAway()
        CTime = help.checkTime(st_day, st_month, st_year, et_day, et_month, et_year)
        if (CTime) {
            try {
                if (trc_editText_caption.text.toString() != "" && CTime) {
                    Observable.fromCallable {
                        val goal_id = MainActivity.ADB?.goalDao()?.getGoalsByCaption(trc_goalSpinner.selectedItem.toString())!!.toInt()
                        val task = EntityTasks(
                            login = MainActivity.Login,
                            caption = trc_editText_caption.text.toString(),
                            importance = trc_taskSpinner.selectedItem.toString(),
                            creation_time = CurrentTime().time.toLocaleString(),
                            tag = trc_editText_tag.text.toString(),
                            task_progress = 0,
                            id_entitygoal = goal_id,
                            start_time_day = st_day,
                            start_time_month = st_month,
                            start_time_year = st_year,
                            end_time_day = et_day,
                            end_time_month = et_month,
                            end_time_year = et_year
                        )
                        with(MainActivity.ADB?.employeeDao()) { this?.insert(task) }
                        startActivity(Intent(this, MainActivity::class.java).noAnimation())
                    }.subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread()).subscribe()
                }
                if (trc_editText_caption.text.toString() == "") {
                    Toast.makeText(this, "Описание не может быть пустым", Toast.LENGTH_SHORT).show()
                }
            } catch (e: Throwable) { Toast.makeText(this, "Error", Toast.LENGTH_SHORT).show() }
        }
        else
            Toast.makeText(this, "Ошибка во времени", Toast.LENGTH_SHORT).show()
    }

    private fun setTagTextView() {
        Observable.fromCallable { DatabaseHandler?.getDistinctTaskTags }.doOnNext{
                list ->
            val adapter = ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, list!!.toTypedArray())
            this@TaskRecordCreate.runOnUiThread{trc_editText_tag.setAdapter(adapter)}
        }.subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread()).subscribe()
    }

    private fun setGoalSpinner() {
        Observable.fromCallable { MainActivity.ADB?.goalDao()?.getGoalList }.doOnNext{ list ->

            val Count = list?.size!!.toInt() + 1

            val ListItems = arrayOfNulls<String>(Count)
            ListItems[0] = " "

            for (i in 0 until list.size) {
                if (list.size == 0) {
                    ListItems[i] = "Отсутствуют"
                }
                else {
                    val recipe = list[i]
                    ListItems[i + 1] = recipe.goal_caption
                }
            }

            val adapter =  ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, ListItems)
            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
            this@TaskRecordCreate.runOnUiThread{ trc_goalSpinner.adapter = adapter }
        }.subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread()).subscribe()
    }

    private fun setPrioritySpinnerMenu() {
        val nodes = arrayOf("Нет", "Низкий", "Средний", "Высокий")
        val adapter =  ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, nodes)
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        trc_taskSpinner.adapter = adapter
    }
}