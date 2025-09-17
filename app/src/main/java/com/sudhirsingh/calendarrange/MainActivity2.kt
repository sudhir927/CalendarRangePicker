package com.sudhirsingh.calendarrange

import android.os.Build
import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.WindowCompat
import com.sudhirsingh.rangecalendar.RangeCalendarView
import java.time.LocalDate
import java.util.Calendar


class MainActivity2 : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        // Apply edge-to-edge display
        enableEdgeToEdge()

        // Find the calendar view
        val calendarView = findViewById<RangeCalendarView>(R.id.calendarView)

        // Set disabled dates (example)
        val disabledDates = mutableSetOf<Calendar>()
        val calendar = Calendar.getInstance()
        calendarView.setDisabledDates(disabledDates)
    }

    private fun enableEdgeToEdge() {
        WindowCompat.setDecorFitsSystemWindows(window, false)
    }
}