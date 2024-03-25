package com.example.partyapp

import android.os.Bundle
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity

class LocalsExtra : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.locals_extra)
        val eventName = intent.getStringExtra("eventName")
        val host = intent.getStringExtra("host")
        val address = intent.getStringExtra("address")
        val time = intent.getStringExtra("time")
        val distance = intent.getStringExtra("distance")
        val dayOfWeek = intent.getStringExtra("dayOfWeek")
        val dayOfMonth = intent.getStringExtra("dayOfMonth")
        val description = intent.getStringExtra("description")
        val imagePathsArray = intent.getStringArrayExtra("imagePaths")?.toList() ?: emptyList()
        val eventNameTextView = findViewById<TextView>(R.id.eventNameTextView)
        val hostNameTextView = findViewById<TextView>(R.id.hostNameTextView)
        val addressTextView = findViewById<TextView>(R.id.addressTextView)
        val timeTextView = findViewById<TextView>(R.id.timeTextView)
        val distanceTextView = findViewById<TextView>(R.id.distanceTextView)
        val dayOfWeekTextView = findViewById<TextView>(R.id.dayOfWeekTextView)
        val dayOfMonthTextView = findViewById<TextView>(R.id.dayOfMonthTextView)
        val descriptionTextView = findViewById<TextView>(R.id.descriptionTextView)
        eventNameTextView.text = eventName
        hostNameTextView.text = host
        addressTextView.text = address
        timeTextView.text = time
        distanceTextView.text = distance
        dayOfWeekTextView.text = dayOfWeek
        dayOfMonthTextView.text = dayOfMonth
        descriptionTextView.text = description

        Toast.makeText(this,imagePathsArray[0].toString(),Toast.LENGTH_LONG).show()

        val concatenatedPaths = imagePathsArray.take(6).joinToString("\n")

        // Find TextView for image paths
        val imagePathsTextView = findViewById<TextView>(R.id.imagePathsTextView)

        // Set concatenated paths to the TextView
        imagePathsTextView.text = concatenatedPaths

        // Show a toast if there are more than 6 image paths
        if (imagePathsArray.size > 6) {
            Toast.makeText(this, "Only the first 6 image paths are displayed", Toast.LENGTH_SHORT).show()
        }

//        Toast.makeText(this,eventName,Toast.LENGTH_LONG).show()

    }
}
