package com.example.mp2.GoalsLogic

import android.content.Intent
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.view.View
import android.widget.Toast
import com.example.mp2.Entityes.EntityGoal
import com.example.mp2.MainActivity
import com.example.mp2.R
import com.example.mp2.SearchCalendars
import com.example.mp2.SearchTask
import io.reactivex.Observable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import kotlinx.android.synthetic.main.activity_goal_record_create.*
import org.jetbrains.anko.noAnimation

class GoalRecordCreate : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_goal_record_create)

        onSetClickerListeners()
    }

    private fun onSetClickerListeners() {
        grc_fab_add.setOnClickListener { runInsertMethod() }
    }

    private fun error() {
        Toast.makeText(this, "Произошла ошибка", Toast.LENGTH_SHORT).show()
    }

    private fun runInsertMethod() {
        try {
            val goal = EntityGoal(goal_caption = grc_editText_caption.text.toString())
            if (grc_editText_caption.text.toString() != "") {
                val intent = Intent(this, GoalRecords::class.java).noAnimation()
                Observable.fromCallable {
                    try {
                        with(MainActivity.ADB?.goalDao()) {this?.insert(goal) }
                        startActivity(intent)
                    }
                    catch (e : Throwable) { this@GoalRecordCreate.runOnUiThread{error()} }
                }.subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread()).subscribe()
            }
            if (grc_editText_caption.text.toString() == "") {
                Toast.makeText(this, "Описание не может быть пустым", Toast.LENGTH_SHORT).show()
            }
        } catch (e: Throwable) {
            Toast.makeText(this, "Error", Toast.LENGTH_SHORT).show()
        }
    }

    fun gotoHome(v :View){ startActivity(Intent(this, MainActivity::class.java).noAnimation()) }
    fun gotoCalendar(v :View){ startActivity(Intent(this, SearchCalendars::class.java).noAnimation()) }
    fun gotoSearch(v :View){ startActivity(Intent(this, SearchTask::class.java).noAnimation()) }
}
