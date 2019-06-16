package com.example.mp2.NotesLogic

import android.content.Intent
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.Toolbar
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.Toast
import com.example.mp2.*
import com.example.mp2.Entityes.EntityNotes
import io.reactivex.Observable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import kotlinx.android.synthetic.main.activity_notes_record_edit.*
import org.jetbrains.anko.noAnimation

class NotesRecordEdit : AppCompatActivity() {

    private var DatabaseHandler = MainActivity.ADB?.notesDao()

    companion object {
        var CurrentSelectedNote : EntityNotes? = null
    }

    override fun onOptionsItemSelected(item: MenuItem?): Boolean {
        when(item?.itemId){
            R.id.item_delete -> {
                val help = HideAway()
                help.deleteNoteRecord()
                startActivity(Intent(this, MainActivity::class.java).noAnimation())
            }
        }
        return super.onOptionsItemSelected(item)
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.toolbar_nodes_delete, menu)
        return true
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_notes_record_edit)

        setToolbar()
        setLogic()
    }

    private fun setToolbar () {
        val toolbar: Toolbar = findViewById(R.id.nre_toolbar_default)
        toolbar.title = "Изменить заметку"
        setSupportActionBar(toolbar)
    }

    private fun setLogic() {
        nre_editText_caption.setText(CurrentSelectedNote?.notes_caption)
        nre_editText_caption.isEnabled = false

        nre_fab_editStart.setOnClickListener {
            nre_editText_caption.isEnabled = true
            nre_fab_editStart.hide()
        }

        nre_fab_editEnd.setOnClickListener {
            if (nre_editText_caption.text.toString() != "" ) {
                Observable.fromCallable {
                    val UNote = CurrentSelectedNote as EntityNotes
                    UNote.notes_caption = nre_editText_caption.text.toString()
                    with(DatabaseHandler) { this?.update(UNote) }
                    startActivity(Intent(this, MainActivity::class.java).noAnimation())
                }.subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread()).subscribe()
            } else {
                Toast.makeText(this, "Описание задачи не может быть пустым", Toast.LENGTH_SHORT).show()
            }
        }
    }

    fun gotoHome(v :View){ startActivity(Intent(this, MainActivity::class.java).noAnimation()) }
    fun gotoCalendar(v :View){ startActivity(Intent(this, SearchCalendars::class.java).noAnimation()) }
    fun gotoSearch(v :View){ startActivity(Intent(this, SearchTask::class.java).noAnimation()) }

}