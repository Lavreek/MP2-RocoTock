package com.example.mp2.TasksLogic

import android.app.DatePickerDialog
import android.content.Intent
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.Toolbar
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.ArrayAdapter
import android.widget.SeekBar
import android.widget.SeekBar.OnSeekBarChangeListener
import android.widget.TextView
import android.widget.Toast
import com.example.mp2.*
import com.example.mp2.Entityes.EntityTasks
import io.reactivex.Observable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import kotlinx.android.synthetic.main.activity_task_record_edit.*
import org.jetbrains.anko.noAnimation
import java.util.*

class TaskRecordEdit : AppCompatActivity(), OnSeekBarChangeListener {

    private var DatabaseHandler = MainActivity.ADB?.employeeDao()
    private var ReadyToEdit : Boolean = false
    private var CTime: Boolean = true
    private var seekbarView: SeekBar? = null
    private var progressView: TextView? = null
    private val CurrentTime = Calendar.getInstance()
    private var c_year = CurrentTime.get(Calendar.YEAR)
    private var c_month = CurrentTime.get(Calendar.MONTH)
    private var c_day = CurrentTime.get(Calendar.DAY_OF_MONTH)
    private var st_day = CurrentSelectedTask?.start_time_day as Int
    private var st_month = CurrentSelectedTask?.start_time_month as Int
    private var st_year = CurrentSelectedTask?.start_time_year as Int
    private var et_day = CurrentSelectedTask?.end_time_day as Int
    private var et_month = CurrentSelectedTask?.end_time_month as Int
    private var et_year = CurrentSelectedTask?.end_time_year as Int
    private val help = HideAway()

    companion object {
        var CurrentSelectedTask : EntityTasks? = null
    }

