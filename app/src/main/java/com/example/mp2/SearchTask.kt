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
import android.widget.ArrayAdapter
import android.widget.TextView
import android.widget.Toast
import com.example.mp2.GoalsLogic.GoalRecords
import com.example.mp2.TasksLogic.TaskRecordEdit
import com.example.mp2.TasksLogic.TaskRecordEdit.Companion.CurrentSelectedTask
import com.example.mp2.TasksLogic.TaskRecords
import io.reactivex.Observable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import kotlinx.android.synthetic.main.support_activity_search_task.*
import org.jetbrains.anko.noAnimation

class SearchTask : AppCompatActivity(), NavigationView.OnNavigationItemSelectedListener {
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

    private var DatabaseHandler = MainActivity.ADB?.employeeDao()
    private var SearchTaskAdapter: ArrayAdapter<String?>? = null
    private var TaskCount = 0
    private var CompletedCount = 0
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_search_task)


        val toolbar: Toolbar = findViewById(R.id.search_task_toolbar_search)
        setSupportActionBar(toolbar)

//        val toolbar : Toolbar = toolbar as Toolbar
//        setSupportActionBar(toolbar)

        val drawerLayout: DrawerLayout = findViewById(R.id.search_task_activity)
        val navView: NavigationView = findViewById(R.id.nav_view)
        val toggle = ActionBarDrawerToggle(
            this, drawerLayout, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close
        )
        drawerLayout.addDrawerListener(toggle)
        toggle.syncState()

        navView.setNavigationItemSelectedListener(this)

        et_search_task.addTextChangedListener(object : TextWatcher {
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
            }

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
            }

            override fun afterTextChanged(p0: Editable?) {
                showTaskList()
            }
        })

        listViewTask.setOnItemClickListener { parent, view, position, id ->
            try {
                val element = SearchTaskAdapter?.getItemId(position)
                val textView = view as TextView
                val ls = textView.text.toString()
                val delimiter = " | "
                val parts = ls.split(delimiter)

                Observable.fromCallable {
                    with(DatabaseHandler) { CurrentSelectedTask = this?.getTaskByCaption(parts[1], parts[2]) }
                }.subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread()).subscribe()

                val intent = Intent(this, TaskRecordEdit::class.java)
                intent.noAnimation()
                startActivity(intent)
            }
            catch(e:Throwable) {
                Toast.makeText(this, "Ошибка, даже не знаю почему :(", Toast.LENGTH_SHORT).show()
            }
        }

        showTaskList()
        setTaskSearch()
    }

    private fun showTaskList() {
        Observable.fromCallable {
            CompletedCount = DatabaseHandler?.getCountTagEntity(et_search_task.text.toString())!!.toInt()
            DatabaseHandler?.getTagEntity(et_search_task.text.toString())
        }.doOnNext { list ->

            TaskCount = list?.size!!.toInt()
            val ListItems = arrayOfNulls<String>(TaskCount)

            for (i in 0 until TaskCount) {
                val recipe = list[i]
                ListItems[i] = " | " + recipe.importance + " | " + recipe.caption.toString()
            }

            SearchTaskAdapter = ArrayAdapter(this, android.R.layout.simple_list_item_1, ListItems)
            this@SearchTask.runOnUiThread(Runnable {
                this.listViewTask?.adapter = SearchTaskAdapter
                st_progressBar.max = TaskCount
                st_progressBar.progress = CompletedCount
                var Result = 0
                if (TaskCount > 0 && CompletedCount > 0) {
                    Result = (100 / TaskCount) * CompletedCount
                }
                st_textView_completed.setText("Выполнено: $Result %")
            })
        }.subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread()).subscribe()
    }

    private fun setTaskSearch() {
        Observable.fromCallable { DatabaseHandler?.getEntityTags }.doOnNext{
                list ->
            val adapter = ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, list!!.toTypedArray())
            this@SearchTask.runOnUiThread{et_search_task.setAdapter(adapter)}
        }.subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread()).subscribe()
    }

    fun gotoSearch(v :View) { startActivity(Intent(this, SearchTask::class.java)) }
    fun gotoHome(v :View) { startActivity(Intent(this, MainActivity::class.java)) }
    fun gotoCalendar(v :View) { startActivity(Intent(this, SearchCalendars::class.java)) }
}
