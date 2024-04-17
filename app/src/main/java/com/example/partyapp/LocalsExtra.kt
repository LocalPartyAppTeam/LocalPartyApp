package com.example.partyapp

import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.flexbox.FlexboxLayoutManager
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.MapView
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationCallback
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.location.LocationResult
import com.google.android.gms.location.LocationServices
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.auth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import kotlin.properties.Delegates

class LocalsExtra : AppCompatActivity(), OnMapReadyCallback {
    private lateinit var auth: FirebaseAuth
    private var lat: Double = 0.0
    private var long: Double = 0.0
    private var eventName: String = ""
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.locals_extra)
        auth = Firebase.auth
        eventName = intent.getStringExtra("name").toString()
        val host = intent.getStringExtra("host")
        val event = intent.getParcelableExtra<EventModel>("event")
        val address = intent.getStringExtra("address")
        val start = intent.getStringExtra("start")
        val end = intent.getStringExtra("end")
        val desc = intent.getStringExtra("desc")
        lat = intent.getDoubleExtra("lat",0.0)
        long = intent.getDoubleExtra("long",0.0)
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
        val joinButton = findViewById<Button>(R.id.join_event)
        joinButton.setOnClickListener {
            event?.pushId?.let { it1 ->
                FirebaseDatabase.getInstance().getReference("UsersAttending")
                    .child(auth.currentUser!!.uid).child(
                    it1
                )
            }?.setValue(event)
        }
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
        val miniEstAdapter = MiniEstAdapter(nearbyMiniList, this, arrayOf<Double>(lat,long))
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
                                val push = establishmentSnapshot.child("pushId").getValue(String::class.java)
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
                                val establishment = EstablishmentModel(push,owner,lat,long,name,desc,estAddress,imagePaths,tags,sanTags)
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


        val mapFragment = supportFragmentManager
            .findFragmentById(R.id.mapFragment) as SupportMapFragment
        mapFragment.getMapAsync(this)

//        Toast.makeText(this,imagePathsArray[0].toString(),Toast.LENGTH_LONG).show()

        val concatenatedPaths = imagePathsArray.take(6).joinToString("\n")
//        val imagePathsTextView = findViewById<TextView>(R.id.imagePathsTextView)
//        imagePathsTextView.text = concatenatedPaths
//        if (imagePathsArray.size > 6) {
//            Toast.makeText(this, "Only the first 6 image paths are displayed", Toast.LENGTH_SHORT).show()
//        }

//        Toast.makeText(this,eventName,Toast.LENGTH_LONG).show()


    }

    override fun onMapReady(googleMap: GoogleMap) {
        val localLocation = LatLng(lat, long)
        googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(localLocation, 15f))
        googleMap.addMarker(MarkerOptions().position(localLocation).title(eventName))
    }


}
