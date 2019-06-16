package com.example.mp2

import android.support.test.InstrumentationRegistry
import android.support.test.runner.AndroidJUnit4

import org.junit.Test
import org.junit.runner.RunWith

import org.junit.Assert.*

/**
 * Instrumented advice_view_first_support, which will execute on an Android device.
 *
 * See [testing documentation](http://d.android.com/tools/testing).
 */
@RunWith(AndroidJUnit4::class)
class ExampleInstrumentedTest {
    @Test
    fun useAppContext() {
        // Context of the app under advice_view_first_support.
        val appContext = InstrumentationRegistry.getTargetContext()
        assertEquals("com.example.mp2", appContext.packageName)
    }
}
