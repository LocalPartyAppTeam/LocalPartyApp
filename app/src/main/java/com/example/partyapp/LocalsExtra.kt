package com.example.partyapp

import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.RecyclerView
import com.google.android.flexbox.FlexboxLayoutManager

class LocalsExtra : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.locals_extra)
        val eventName = intent.getStringExtra("name")
        val host = intent.getStringExtra("host")
        val address = intent.getStringExtra("address")
        val start = intent.getStringExtra("start")
        val end = intent.getStringExtra("end")
        val desc = intent.getStringExtra("desc")
        val imagePathsArray = intent.getStringArrayExtra("imgPaths")?.toList() ?: emptyList()
        val tags = intent.getStringArrayExtra("tags")?.toList() ?: emptyList()
        val sanTags = intent.getStringArrayExtra("sanitizedTags")?.toList() ?: emptyList()
        val tagModelList = mutableListOf<TagModel>()
        for(tag in tags){
            tagModelList.add(TagModel(text=tag))
            Log.i("Tags","$tag")
        }

        val eventNameTextView = findViewById<TextView>(R.id.eventNameTextView)
//        val hostNameTextView = findViewById<TextView>(R.id.hostNameTextView)
        val startTimeTextView = findViewById<TextView>(R.id.startTimeTextView)
        val endTimeTextView = findViewById<TextView>(R.id.endTimeTextView)
        val addressTextView = findViewById<TextView>(R.id.addressTextView)
//        val distanceTextView = findViewById<TextView>(R.id.distanceTextView)
//        val dayOfWeekTextView = findViewById<TextView>(R.id.dayOfWeekTextView)
//        val dayOfMonthTextView = findViewById<TextView>(R.id.dayOfMonthTextView)
        val descriptionTextView = findViewById<TextView>(R.id.descriptionTextView)
        eventNameTextView.text = eventName
//        hostNameTextView.text = host
        addressTextView.text = address
        startTimeTextView.text = start
        endTimeTextView.text = end
//        distanceTextView.text = distance
//        dayOfWeekTextView.text = dayOfWeek
//        dayOfMonthTextView.text = dayOfMonth
        descriptionTextView.text = desc

        val tagsRV = findViewById<RecyclerView>(R.id.localEventTags)
        val tagsAdapter = TagsAdapter(false, tagModelList)
        tagsRV.layoutManager = FlexboxLayoutManager(this)
        tagsRV.adapter = tagsAdapter

//        Toast.makeText(this,imagePathsArray[0].toString(),Toast.LENGTH_LONG).show()

        val concatenatedPaths = imagePathsArray.take(6).joinToString("\n")
//        val imagePathsTextView = findViewById<TextView>(R.id.imagePathsTextView)
//        imagePathsTextView.text = concatenatedPaths
//        if (imagePathsArray.size > 6) {
//            Toast.makeText(this, "Only the first 6 image paths are displayed", Toast.LENGTH_SHORT).show()
//        }

//        Toast.makeText(this,eventName,Toast.LENGTH_LONG).show()

    }
}
