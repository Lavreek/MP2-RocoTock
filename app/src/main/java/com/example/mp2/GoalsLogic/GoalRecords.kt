package com.example.mp2.GoalsLogic

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
import com.example.mp2.*
import com.example.mp2.TasksLogic.TaskRecords
import io.reactivex.Observable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import kotlinx.android.synthetic.main.support_activity_goal.*
import org.jetbrains.anko.noAnimation

class GoalRecords : AppCompatActivity(), NavigationView.OnNavigationItemSelectedListener {

    private var goal_listViewAdapter: ArrayAdapter<String?>? = null
    private var goal_IntArray: Array<Int>? = null

    override fun onNavigationItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.nav_goal -> { startActivity(Intent(this, GoalRecords::class.java).noAnimation()) }
            R.id.nav_task -> { startActivity(Intent(this, TaskRecords::class.java).noAnimation()) }
            R.id.nav_stats -> { startActivity(Intent(this, PersonalAccount::class.java).noAnimation()) }
            R.id.nav_settings -> { startActivity(Intent(this, OtherSettings::class.java).noAnimation()) }
        }
        val drawerLayout: DrawerLayout = findViewById(R.id.goal_activity)
        drawerLayout.closeDrawer(GravityCompat.START)
        return true
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_goal_records)

        gr_fab.setOnClickListener { startActivity(Intent(this, GoalRecordCreate::class.java).noAnimation()) }

        val toolbar : Toolbar = findViewById(R.id.goal_toolbar_goal)
        setSupportActionBar(toolbar)
        val drawerLayout: DrawerLayout = findViewById(R.id.goal_activity)
        val navView: NavigationView = findViewById(R.id.nav_view)
        val toggle = ActionBarDrawerToggle(
            this, drawerLayout, toolbar,
            R.string.navigation_drawer_open,
            R.string.navigation_drawer_close
        )
        drawerLayout.addDrawerListener(toggle)
        toggle.syncState()
        navView.setNavigationItemSelectedListener(this)

        currentTaskList()
        createTaskListMethod()
    }


    private fun currentTaskList() {
        Observable.fromCallable {
//            var count = MainActivity.ADB?.goalDao()?.Upsss222
            MainActivity.ADB?.goalDao()?.getGoals
        }.doOnNext {list ->

            var asd = MainActivity.ADB?.employeeDao()?.getTasks
            var list2 = MainActivity.ADB?.goalDao()?.getGoals
                val Count = list?.size!!.toInt()
                val ListItems = arrayOfNulls<String>(Count)
//                goal_IntArray = arrayOf(list.size)
                val help = HideAway()
                for (i in 0 until list.size) {
                    val recipe = list[i]
//                    goal_IntArray!![i] = recipe.id.toInt()
                    val Check = MainActivity.ADB?.goalDao()?.getCountTaskForGoal(recipe.id)
                    if (Check!! > 0) {
                        MainActivity.ADB?.goalDao()?.setProgress(recipe.id)
                    }
                    val status = help.returnStatus(recipe.goal_status)
                    ListItems[i] = status + " ( " + recipe.goal_progress + "% ) | " + recipe.goal_caption
                }
                goal_listViewAdapter = ArrayAdapter(this, android.R.layout.simple_list_item_1, ListItems)
        }.doOnNext {
            this@GoalRecords.runOnUiThread { gr_listView.adapter = goal_listViewAdapter }
        }.subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread()).subscribe()
    }

    private fun createTaskListMethod() {
        gr_listView.setOnItemClickListener { parent, view, position, id ->

            val textView = view as TextView
            val ls = textView.text.toString()
            val delimiter = " | "
            val parts = ls.split(delimiter)
            Observable.fromCallable {
                try {
                    with(MainActivity.ADB?.goalDao()) {
                        GoalRecordEdit.CurrentSelectedGoal = this?.getGoalByCaption(parts[1])
                    }
                    startActivity(Intent(this, GoalRecordEdit::class.java).noAnimation())
                } catch (e: Throwable) {
                }
            }.subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread()).subscribe()
        }
    }

    fun gotoHome(view : View){ startActivity(Intent(this, MainActivity::class.java).noAnimation()) }
    fun gotoCalendar(view : View){ startActivity(Intent(this, SearchCalendars::class.java).noAnimation()) }
    fun gotoPersonalAccount(view : View){ startActivity(Intent(this, PersonalAccount::class.java).noAnimation()) }
    fun gotoSearch(view : View){ startActivity(Intent(this, SearchTask::class.java).noAnimation()) }

}
