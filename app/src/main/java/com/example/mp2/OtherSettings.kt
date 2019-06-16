package com.example.mp2

import android.content.Intent
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.view.View
import io.reactivex.Observable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import kotlinx.android.synthetic.main.activity_other_settings.*

class OtherSettings : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_other_settings)

        os_delete_all_tasks.setOnClickListener{
            Observable.fromCallable {
                MainActivity.ADB?.goalDao()?.deleteTable()
                MainActivity.ADB?.employeeDao()?.deleteTable()
                MainActivity.ADB?.notesDao()?.deleteTable()
            }.doOnNext({    }).subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread()).subscribe()
        }
    }

    fun gotoSearch(v :View) { startActivity(Intent(this, SearchTask::class.java)) }
    fun gotoHome(v :View) { startActivity(Intent(this, MainActivity::class.java)) }
    fun gotoCalendar(v :View) { startActivity(Intent(this, SearchCalendars::class.java)) }
}
