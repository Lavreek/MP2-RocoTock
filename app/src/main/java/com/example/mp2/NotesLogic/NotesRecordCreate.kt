package com.example.mp2.NotesLogic

import android.content.Intent
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.Toolbar
import android.view.View
import android.widget.Toast
import com.example.mp2.Entityes.EntityNotes
import com.example.mp2.MainActivity
import com.example.mp2.R
import com.example.mp2.SearchCalendars
import com.example.mp2.SearchTask
import io.reactivex.Observable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import kotlinx.android.synthetic.main.activity_notes_record_create.*
import org.jetbrains.anko.noAnimation

class NotesRecordCreate : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_notes_record_create)

        setToolbar()
        setLogic()
    }

    private fun setInsertMethod() {
        try {
            val notes = EntityNotes(
                notes_caption = nrc_editText_caption.text.toString()
            )
            if (nrc_editText_caption.text.toString() != "") {
                Observable.fromCallable {
                    with(MainActivity.ADB?.notesDao()) { this?.insert(notes) }
                }.doOnNext {
                    val intent = Intent(this, MainActivity::class.java)
                    intent.noAnimation()
                    startActivity(intent)
                }.subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread()).subscribe()
            }
            if (nrc_editText_caption.text.isEmpty()) {
                Toast.makeText(this, "Описание не может быть пустым", Toast.LENGTH_SHORT).show()
            }
        } catch (e: Throwable) {
            Toast.makeText(this, e.message, Toast.LENGTH_SHORT).show()
        }
    }

    private fun setToolbar() {
        val toolbar: Toolbar = findViewById(R.id.nrc_toolbar_default)
        toolbar.title = "Создание заметки"
        setSupportActionBar(toolbar)
    }

    private fun setLogic() {
        nrc_fab_add.setOnClickListener {
            setInsertMethod()
        }
    }

    fun gotoHome(v :View){ startActivity(Intent(this, MainActivity::class.java).noAnimation()) }
    fun gotoCalendar(v :View){ startActivity(Intent(this, SearchCalendars::class.java).noAnimation()) }
    fun gotoSearch(v :View){ startActivity(Intent(this, SearchTask::class.java).noAnimation()) }
}
