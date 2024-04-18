package com.example.partyapp

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.RecyclerView
import com.google.android.flexbox.FlexboxLayoutManager
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase

class OwnerEventExtraActivity : AppCompatActivity() {
    private lateinit var auth: FirebaseAuth
    override fun onCreate(savedInstanceState: Bundle?) {
        auth = Firebase.auth
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_owner_event_extra)
        val event = intent.getParcelableExtra<EventModel>("event")
        val eventName = event!!.name
        val host = event.host
        val address = event.address
        val start = event.start
        val end = event.end
        val desc = event.desc
        val lat = event.lat
        val long = event.long
        val imagePathsArray = intent.getStringArrayExtra("imgPaths")?.toList() ?: emptyList()
        val tags = intent.getStringArrayExtra("tags")?.toList() ?: emptyList()
        val sanTags = intent.getStringArrayExtra("sanTags")?.toList() ?: emptyList()
        val tagModelList = mutableListOf<TagModel>()
        Log.i("CHECK2","${event.tags?.size}")
        Log.i("CHECK2","${tags.size}")
        for(tag in tags){
            tagModelList.add(TagModel(text=tag))
            Log.i("Tags","$tag")
        }
        val geoHelper = GeoHelper(this)
        val eventNameTextView = findViewById<TextView>(R.id.owner_eventNameTextView)
        val startTimeTextView = findViewById<TextView>(R.id.owner_startTimeTextView)
        val endTimeTextView = findViewById<TextView>(R.id.owner_endTimeTextView)
        val addressTextView = findViewById<TextView>(R.id.owner_addressTextView)
        val descriptionTextView = findViewById<TextView>(R.id.owner_descriptionTextView)

        val tagsRV = findViewById<RecyclerView>(R.id.owner_EventTagsRV)
        val tagsAdapter = TagsAdapter(false, tagModelList)
        tagsRV.layoutManager = FlexboxLayoutManager(this)
        tagsRV.adapter = tagsAdapter

        val whosGoingButton = findViewById<Button>(R.id.owner_see_who_is_going)
        val checkInButton = findViewById<Button>(R.id.owner_check_in_users)
        val editEventButton = findViewById<Button>(R.id.owner_edit_event)
        val cancelEventButton = findViewById<Button>(R.id.owner_cancel_event)

        whosGoingButton.setOnClickListener {
            val intent = Intent(this,SeeAttendeesActivity::class.java).apply {
                putExtra("pushId",event.pushId)
                putExtra("owner", event.host == auth.currentUser!!.uid)
            }
            this.startActivity(intent)
        }

        eventNameTextView.text = eventName
        addressTextView.text = address
        startTimeTextView.text = start
        endTimeTextView.text = end
        descriptionTextView.text = desc



    }
}