    override fun onOptionsItemSelected(item: MenuItem?): Boolean {
        when(item?.itemId){
            R.id.item_delete -> { val help = HideAway()
                help.deleteTaskRecord()
                startActivity(Intent(this, MainActivity::class.java).noAnimation())
            }
        }
        return super.onOptionsItemSelected(item)
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.toolbar_nodes_delete, menu)
        return true
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_task_record_edit)

        setToolbar()
        setLogic()
    }

    override fun onProgressChanged(seekBar: SeekBar, progress: Int,
                                   fromUser: Boolean) {
        progressView!!.text = progress.toString()
    }

    override fun onStartTrackingTouch(seekBar: SeekBar) {
    }

    override fun onStopTrackingTouch(seekBar: SeekBar) {
    }

    private fun setToolbar() {
        val toolbar: Toolbar = findViewById(R.id.tre_toolbar_default)
        toolbar.title = "Изменение задачи"
        setSupportActionBar(toolbar)
    }

    private fun setLogic() {
        tse_editText_caption.setText(CurrentSelectedTask?.caption)
        tse_editText_start_time.setText("" + CurrentSelectedTask?.start_time_day + "." + CurrentSelectedTask?.start_time_month?.plus(1) + "." + CurrentSelectedTask?.start_time_year)

        if (CurrentSelectedTask?.end_time_day == 0)
            tse_editText_end_time.setText("Не определенно")
        else
            tse_editText_end_time.setText("" + CurrentSelectedTask?.end_time_day + "." + CurrentSelectedTask?.end_time_month?.plus(1) + "." + CurrentSelectedTask?.end_time_year)

        setSpinners()

        setButtonStartTimeMethod()
        setTagTextView()
        setButtonEndTimeMethod()

        tse_progressBar.progress = CurrentSelectedTask?.task_progress!!.toInt()
        tse_textView_progressBar.setText("" + CurrentSelectedTask?.task_progress!!.toInt())
        tre_seekBar.progress = CurrentSelectedTask?.task_progress!!.toInt()

        tre_fab_editStart.setOnClickListener {
            reverseComponents()
        }

        tre_fab_editEnd.setOnClickListener {
            setUpdateMethod()
        }

        progressView = this.tre_textView_seekBar
        seekbarView = this.tre_seekBar
        seekbarView!!.setOnSeekBarChangeListener(this)
        seekbarView!!.visibility = View.INVISIBLE
    }

    private fun setTagTextView() {
        Observable.fromCallable { DatabaseHandler?.getDistinctTaskTags }.doOnNext{
                list ->
            val adapter = ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, list!!.toTypedArray())
            this@TaskRecordEdit.runOnUiThread{tse_editText_setTag.setAdapter(adapter); tse_editText_setTag.setText(
                CurrentSelectedTask?.tag.toString())}
        }.subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread()).subscribe()
    }

    private fun setSpinners() {
        setSpinnerPriority()
        setSpinnerStatus()
        setSpinnerGoal()
    }

    private fun setUpdateMethod() {
        CTime = help.checkTime(st_day, st_month, st_year, et_day, et_month, et_year)

//        if (CTime) {
            try {
                if (tse_editText_caption.text.isNotEmpty()) {
                    Observable.fromCallable {
                        val entityId = MainActivity.ADB?.goalDao()?.getGoalByCaption(tre_goalspinner.selectedItem.toString())
                        val uGoal = CurrentSelectedTask as EntityTasks
                        uGoal.login = MainActivity.Login
                        uGoal.caption = tse_editText_caption.text.toString()
                        uGoal.importance = tse_prioritySpinner.selectedItem.toString()
                        uGoal.status = tse_statusSpinner.selectedItem.toString()
                        uGoal.creation_time = CurrentSelectedTask?.creation_time.toString()
                        uGoal.tag = tse_editText_setTag.text.toString()
                        if (entityId != null) {
                            uGoal.id_entitygoal = entityId.id
                        }
                        if (entityId == null) {
                            uGoal.id_entitygoal = 0
                        }
                        if (st_day > CurrentTime.get(Calendar.DAY_OF_MONTH) && st_month > CurrentTime.get(Calendar.MONTH) && st_year > CurrentTime.get(Calendar.YEAR) ) {
                            uGoal.start_time_day = st_day
                            uGoal.start_time_month = st_month
                            uGoal.start_time_year = st_year
                        }

                        uGoal.task_progress = tre_seekBar.progress

                        if (et_day > CurrentTime.get(Calendar.DAY_OF_MONTH) && et_month > CurrentTime.get(Calendar.MONTH) && et_year > CurrentTime.get(Calendar.YEAR) ) {
                            uGoal.end_time_day = et_day
                            uGoal.end_time_month = et_month
                            uGoal.end_time_year = et_year
                        }

                        if (tse_statusSpinner.selectedItem.toString() == "Выполнено") {
                            uGoal.completion_time_day = c_day
                            uGoal.completion_time_month = c_month
                            uGoal.completion_time_year = c_year
                        }

                        with(DatabaseHandler) {
                            this?.update(uGoal)
                        }
                    }.doOnNext { startActivity(Intent(this, MainActivity::class.java).noAnimation())
                    }.subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread()).subscribe()
                } else {
                    Toast.makeText(this, "Описание задачи не может быть пустым", Toast.LENGTH_SHORT).show()
                }

                Toast.makeText(this, "Задача успешно добавлена", Toast.LENGTH_SHORT).show()
            } catch (e: Throwable) {
                Toast.makeText(this, "Error", Toast.LENGTH_SHORT).show()
            }
