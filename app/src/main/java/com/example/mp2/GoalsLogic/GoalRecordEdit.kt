package com.example.mp2.GoalsLogic

import android.content.Intent
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.Toolbar
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.ArrayAdapter
import android.widget.Toast
import com.example.mp2.*
import com.example.mp2.Entityes.EntityGoal
import io.reactivex.Observable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import kotlinx.android.synthetic.main.activity_goal_record_edit.*
import org.jetbrains.anko.noAnimation

class GoalRecordEdit : AppCompatActivity() {

    private var gre_listViewAdapter: ArrayAdapter<String?>? = null
    private var ReadyToEdit : Boolean = false
    private var help = HideAway()
    companion object {
        var CurrentSelectedGoal : EntityGoal? = null
    }

    override fun onOptionsItemSelected(item: MenuItem?): Boolean {
        when(item?.itemId){
            R.id.item_delete -> {
                val help = HideAway()
                help.deleteGoalRecord()
                startActivity(Intent(this, GoalRecords::class.java).noAnimation())
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
        setContentView(R.layout.activity_goal_record_edit)

        setLogic()
    }

    private fun setLogic() {
        setToolbar()

        Observable.fromCallable {
            MainActivity.ADB?.goalDao()?.getGoalTasksById(CurrentSelectedGoal?.id!!)
        }.doOnNext { list ->

            gre_editText_caption.setText(CurrentSelectedGoal?.goal_caption)

            val Count = list?.size!!.toInt()
            val ListItems = arrayOfNulls<String>(Count)
            val status = HideAway()
            val array = status.returnStatusCaption(CurrentSelectedGoal?.goal_status!!.toInt())
            val GoalItems = arrayOf(array)
            for (i in 0 until list.size) {
                val recipe = list[i]
                ListItems[i] = "${recipe.task_progress}% | ${recipe.importance} | ${recipe.caption.toString()}"
            }
            val adapter = ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, GoalItems)
            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
            gre_listViewAdapter = ArrayAdapter(this, android.R.layout.simple_list_item_1, ListItems)
            this@GoalRecordEdit.runOnUiThread{
                gre_listView.adapter = gre_listViewAdapter
                gre_statusSpinner.adapter = adapter
            }
        }.subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread()).subscribe()

        gre_fab_editStart.setOnClickListener {
            gre_fab_editStart.hide()
            gre_editText_caption.isEnabled = true
            ReadyToEdit = true
            setSpinner()
        }

        gre_fab_editEnd.setOnClickListener {
            updateGoal()
        }
    }

    private fun updateGoal() {
        Observable.fromCallable { }.doOnNext {
            try {
                if (gre_editText_caption.text.isNotEmpty()) {
                    Observable.fromCallable {
                        val goal = GoalRecordEdit.CurrentSelectedGoal as EntityGoal
                        goal.goal_caption = gre_editText_caption.text.toString()
                        goal.goal_status = help.returnStatusInt(gre_statusSpinner.selectedItem.toString())

                        with(MainActivity.ADB?.goalDao()) {
                            this?.update(goal)
                        }
                    }.doOnNext {
                        startActivity(Intent(this, MainActivity::class.java).noAnimation())
                    }.subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread()).subscribe()
                } else {
                    Toast.makeText(this, "Описание задачи не может быть пустым", Toast.LENGTH_SHORT).show()
                }
                Toast.makeText(this, "Задача успешно добавлена", Toast.LENGTH_SHORT).show()
            } catch (e: Throwable) {
                Toast.makeText(this, "Error", Toast.LENGTH_SHORT).show()
            }
        }.subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread()).subscribe()
    }

    private fun setSpinner() {
        if (!ReadyToEdit) {
            val nodes = arrayOf("${help.returnStatusCaption(CurrentSelectedGoal?.goal_progress!!)}")
            val adapter = ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, nodes)
            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
            gre_statusSpinner.adapter = adapter
        }
        if (ReadyToEdit) {
            val nodes = arrayOf("В процессе", "Выполнено", "Провалено")
            val adapter = ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, nodes)
            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
            gre_statusSpinner.adapter = adapter
        }
    }

    private fun setToolbar() {
        val toolbar: Toolbar = findViewById(R.id.gre_toolbar_default)
        toolbar.title = "Изменить цель"
        setSupportActionBar(toolbar)
    }

    fun gotoHome(v :View){ startActivity(Intent(this, MainActivity::class.java).noAnimation()) }
    fun gotoCalendar(v :View){ startActivity(Intent(this, SearchCalendars::class.java).noAnimation()) }
    fun gotoSearch(v :View){ startActivity(Intent(this, SearchTask::class.java).noAnimation()) }

}
