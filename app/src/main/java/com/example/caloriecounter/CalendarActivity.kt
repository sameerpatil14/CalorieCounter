package com.example.caloriecounter

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.CalendarView
import java.text.SimpleDateFormat
import java.util.*

class CalendarActivity : AppCompatActivity() {

    val TAG = "CalendarActivity"

    var date = ""
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_calendar)

        val calendarView = findViewById<CalendarView>(R.id.calendarView)
        val selectDate = findViewById<Button>(R.id.selectDate)


        calendarView.setOnDateChangeListener { _, year, month, dayOfMonth ->
            date = ("${dayOfMonth}-${month + 1}-${year}")
            Log.d(TAG, "calendar setOnDateChangeListener  dd-mm-yyyy $date")
        }

        selectDate.setOnClickListener {
            if ((date == null) or (date == "")) {
                val simpleDateFormat = SimpleDateFormat("dd-M-yyyy")
                date = simpleDateFormat.format(Date())
                Log.d(TAG, "Inside onClickListener $date")
            }
            val intent = Intent(this, DashboardActivity::class.java)
            intent.putExtra("date", date)
            startActivity(intent)
            finish()
        }
    }
}