//        }
//        else
//            Toast.makeText(this, "Ошибка во времени", Toast.LENGTH_SHORT).show()
    }

    private fun setSpinnerPriority() {
        if (!ReadyToEdit) {
            val nodes = arrayOf("${CurrentSelectedTask?.importance}")
            val adapter = ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, nodes)
            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
            tse_prioritySpinner.adapter = adapter
        }
        if (ReadyToEdit) {
            val nodes = arrayOf("Нет", "Низкий", "Средний", "Высокий")
            val itemList = help.setArray(nodes, CurrentSelectedTask?.importance!!)
            val adapter = ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, itemList)
            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
            tse_prioritySpinner.adapter = adapter
        }
    }

    private fun setSpinnerGoal() {
        if (!ReadyToEdit) {
            if (CurrentSelectedTask?.id_entitygoal!! > 0) {
                Observable.fromCallable {
                    val pos = CurrentSelectedTask?.id_entitygoal!!
                    MainActivity.ADB?.goalDao()?.getGoalById(pos)
                }.doOnNext { entity ->
                    val listItems = arrayOfNulls<String>(1)
                    for (i in 0 until 1) {
                        listItems[i] = entity?.goal_caption
                    }

                    val adapter = ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, listItems)
                    adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
                    this@TaskRecordEdit.runOnUiThread { tre_goalspinner.adapter = adapter }
                }.subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread()).subscribe()
            }
        }
        if (ReadyToEdit) {
            Observable.fromCallable {
                MainActivity.ADB?.goalDao()?.getGoalList
            }.doOnNext { list ->

                val count = list?.size!!.toInt() + 1
                val listItems = arrayOfNulls<String>(count)
                listItems[0] = " "
                for (i in 0 until list.size) {
                    val recipe = list[i]
                    if (CurrentSelectedTask?.id_entitygoal != 0 && recipe.id == CurrentSelectedTask?.id_entitygoal) {
                        listItems[0] = recipe.goal_caption
                    }

                    listItems[i + 1] = recipe.goal_caption
                    if (listItems[i + 1] == listItems[0]) {
                        listItems[i + 1] = " "
                    }
                }
                val adapter = ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, listItems)
                adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
                this@TaskRecordEdit.runOnUiThread{ tre_goalspinner.adapter = adapter }
            }.subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread()).subscribe()
        }
    }

    private fun setSpinnerStatus() {
        if (!ReadyToEdit) {
            val nodes = arrayOf("${CurrentSelectedTask?.status}")
            val adapter = ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, nodes)
            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
            tse_statusSpinner.adapter = adapter
        }
        if (ReadyToEdit) {
            val nodes = arrayOf("В процессе", "Выполнено", "Провалено")
            val itemList = help.setArray(nodes, CurrentSelectedTask?.status!!)
            val adapter = ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, itemList)
            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
            tse_statusSpinner.adapter = adapter
        }
    }



    private fun setButtonStartTimeMethod() {
        val c = Calendar.getInstance()
        val year = c.get(Calendar.YEAR)
        val month = c.get(Calendar.MONTH)
        val day = c.get(Calendar.DAY_OF_MONTH)

        tse_editText_start_time.setOnClickListener {
            val dpd = DatePickerDialog(this, DatePickerDialog.OnDateSetListener { view, myear, mmonth, mday ->
                tse_editText_start_time.setText("" + mday + "." + mmonth.plus(1) + "." + myear)
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

        tse_editText_end_time.setOnClickListener {
            val dpd = DatePickerDialog(this, DatePickerDialog.OnDateSetListener { view, myear, mmonth, mday ->
                tse_editText_end_time.setText("" + mday + "." + mmonth.plus(1) + "." + myear)
                et_day = mday
                et_month = mmonth
                et_year = myear
            }, year, month, day)
            dpd.show()
        }
    }

    private fun reverseComponents(){
        ReadyToEdit = true
        tse_editText_caption.isEnabled = true
        if (CurrentSelectedTask?.start_time_day != 0)
        tse_editText_start_time.isEnabled = true
        tse_editText_end_time.isEnabled = true
        tse_editText_setTag.isEnabled = true
        tre_fab_editStart.hide()
        tse_progressBar.visibility = View.INVISIBLE
        tse_textView_progressBar.visibility = View.INVISIBLE
        seekbarView!!.visibility = View.VISIBLE
        progressView!!.visibility = View.VISIBLE
        tre_fab_editEnd.show()
        setSpinners()
    }

    fun gotoHome(v :View){ startActivity(Intent(this, MainActivity::class.java).noAnimation()) }
    fun gotoCalendar(v :View){ startActivity(Intent(this, SearchCalendars::class.java).noAnimation()) }
    fun gotoSearch(v :View){ startActivity(Intent(this, SearchTask::class.java).noAnimation()) }
}

