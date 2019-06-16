package com.example.mp2

import android.content.Intent
import android.os.Bundle
import android.support.design.widget.NavigationView
import android.support.v4.view.GravityCompat
import android.support.v4.widget.DrawerLayout
import android.support.v7.app.ActionBarDrawerToggle
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.Toolbar
import android.text.Editable
import android.text.TextWatcher
import android.view.MenuItem
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.TextView
import android.widget.Toast
import com.example.mp2.Entityes.EntityGoal
import com.example.mp2.Entityes.EntityTasks
import com.example.mp2.GoalsLogic.GoalRecordEdit
import com.example.mp2.GoalsLogic.GoalRecordEdit.Companion.CurrentSelectedGoal
import com.example.mp2.GoalsLogic.GoalRecords
import com.example.mp2.TasksLogic.TaskRecordEdit
import com.example.mp2.TasksLogic.TaskRecords
import io.reactivex.Observable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import kotlinx.android.synthetic.main.support_activity_search_task.*
import org.jetbrains.anko.noAnimation

class SearchTask : AppCompatActivity(), NavigationView.OnNavigationItemSelectedListener {

    private var DatabaseHandler = MainActivity.ADB?.employeeDao()
    private var SearchTaskAdapter: ArrayAdapter<String?>? = null
    private var CompletedCount = 0
    private var taskList: List<EntityTasks>? = null
    private var goalList: List<EntityGoal>? = null

    override fun onNavigationItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.nav_goal -> { startActivity(Intent(this, GoalRecords::class.java).noAnimation()) }
            R.id.nav_task -> { startActivity(Intent(this, TaskRecords::class.java).noAnimation()) }
            R.id.nav_stats -> { startActivity(Intent(this,PersonalAccount::class.java).noAnimation()) }
            R.id.nav_settings -> { startActivity(Intent(this, OtherSettings::class.java).noAnimation()) }
        }
        val drawerLayout: DrawerLayout = findViewById(R.id.search_task_activity)
        drawerLayout.closeDrawer(GravityCompat.START)
        return true
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_search_task)

        setLogic()
    }

    private fun setLogic() {
        setToolbar()

        et_search_task.addTextChangedListener(object : TextWatcher {
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) { }
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) { }
            override fun afterTextChanged(p0: Editable?) { showTaskList() }
        })

        st_spinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(adapterView: AdapterView<*>, view: View, i: Int, l: Long) { setTaskSearch() }
            override fun onNothingSelected(adapterView: AdapterView<*>) { }
        }

        listViewTask.setOnItemClickListener { parent, view, position, id ->
            try {
                val textView = view as TextView
                val ls = textView.text.toString()
                val delimiter = " | "
                val parts = ls.split(delimiter)

                if (st_spinner.selectedItem.toString() == "Метки") {
                    Observable.fromCallable {
                        try {
                            with(DatabaseHandler) { TaskRecordEdit.CurrentSelectedTask = this?.getTaskByCaption(parts[1], parts[2] + "%") }
                            startActivity(Intent(this, TaskRecordEdit::class.java).noAnimation())
                        } catch (e : Throwable) { }
                    }.subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread()).subscribe()
                }
                if (st_spinner.selectedItem.toString() == "Цели") {
                    Observable.fromCallable {
                        try {
                            with(MainActivity.ADB?.goalDao()) { CurrentSelectedGoal = this?.getGoalByCaption(parts[1]) }
                            startActivity(Intent(this, GoalRecordEdit::class.java).noAnimation())
                        } catch (e : Throwable) { }
                    }.subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread()).subscribe()
                }
            }
            catch(e:Throwable) {
                Toast.makeText(this, "Ошибка, даже не знаю почему :(", Toast.LENGTH_SHORT).show()
            }
        }

        showTaskList()
        setSpinner()
        setTaskSearch()
    }

    private fun setSpinner() {
        val nodes = arrayOf("Метки", "Цели")
        val adapter =  ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, nodes)
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        st_spinner.adapter = adapter
    }

    private fun setToolbar() {
        val toolbar: Toolbar = findViewById(R.id.search_task_toolbar_search)
        setSupportActionBar(toolbar)

        val drawerLayout: DrawerLayout = findViewById(R.id.search_task_activity)
        val navView: NavigationView = findViewById(R.id.nav_view)
        val toggle = ActionBarDrawerToggle(
            this, drawerLayout, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close
        )
        drawerLayout.addDrawerListener(toggle)
        toggle.syncState()

        navView.setNavigationItemSelectedListener(this)
    }

    private fun setList(taskList: List<EntityTasks>? = null, goalList: List<EntityGoal>? = null) : Array<String?> {
        var ListItems : Array<String?> = arrayOf("")

        if (st_spinner.selectedItem.toString() == "Метки") {
            val count = taskList?.size!!.toInt()
            ListItems = arrayOfNulls(taskList.size)

            for (i in 0 until count) {
                val recipe = taskList!![i]
                ListItems[i] = "${recipe.task_progress}% | ${recipe.importance} | ${recipe.caption.toString()}"
            }
        }
        if (st_spinner.selectedItem.toString() == "Цели" && goalList!!.size > 0) {
            if (et_search_task.text.isNotEmpty()) {
                val count = goalList?.size!!.toInt()
                ListItems = arrayOfNulls(goalList.size)
                val help = HideAway()

                for (i in 0 until count) {
                    val recipe = goalList!![i]
                    ListItems[i] = "${help.returnStatusCaption(recipe.goal_status)} ( ${recipe.goal_progress}% ) | ${recipe.goal_caption}"
                }
            }
        }

        return ListItems
    }

    private fun showTaskList() {
        Observable.fromCallable {
            CompletedCount = DatabaseHandler?.getCountTagEntity(et_search_task.text.toString())!!.toInt()
            taskList = DatabaseHandler?.getTagEntity(et_search_task.text.toString())
            goalList = MainActivity.ADB?.goalDao()?.getCaptionGoalList(et_search_task.text.toString())
        }.doOnNext{
            val list = setList(taskList, goalList)
            if (list.isNotEmpty())
            {
                SearchTaskAdapter = ArrayAdapter(this, android.R.layout.simple_list_item_1, list)
                this@SearchTask.runOnUiThread {
                    this.listViewTask?.adapter = SearchTaskAdapter
                }
            }
        }.subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread()).subscribe()
    }

    private fun setTaskSearch() {
        if (st_spinner.selectedItem.toString() == "Метки") {
            Observable.fromCallable { DatabaseHandler?.getEntityTags }.doOnNext { list ->
                val adapter = ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, list!!.toTypedArray())
                this@SearchTask.runOnUiThread { et_search_task.setAdapter(adapter) }
            }.subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread()).subscribe()
        }
        if (st_spinner.selectedItem.toString() == "Цели") {
            Observable.fromCallable { MainActivity.ADB?.goalDao()?.getGoalCaptions }.doOnNext { list ->
                val adapter = ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, list!!.toTypedArray())
                this@SearchTask.runOnUiThread { et_search_task.setAdapter(adapter) }
            }.subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread()).subscribe()
        }
    }

    fun gotoSearch(v :View) { startActivity(Intent(this, SearchTask::class.java)) }
    fun gotoHome(v :View) { startActivity(Intent(this, MainActivity::class.java)) }
    fun gotoCalendar(v :View) { startActivity(Intent(this, SearchCalendars::class.java)) }
}
