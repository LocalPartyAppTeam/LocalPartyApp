package com.example.partyapp

import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.google.android.flexbox.FlexboxLayoutManager
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.FirebaseStorage
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

@RequiresApi(Build.VERSION_CODES.O)
class OwnerEventExtraActivity : AppCompatActivity() {
    private lateinit var auth: FirebaseAuth
    override fun onCreate(savedInstanceState: Bundle?) {
        val taggedEvents: DatabaseReference = FirebaseDatabase.getInstance().reference.child("TaggedEvents")
        auth = Firebase.auth
        val currUser = auth.currentUser!!.uid
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_owner_event_extra)
        val event = intent.getParcelableExtra<EventModel>("event")
        val eventName = event!!.name
        val host = event.host
        val address = event.address
        val start = LocalDateTime.parse(event.start!!).format(DateTimeFormatter.ofPattern("dd MMMM yyyy hh:mma"))
        val end = LocalDateTime.parse(event.end!!).format(DateTimeFormatter.ofPattern("dd MMMM yyyy hh:mma"))
        val desc = event.desc
        val lat = event.lat
        val long = event.long
        val imagePathsArray = event.imgPaths ?: emptyList<String>()
        val tags = event.tags ?: emptyList<String>()
        val sanTags = event.sanitizedTags ?: emptyList<String>()
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
        checkInButton.setOnClickListener {
            val intent = Intent(this,CheckInActivity::class.java).apply {
                putExtra("pushId",event.pushId)
            }
            this.startActivity(intent)
        }

        cancelEventButton.setOnClickListener {

            taggedEvents.addListenerForSingleValueEvent(object:
                ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    for(childSnapshot in snapshot.children){
                        val userID: String = childSnapshot.key!!
                        if(userID == currUser){
//                            Toast.makeText(this,"delete this one (TaggedEvents) " + userID,Toast.LENGTH_LONG).show()

                                        taggedEvents.child(userID).removeValue()
                                            .addOnSuccessListener {
//                                                Toast.makeText(context, "User deleted: $userID", Toast.LENGTH_SHORT).show()
                                            }
                                            .addOnFailureListener { e ->
//                                                Toast.makeText(context, "Failed to delete user: $userID", Toast.LENGTH_SHORT).show()
                                                Log.e("Firebase", "Failed to delete user: $userID", e)
                                            }
                                        break
                        }

                    }

                }

                override fun onCancelled(error: DatabaseError) {
                    TODO("Not yet implemented")
                }
            })
        }


        eventNameTextView.text = eventName
        addressTextView.text = address
        startTimeTextView.text = start
        endTimeTextView.text = end
        descriptionTextView.text = desc
        val headerImage = findViewById<ImageView>(R.id.headerImage)

        var storageRef = FirebaseStorage.getInstance().reference.child("eventImages")
        if(imagePathsArray.isNotEmpty()){
            Glide.with(headerImage)
                .load(storageRef.child(imagePathsArray[0]) )
                .diskCacheStrategy(DiskCacheStrategy.NONE)
                .skipMemoryCache(true)
                .centerCrop()
                .error(R.drawable.baseline_pictures_24)
                .into(headerImage)
        }






    }
}