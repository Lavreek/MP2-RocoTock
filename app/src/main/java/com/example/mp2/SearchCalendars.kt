package com.example.mp2

import android.content.Intent
import android.os.Bundle
import android.support.design.widget.NavigationView
import android.support.v4.view.GravityCompat
import android.support.v4.widget.DrawerLayout
import android.support.v7.app.ActionBarDrawerToggle
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.Toolbar
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
import kotlinx.android.synthetic.main.support_activity_calendar.*
import org.jetbrains.anko.noAnimation
import java.util.*



class SearchCalendars : AppCompatActivity(), NavigationView.OnNavigationItemSelectedListener {
    override fun onNavigationItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.nav_goal -> { startActivity(Intent(this, GoalRecords::class.java).noAnimation()) }
            R.id.nav_task -> { startActivity(Intent(this, TaskRecords::class.java).noAnimation()) }
            R.id.nav_stats -> { startActivity(Intent(this,PersonalAccount::class.java).noAnimation()) }
            R.id.nav_settings -> { startActivity(Intent(this, OtherSettings::class.java).noAnimation()) }
        }
        val drawerLayout: DrawerLayout = findViewById(R.id.calendar_activity)
        drawerLayout.closeDrawer(GravityCompat.START)
        return true
    }

    private var TaskAdapter: ArrayAdapter<String?>? = null

    private val DatabaseHandler = MainActivity.ADB?.employeeDao()
    private val CurrentDate = Calendar.getInstance()
    private var year = CurrentDate.get(Calendar.YEAR)
    private var month = CurrentDate.get(Calendar.MONTH)
    private var day = CurrentDate.get(Calendar.DAY_OF_MONTH)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_calendar)
        val toolbar: Toolbar = findViewById(R.id.calendar_toolbar_calendar)
        setSupportActionBar(toolbar)

//        val toolbar : Toolbar = toolbar as Toolbar
//        setSupportActionBar(toolbar)

        val drawerLayout: DrawerLayout = findViewById(R.id.calendar_activity)
        val navView: NavigationView = findViewById(R.id.nav_view)
        val toggle = ActionBarDrawerToggle(
            this, drawerLayout, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close
        )
        drawerLayout.addDrawerListener(toggle)
        toggle.syncState()

        navView.setNavigationItemSelectedListener(this)

        cv_TaskCalendar.setOnDateChangeListener { view, tc_year, tc_month, tc_dayOfMonth ->
            year = tc_year; month = tc_month; day = tc_dayOfMonth

            runOnUiThread {
                backgroundThreadProcessing()
            }
        }

        lv_CalendarTaskList.setOnItemClickListener { parent, view, position, id ->
            try {
                val element = TaskAdapter?.getItemId(position)
                val textView = view as TextView
                val ls = textView.text.toString()
                val delimiter = " | "
                val parts = ls.split(delimiter)

                Observable.fromCallable {
                    with(MainActivity.ADB?.employeeDao()) { CurrentSelectedTask = this?.getTaskByCaption(parts[1], parts[2]) }
                }.subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread()).subscribe()

                val intent = Intent(this, TaskRecordEdit::class.java)
                intent.noAnimation()
                startActivity(intent)
            }
            catch(e:Throwable) {
                Toast.makeText(this, "Ошибка, даже не знаю почему :(", Toast.LENGTH_SHORT).show()
            }
        }

        backgroundThreadProcessing()
    }

    private fun backgroundThreadProcessing() {

        Observable.fromCallable {
            DatabaseHandler?.getDateEntity(day, month, year)
        }.doOnNext { list ->

            val Count = list?.size!!.toInt()
            val ListItems = arrayOfNulls<String>(Count)

            for (i in 0 until Count) {
                val recipe = list[i]
                ListItems[i] = " | " + recipe.importance + " | " + recipe.caption.toString()
            }

            TaskAdapter = ArrayAdapter(this, android.R.layout.simple_list_item_1, ListItems)
            this@SearchCalendars.runOnUiThread{ this.lv_CalendarTaskList?.adapter = TaskAdapter }
        }.subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread()).subscribe()
    }

    fun gotoHome(v : View){ startActivity(Intent(this, MainActivity::class.java).noAnimation()) }
    fun gotoSearch(v : View){ startActivity(Intent(this, SearchTask::class.java).noAnimation()) }
    fun gotoCalendar(v : View){  }
}
