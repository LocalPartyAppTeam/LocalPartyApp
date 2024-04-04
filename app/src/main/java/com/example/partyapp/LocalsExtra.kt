package com.example.partyapp

import android.os.Bundle
import android.util.Log
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.flexbox.FlexboxLayoutManager
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener

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
        val lat = intent.getDoubleExtra("lat",0.0)
        val long = intent.getDoubleExtra("long",0.0)
        val imagePathsArray = intent.getStringArrayExtra("imgPaths")?.toList() ?: emptyList()
        val tags = intent.getStringArrayExtra("tags")?.toList() ?: emptyList()
        val sanTags = intent.getStringArrayExtra("sanitizedTags")?.toList() ?: emptyList()
        val tagModelList = mutableListOf<TagModel>()
        for(tag in tags){
            tagModelList.add(TagModel(text=tag))
            Log.i("Tags","$tag")
        }
        val nearbyMiniList = mutableListOf<EstablishmentModel>()
        val geoHelper = GeoHelper(this)
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

        val tagsRV = findViewById<RecyclerView>(R.id.localEventTagsRV)
        val tagsAdapter = TagsAdapter(false, tagModelList)
        tagsRV.layoutManager = FlexboxLayoutManager(this)
        tagsRV.adapter = tagsAdapter


        val nearbyRV = findViewById<RecyclerView>(R.id.nearbyEstablishmentRV)
        val miniEstAdapter = MiniEstAdapter(nearbyMiniList, this)
        nearbyRV.layoutManager = LinearLayoutManager(this, RecyclerView.HORIZONTAL,false)
        nearbyRV.adapter = miniEstAdapter

        val databaseReference = FirebaseDatabase.getInstance().getReference("TaggedEstablishments")
        databaseReference.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                for (uniqueIDSnapshot in dataSnapshot.children) {
                    val uniqueID = uniqueIDSnapshot.key
                    uniqueID?.let {
                        for (establishmentSnapshot in uniqueIDSnapshot.children) {
                            val establishmentName = establishmentSnapshot.key
                            establishmentName?.let {
                                val lat = establishmentSnapshot.child("lat").getValue(Double::class.java)
                                val long = establishmentSnapshot.child("long").getValue(Double::class.java)
                                val name = establishmentSnapshot.child("name").getValue(String::class.java)
                                val desc = establishmentSnapshot.child("desc").getValue(String::class.java)
                                val owner = establishmentSnapshot.child("ownerAccount").getValue(String::class.java)
                                val estAddress = geoHelper.getAddress(lat,long)
                                val imgPathsSnapshot = establishmentSnapshot.child("imgPaths")
                                val imagePaths = mutableListOf<String>()
                                for (imageSnapshot in imgPathsSnapshot.children) {
                                    val imagePath = imageSnapshot.getValue(String::class.java)
                                    imagePath?.let {
                                        imagePaths.add(it)
                                    }
                                }
                                val tagsSnapshot = establishmentSnapshot.child("tags")
                                val tags = mutableListOf<String>()
                                for (tagSnapshot in tagsSnapshot.children) {
                                    val tag = tagSnapshot.getValue(String::class.java)
                                    tag?.let {
                                        tags.add(it)
                                    }
                                }
                                val sanTagsSnapshot = establishmentSnapshot.child("sanitizedTags")
                                val sanTags = mutableListOf<String>()
                                for (sanTagSnapshot in sanTagsSnapshot.children) {
                                    val sanTag = sanTagSnapshot.getValue(String::class.java)
                                    sanTag?.let {
                                        sanTags.add(it)
                                    }
                                }
                                ///i need establishmentName, address, distance, description, ownerAccount,iP
                                // Create Establishment object and add to the list
                                val establishment = EstablishmentModel(owner,lat,long,name,desc,estAddress,imagePaths,tags,sanTags)
                                nearbyMiniList.add(establishment)

                            }
                        }
                    }
                }
                nearbyMiniList.sortBy { geoHelper.calculateDistance(it.lat!!,it.long!!,lat,long) }
                miniEstAdapter.notifyDataSetChanged()
            }

            override fun onCancelled(databaseError: DatabaseError) {
                Toast.makeText(applicationContext,"error", Toast.LENGTH_LONG).show()
            }
        })

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
