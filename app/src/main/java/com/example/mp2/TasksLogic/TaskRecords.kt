package com.example.mp2.TasksLogic

import android.content.Intent
import android.os.Bundle
import android.support.design.widget.NavigationView
import android.support.v4.view.GravityCompat
import android.support.v4.widget.DrawerLayout
import android.support.v7.app.ActionBarDrawerToggle
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.Toolbar
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.ArrayAdapter
import android.widget.TextView
import android.widget.Toast
import com.example.mp2.*
import com.example.mp2.GoalsLogic.GoalRecords
import com.example.mp2.TasksLogic.TaskRecordEdit.Companion.CurrentSelectedTask
import io.reactivex.Observable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import kotlinx.android.synthetic.main.support_activity_task.*
import org.jetbrains.anko.noAnimation
import java.util.*

class TaskRecords : AppCompatActivity(), NavigationView.OnNavigationItemSelectedListener {

    override fun onNavigationItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.nav_goal -> { startActivity(Intent(this, GoalRecords::class.java).noAnimation()) }
            R.id.nav_task -> { startActivity(Intent(this, TaskRecords::class.java).noAnimation()) }
            R.id.nav_stats -> { startActivity(Intent(this, PersonalAccount::class.java).noAnimation()) }
            R.id.nav_settings -> { startActivity(Intent(this, OtherSettings::class.java).noAnimation()) }
        }
        val drawerLayout: DrawerLayout = findViewById(R.id.task_activity)
        drawerLayout.closeDrawer(GravityCompat.START)
        return true
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.toolbar_nodes, menu)
        return true
    }

    private val CurrentDate = Calendar.getInstance()
    private val year = CurrentDate.get(Calendar.YEAR)
    private val month = CurrentDate.get(Calendar.MONTH)
    private val day = CurrentDate.get(Calendar.DAY_OF_MONTH)


    private var listViewAdapter: ArrayAdapter<String?>? = null
    private var SItem : Int = -1
    private var SChange : Boolean = false

    private var TaskString = arrayOf("Выполнено", "Провалено", "В процессе")
    private var ConstListTag = arrayOf("Выполненые:", "Проваленные:", "Все запланированные задачи:")

    override fun onOptionsItemSelected(item: MenuItem?): Boolean {
        when(item?.itemId){
            R.id.CurrentNode -> {
                SItem = -1
                SChange = true
                openIntentWithExtra(SItem)
            }
            R.id.CompletedNode -> {
                SItem = 0
                SChange = true
                openIntentWithExtra(SItem)
            }
            R.id.LostNode -> {
                SItem = 1
                SChange = true
                openIntentWithExtra(SItem)
            }
            R.id.AllNode -> {
                SItem = 2
                SChange = true
                openIntentWithExtra(SItem)
            }
        }
        return super.onOptionsItemSelected(item)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_task_records)
        setToolbar()

        SItem = intent.getIntExtra("PESItem",-1)
        createTaskListMethod()

        setClickListeners()
    }

    private fun setToolbar() {
        val toolbar : Toolbar = findViewById(R.id.task_toolbar_task)
        setSupportActionBar(toolbar)

        val drawerLayout: DrawerLayout = findViewById(R.id.task_activity)
        val navView: NavigationView = findViewById(R.id.nav_view)
        val toggle = ActionBarDrawerToggle(
            this, drawerLayout, toolbar,
            R.string.navigation_drawer_open,
            R.string.navigation_drawer_close
        )
        drawerLayout.addDrawerListener(toggle)
        toggle.syncState()
        navView.setNavigationItemSelectedListener(this)
    }

    private fun openIntentWithExtra(ExtraItem: Int) {
        intent = Intent(this, TaskRecords::class.java)
        intent.putExtra("PESItem", ExtraItem)
        intent.noAnimation()
        startActivity(intent)
    }

    private fun setClickListeners() {
        if (SItem == -1) {
            mainListView()
        } else {
            customListView(SItem)
        }

        tr_fab.setOnClickListener{
            startActivity(Intent(this, TaskRecordCreate::class.java).noAnimation())
        }
    }

    private fun mainListView() {
        Observable.fromCallable {
            MainActivity.ADB?.employeeDao()?.getTodayEntity(MainActivity.Login, day, month, year, "В процессе")
        }.doOnNext{ list ->
            val Count = list?.size!!.toInt() + 1
            val ListItems = arrayOfNulls<String>(Count)

            val LS = Calendar.getInstance().time.toLocaleString()
            val delimiter = " "

            val Parts = LS.split(delimiter)
            val ConstListTag = " – Ваши задачи на сегодня:"

            ListItems[0] = Parts[0] + " " + Parts[1] + " " + Parts[2] + Parts[3] + ConstListTag

            for (i in 0 until list.size) {
                val recipe = list[i]
                ListItems[i + 1] = "${recipe.efficiency}% | ${recipe.importance} | ${recipe.caption.toString()}"
            }

            listViewAdapter = ArrayAdapter(this, android.R.layout.simple_list_item_1, ListItems)
            tr_listView.adapter = listViewAdapter
            list?.map { }
        }.subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread()).subscribe()
    }

    private fun customListView(TaskCount : Int) {
        Observable.fromCallable {
            MainActivity.ADB?.employeeDao()?.getStatusEntity(TaskString[TaskCount])
        }.doOnNext({ list ->
            val Count = list?.size!!.toInt() + 1
            val ListItems = arrayOfNulls<String>(Count)

            ListItems[0] = ConstListTag[TaskCount]

            for (i in 0 until list.size) {
                val recipe = list[i]
                ListItems[i + 1] = "${recipe.efficiency}% | ${recipe.importance} | ${recipe.caption.toString()}"
            }

            listViewAdapter = ArrayAdapter(this, android.R.layout.simple_list_item_1, ListItems)
            tr_listView.adapter = listViewAdapter
            list?.map { }
        }).subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread()).subscribe()
    }

    private fun createTaskListMethod() {
        tr_listView.setOnItemClickListener { parent, view, position, id ->
            try {
                val textView = view as TextView
                val ls = textView.text.toString()
                val delimiter = " | "
                val parts = ls.split(delimiter)
                if (parts.size > 1) {
                    Observable.fromCallable {
                        with(MainActivity.ADB?.employeeDao()) { CurrentSelectedTask = this?.getTaskByCaption(parts[1], "${parts[2]}%") }
                    }.subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread()).subscribe()
                    startActivity(Intent(this, TaskRecordEdit::class.java).noAnimation())
                }
            }
            catch(e:Throwable) {
                Toast.makeText(this, "Ошибка, даже не знаю почему :(", Toast.LENGTH_SHORT).show()
            }
        }
    }

    fun gotoHome(v :View){ startActivity(Intent(this, MainActivity::class.java).noAnimation()) }
    fun gotoCalendar(v :View){ startActivity(Intent(this, SearchCalendars::class.java).noAnimation()) }
    fun gotoSearch(v :View){ startActivity(Intent(this, SearchTask::class.java).noAnimation()) }
}
