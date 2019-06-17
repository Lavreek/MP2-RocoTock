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
import android.widget.Toast
import com.example.mp2.Entityes.EntityGoal
import com.example.mp2.GoalsLogic.GoalRecords
import com.example.mp2.TasksLogic.TaskRecords
import io.reactivex.Observable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import kotlinx.android.synthetic.main.activity_personal_account.*
import kotlinx.android.synthetic.main.support_activity_personal_account.*
import org.jetbrains.anko.noAnimation
import java.util.*

class PersonalAccount : AppCompatActivity(), NavigationView.OnNavigationItemSelectedListener {

    private var DatabaseHandler = MainActivity.ADB?.employeeDao()
    private val CurrentDate = Calendar.getInstance()
    private val year = CurrentDate.get(Calendar.YEAR)
    private val month = CurrentDate.get(Calendar.MONTH)
    private val day = CurrentDate.get(Calendar.DAY_OF_MONTH)

    override fun onNavigationItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.nav_goal -> { startActivity(Intent(this, GoalRecords::class.java).noAnimation()) }
            R.id.nav_task -> { startActivity(Intent(this, TaskRecords::class.java).noAnimation()) }
            R.id.nav_stats -> { startActivity(Intent(this,PersonalAccount::class.java).noAnimation()) }
            R.id.nav_advice -> { startActivity(Intent(this, AdviceScreen::class.java).noAnimation()) }
            R.id.nav_settings -> { startActivity(Intent(this, OtherSettings::class.java).noAnimation()) }
        }
        val drawerLayout: DrawerLayout = findViewById(R.id.activity_pc)
        drawerLayout.closeDrawer(GravityCompat.START)
        return true
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_personal_account)

        nav_view.visibility = View.VISIBLE

        setToolbar()
        showTaskList()
    }

    private fun setToolbar() {
        val toolbar: Toolbar = findViewById(R.id.pc_toolbar_personal_account)
        setSupportActionBar(toolbar)

        val drawerLayout: DrawerLayout = findViewById(R.id.activity_pc)
        val navView: NavigationView = findViewById(R.id.nav_view)
        val toggle = ActionBarDrawerToggle(
            this, drawerLayout, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close
        )
        drawerLayout.addDrawerListener(toggle)
        toggle.syncState()

        navView.setNavigationItemSelectedListener(this)
    }

    private fun showTaskList() {
        Observable.fromCallable { }.doOnNext {
            val CurrentTask = DatabaseHandler?.getCurrentCount!!.toInt()
            val CompletedTask = DatabaseHandler?.getCompletedTasks!!.toInt()
            val FallenTask = DatabaseHandler?.getLostTasks!!.toInt()
            val ProgressCount = DatabaseHandler?.getInProgressTasks!!.toInt()
            val asd = DatabaseHandler?.getCompletedForThreeDays(day)
            val asd2 = DatabaseHandler?.getCompletedForWeek(day)
            val asd3 = DatabaseHandler?.getCompletedForMonth(month)
            val asd4 = DatabaseHandler?.getAVGGoals
            val asd5 = DatabaseHandler?.getBestGoal
            val asd6 = DatabaseHandler?.getCountGoal!!
            val asd7 = DatabaseHandler?.getCountTask!!
            val asd8 = DatabaseHandler?.getPriorityTasksCount!!
            val asd9 = DatabaseHandler?.getCompletedHighTasksCount!!
            val asd10 = DatabaseHandler?.getCompletedMediumTasksCount!!
            val asd11 = DatabaseHandler?.getCompletedLowTasksCount!!
            val asd12 = DatabaseHandler?.getCompletedTaskByMonth(month - 1)!!
            val asd13 = DatabaseHandler?.getCompletedTaskByMonth(month)!!
            val asd14 = DatabaseHandler?.getCompletedGoals!!

            val Ef : Long
            if (asd12 < 1 || asd13 < 1) {
                Ef = 0
            } else {
                Ef = asd13/ asd12
            }

            this@PersonalAccount.runOnUiThread{ stats(CurrentTask, CompletedTask, FallenTask, ProgressCount,
                p2=asd!!,
                p3 = asd2!!,
                p4 = asd3!!,
                p5= asd4!!,
                p6 = setMax(asd5),
                p7 = asd6,
                p8 = asd7,
                p9 = asd8,
                p10 = asd9,
                p11 = asd10,
                p12 = asd11,
                p13 = Ef,
                p14 = asd14) }
        }.subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread()).subscribe()
    }

    private fun setMax(asd :List<EntityGoal>?): String {
        var listItemProgress = 0
        var listItemCaption = ""
        val list = asd?.toList()
        list?.forEach {
            if (it.goal_progress >= listItemProgress) {
                listItemProgress = it.goal_progress
                listItemCaption = it.goal_caption as String
            }
        }
        return "$listItemProgress% $listItemCaption"
    }

    fun stats(CurrentTask :Int, CompletedTask :Int, FallenTask :Int, p1 :Int,
              p2 :Int, p3 :Int, p4 :Int, p5 :Int, p6 :String, p7 :Int, p8 :Long, p9 :Long,
              p10 :Long, p11 :Long,p12 :Long, p13 :Long,p14 :Long){
        try {
            pc_textView_1.setText("Количество заданий: $p8")
            pc_textView_2.setText("выполненых заданий: $CompletedTask")
            pc_textVew_3.setText("проваленных заданий: $FallenTask")
            pc_textView_4.setText("заданий в процессе: $CurrentTask")
            pc_textView_5.setText("Выполнено заданий c приоритетом: $p9")
            pc_textView_6.setText("с высоким приоритетом: $p10")
            pc_textView_7.setText("с средним приоритетом: $p11")
            pc_textView_8.setText("с низким приоритетом: $p12")
            pc_textView_9.setText("Количество заданий с прогрессом более 50%: $p1")
            pc_textView_10.setText("За 3 дня: ${p2}")
            pc_textView_11.setText("За неделю: ${p3}")
            pc_textView_12.setText("В этом месяце: ${p4}")
            pc_textView_13.setText("Самая успешная цель: ${p6}")
            pc_textView_14.setText("Среднее выполнение целей: ${p5}% из ${p7} целей")
            pc_textView_15.setText("Целей завершено: ${p14}%")
            pc_textView_16.setText("Ваша эффективность лучше чем в прошлом месяце на: ${p13}%")
        } catch (e : Throwable) { Toast.makeText(this,e.message,Toast.LENGTH_SHORT).show()}
    }

    fun gotoHome(v :View){ startActivity(Intent(this, MainActivity::class.java).noAnimation()) }
    fun gotoSearch(v :View){ startActivity(Intent(this, SearchTask::class.java).noAnimation()) }
    fun gotoCalendar(v :View){ startActivity(Intent(this, SearchCalendars::class.java).noAnimation()) }
}
