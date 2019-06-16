package com.example.mp2

import android.content.Intent
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.view.GestureDetector
import android.view.GestureDetector.SimpleOnGestureListener
import android.view.MotionEvent
import android.view.View
import android.view.animation.Animation
import android.view.animation.AnimationUtils
import android.widget.ViewFlipper
import org.jetbrains.anko.noAnimation


class AdviceScreen : AppCompatActivity() {

    var flipper: ViewFlipper? = null

    var animFlipInForward: Animation? = null
    var animFlipOutForward: Animation? = null
    var animFlipInBackward: Animation? = null
    var animFlipOutBackward: Animation? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_advice_screen)

        flipper = findViewById(R.id.viewFlipper1);

        animFlipInForward = AnimationUtils.loadAnimation(this, R.anim.advice_flipin);
        animFlipOutForward = AnimationUtils.loadAnimation(this, R.anim.advice_flipout);
        animFlipInBackward = AnimationUtils.loadAnimation(this, R.anim.advice_flipin_reverse);
        animFlipOutBackward = AnimationUtils.loadAnimation(this, R.anim.advice_flipout_reverse);
    }

    private fun SwipeLeft() {
        flipper!!.setInAnimation(animFlipInBackward)
        flipper!!.setOutAnimation(animFlipOutBackward)
        flipper!!.showPrevious()
    }

    private fun SwipeRight() {
        flipper!!.setInAnimation(animFlipInForward)
        flipper!!.setOutAnimation(animFlipOutForward)
        flipper!!.showNext()
    }

    override fun onTouchEvent(event: MotionEvent): Boolean {
        return gestureDetector.onTouchEvent(event)
    }

    var simpleOnGestureListener: SimpleOnGestureListener = object : SimpleOnGestureListener() {

        override fun onFling(
            e1: MotionEvent, e2: MotionEvent, velocityX: Float,
            velocityY: Float
        ): Boolean {

            val sensitvity = 50f
            if (e1.x - e2.x > sensitvity) {
                SwipeLeft()
            } else if (e2.x - e1.x > sensitvity) {
                SwipeRight()
            }
            return true
        }
    }

    var gestureDetector = GestureDetector(
        baseContext,
        simpleOnGestureListener
    )

    fun flipByClick(v: View) {
        flipper!!.showNext()
    }

    fun gotoHome(v : View){ startActivity(Intent(this, MainActivity::class.java).noAnimation()) }
    fun gotoCalendar(v : View){ startActivity(Intent(this, SearchCalendars::class.java).noAnimation()) }
    fun gotoSearch(v : View){ startActivity(Intent(this, SearchTask::class.java).noAnimation()) }
}
