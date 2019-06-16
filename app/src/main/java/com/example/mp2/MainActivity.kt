package com.example.mp2

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
import com.example.mp2.GoalsLogic.GoalRecords
import com.example.mp2.NotesLogic.NotesRecordCreate
import com.example.mp2.NotesLogic.NotesRecordEdit
import com.example.mp2.NotesLogic.NotesRecordEdit.Companion.CurrentSelectedNote
import com.example.mp2.TasksLogic.TaskRecordCreate
import com.example.mp2.TasksLogic.TaskRecordEdit
import com.example.mp2.TasksLogic.TaskRecords
import io.reactivex.Observable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import kotlinx.android.synthetic.main.support_activity_main.*
import org.jetbrains.anko.noAnimation
import java.util.*
import java.util.Calendar.getInstance as CurrentTime


class MainActivity : AppCompatActivity(), NavigationView.OnNavigationItemSelectedListener {

    private var arrayTasks = arrayOf("Определение содержания ВКР", "Введение",
        "Исследование предметной области", "Анализ и выбор инструментальных средств",
        "Техническое задание", "Пояснительная записка к техническому проекту", "Разработка программного изделия",
        "Программа и методика испытаний", "Заключение", "Список использованных источников")

    private var task_listViewAdapter: ArrayAdapter<String?>? = null
    private var notes_listViewAdapter: ArrayAdapter<String?>? = null

    private val CurrentDate = Calendar.getInstance()
    private val year = CurrentDate.get(Calendar.YEAR)
    private val month = CurrentDate.get(Calendar.MONTH)
    private val day = CurrentDate.get(Calendar.DAY_OF_MONTH)

    companion object {
        var Login : String = "Offline"
        var ADB: AppDatabase? = null
    }

    override fun onNavigationItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.nav_goal -> { startActivity(Intent(this, GoalRecords::class.java).noAnimation()) }
            R.id.nav_task -> { startActivity(Intent(this, TaskRecords::class.java).noAnimation()) }
            R.id.nav_stats -> { startActivity(Intent(this, PersonalAccount::class.java).noAnimation()) }
            R.id.nav_advice -> { startActivity(Intent(this, AdviceScreen::class.java).noAnimation()) }
            R.id.nav_settings -> { startActivity(Intent(this, OtherSettings::class.java).noAnimation()) }
        }
        val drawerLayout: DrawerLayout = findViewById(R.id.main_activity)
        drawerLayout.closeDrawer(GravityCompat.START)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem?): Boolean {
        when(item?.itemId){
            R.id.am_createNote -> { startActivity(Intent(this, NotesRecordCreate::class.java).noAnimation()) }
            R.id.am_createTask -> { startActivity(Intent(this, TaskRecordCreate::class.java).noAnimation()) }
        }
        return super.onOptionsItemSelected(item)
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.toolbar_nodes_main_activity, menu)
        return true
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        onCreateToolbar()
        onSetClickerListeners()
    }

    private fun onCreateToolbar() {
        val toolbar: Toolbar = findViewById(R.id.main_toolbar_main)
        setSupportActionBar(toolbar)

        val drawerLayout: DrawerLayout = findViewById(R.id.main_activity)
        val navView: NavigationView = findViewById(R.id.nav_view)
        val toggle = ActionBarDrawerToggle(
            this, drawerLayout, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close
        )
        drawerLayout.addDrawerListener(toggle)
        toggle.syncState()

        navView.setNavigationItemSelectedListener(this)
    }

    private fun onSyncDateBase() {
        Observable.fromCallable {
            if (ADB == null)
                ADB = AppDatabase.getAppDataBase(context = this)
        }.doOnNext {
            setListView()
        }.subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread()).subscribe()
    }

    private fun onSetClickerListeners() {
        onSyncDateBase()
        createTaskListMethod()
    }

    fun gotoHome(v :View) {   }
    fun gotoCalendar(v :View) { startActivity(Intent(this, SearchCalendars::class.java).noAnimation()) }
    fun gotoSearch(v :View) { startActivity(Intent(this, SearchTask::class.java).noAnimation()) }

    private fun setListView() {
        Observable.fromCallable {
            ADB?.employeeDao()?.changeEntityStatus(day, month, year)
            ADB?.employeeDao()?.getTodayEntity(Login, day, month, year, "В процессе")
        }.doOnNext { list ->
            val Count = list?.size!!.toInt() + 1
            val ListItems = arrayOfNulls<String>(Count)

            val LS = CurrentTime().time.toLocaleString()
            val delimiter = " "

            val Parts = LS.split(delimiter)
            val ConstListTag = " – Ваши задачи на сегодня:"

            ListItems[0] = Parts[0] + " " + Parts[1] + " " + Parts[2] + Parts[3] + ConstListTag

            for (i in 0 until list.size) {
                val recipe = list[i]
                ListItems[i + 1] = "${recipe.task_progress}%" + " | " + recipe.importance + " | " + recipe.caption.toString()
            }
            task_listViewAdapter = ArrayAdapter(this, android.R.layout.simple_list_item_1, ListItems)
            am_listView_tasks?.adapter = task_listViewAdapter
        }.doOnNext {
            val notesList = ADB?.notesDao()?.getNotes

            val Count = notesList?.size!!.toInt() + 1
            val ListItems = arrayOfNulls<String>(Count)

            ListItems[0] = "Заметки:"

            for (i in 0 until notesList.size) {
                val recipe = notesList[i]
                ListItems[i + 1] = recipe.notes_caption
            }
            notes_listViewAdapter = ArrayAdapter(this, android.R.layout.simple_list_item_1, ListItems)
            if (notesList.isNotEmpty())
                am_listView_notes?.adapter = notes_listViewAdapter
        }.subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread()).subscribe()
    }

    private fun createTaskListMethod() {
        am_listView_notes.setOnItemClickListener { parent, view, position, id ->
            try {
                val textView = view as TextView
                val ls = textView.text.toString()
                val intent = Intent(this, NotesRecordEdit::class.java).noAnimation()
                    Observable.fromCallable {
                        try {
                            with(ADB?.notesDao()) { CurrentSelectedNote = this?.getNoteByCaption(ls) }
                            if (CurrentSelectedNote != null)
                                startActivity(intent)
                        }
                        catch (e : Throwable) { }
                    }.subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread()).subscribe()
            }
            catch(e:Throwable) {
                Toast.makeText(this, "Ошибка, даже не знаю почему :(", Toast.LENGTH_SHORT).show()
            }
        }

        am_listView_tasks.setOnItemClickListener { parent, view, position, id ->
            try {
                val textView = view as TextView
                val ls = textView.text.toString()
                val delimiter = " | "
                val parts = ls.split(delimiter)
                val intent = Intent(this, TaskRecordEdit::class.java).noAnimation()

                    Observable.fromCallable {
                        try {
                            with(ADB?.employeeDao()) { TaskRecordEdit.CurrentSelectedTask = this?.getTaskByCaption(parts[1], parts[2]+"%") }
                            startActivity(intent)
                        }
                        catch (e : Throwable) { }
                    }.subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread()).subscribe()
            }
            catch(e:Throwable) {
                Toast.makeText(this, "Ошибка, даже не знаю почему :(", Toast.LENGTH_SHORT).show()
            }
        }
    }
}



