package com.example.partyapp
import android.Manifest
import android.content.pm.PackageManager
import android.location.Geocoder
import android.os.Build
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.core.app.ActivityCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.firebase.FirebaseApp
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import java.util.Locale
import com.google.android.gms.location.LocationServices
import kotlin.math.pow

//import com.google.firebase.database.core.view.View
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.time.format.TextStyle

@RequiresApi(Build.VERSION_CODES.O)
class LocalsFragment2 : Fragment() {
    private lateinit var recyclerView: RecyclerView
    private lateinit var recyclerViewE: RecyclerView
    private lateinit var adapter: LocalsAdapter
    private lateinit var adapterE: LocalEstablishmentAdapter
    private lateinit var eventsReference: DatabaseReference
    private lateinit var geocoder: Geocoder
    private lateinit var fusedLocationClient: FusedLocationProviderClient
    private lateinit var searchbar : EditText


    private var currentLatitude: Double = 0.0
    private var currentLongitude: Double = 0.0


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        FirebaseApp.initializeApp(requireContext())
        geocoder = Geocoder(requireContext(), Locale.getDefault())
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(requireContext())
    }
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        val geoHelper = GeoHelper(requireContext())
        val loc = geoHelper.requestLocation()
        if(loc != null){
            currentLatitude = loc[0]
            currentLongitude = loc[1]
        }
        val view = inflater.inflate(R.layout.fragment_locals, container, false)
        recyclerView = view.findViewById(R.id.recycler_locals)
        val navigationBarHeight = resources.getDimensionPixelSize(com.google.android.material.R.dimen.design_bottom_navigation_height)
        recyclerView.setPadding(0, 0, 0, navigationBarHeight)
        recyclerViewE = view.findViewById(R.id.recycler_establishments)
        recyclerView.layoutManager = LinearLayoutManager(requireContext())
        recyclerViewE.layoutManager = LinearLayoutManager(requireContext(),LinearLayoutManager.HORIZONTAL,true)


        /*
        i have to add the data from the local establishments google firebase
         */


        val databaseReference = FirebaseDatabase.getInstance().getReference("TaggedEstablishments")
        databaseReference.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                val establishmentsList = mutableListOf<EstablishmentModel>()
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
                                establishmentsList.add(establishment)

                            }
                        }
                    }
                }
                adapterE = LocalEstablishmentAdapter(arrayOf(currentLatitude, currentLongitude), geoHelper, establishmentsList)
                recyclerViewE.adapter = adapterE
            }

            override fun onCancelled(databaseError: DatabaseError) {
                Toast.makeText(requireContext(),"error",Toast.LENGTH_LONG).show()
            }
        })






        /*
        the below is to add the information of for the local events firebase and adding to adapter
         */
        ///random events will add from firebase later
        eventsReference = FirebaseDatabase.getInstance().getReference("TaggedEvents")
        //Toast.makeText(requireContext(),"EventsReference path: ${eventsReference}",Toast.LENGTH_LONG).show()
        eventsReference.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                val localsList = mutableListOf<EventModel>()
                for (userSnapshot in dataSnapshot.children){
                    val userId = userSnapshot.key ///the host
                    userId?.let {
                        for (eventSnapshot in userSnapshot.children) {
                            val eventName = eventSnapshot.child("name").getValue(String::class.java)
                            val eventDesc = eventSnapshot.child("desc").getValue(String::class.java) //i need this
                            val eventHost = eventSnapshot.child("host").getValue(String::class.java)
                            val eventLat = eventSnapshot.child("lat").getValue(Double::class.java)
                            val eventLong = eventSnapshot.child("long").getValue(Double::class.java)
                            val eventStart = eventSnapshot.child("start").getValue(String::class.java)
                            val eventEnd = eventSnapshot.child("end").getValue(String::class.java)
                            val startTime = eventStart.toString().substringAfter('T')
                            val endTime = eventEnd.toString().substringAfter('T')
                            val dateFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm")
                            val startDate = LocalDate.parse(eventStart, dateFormatter)
                            val endDate = LocalDate.parse(eventEnd, dateFormatter)
                            val dayOfWeek = getDayOfWeek(startDate)
                            val dayOfMonth = getDayofMonth(startDate)
                            val address = geoHelper.getAddress(eventLat,eventLong)
                            var distance = 0.0

                            if(eventLat != null && eventLong != null) distance = geoHelper.calculateDistance(currentLatitude, currentLongitude, eventLat, eventLong)
//                            Toast.makeText(requireContext(),dayOfWeek.toString(),Toast.LENGTH_SHORT).show()
                            /* structure of card item
                            val eventName: String,
                                val host: String,
                                val address: String,
                                val time: String,
                                val distance: String,
                                val dayOfWeek: String,
                                val dayOfMonth: String
                             */
                            /*
                            firebase structure
                            desc,end,host,imgPaths,lat,long,name,start
                            format: 2024-04-21T10:30
                             */
                            /*
                            i need the imgPaths now
                             */
                            val imgPathsSnapshot = eventSnapshot.child("imgPaths")
                            val imagePaths = mutableListOf<String>()
                            for (imageSnapshot in imgPathsSnapshot.children) {
                                val imagePath = imageSnapshot.getValue(String::class.java)
                                imagePath?.let {
                                    imagePaths.add(it)
                                }
                            }
                            val tagsSnapshot = eventSnapshot.child("tags")
                            val tags = mutableListOf<String>()
                            for (tagSnapshot in tagsSnapshot.children) {
                                val tag = tagSnapshot.getValue(String::class.java)
                                tag?.let {
                                    tags.add(it)
                                    Log.i("TagsLF2","tag $it")
                                }
                            }

                            val sanTagsSnapshot = eventSnapshot.child("sanitizedTags")
                            val sanTags = mutableListOf<String>()
                            for (sanTagSnapshot in sanTagsSnapshot.children) {
                                val sanTag = sanTagSnapshot.getValue(String::class.java)
                                sanTag?.let {
                                    sanTags.add(it)
                                    Log.i("TagsLF2","santag $it")
                                }
                            }

                            Log.i("TagsLF2","tags array size ${tags!!.size}")
                            val localEvent = EventModel(eventHost.toString(), eventLat, eventLong,
                                address.toString(), eventStart, eventEnd, eventName.toString(),
                                eventDesc, imagePaths, tags, sanTags)
                            Log.i("TagsLF2","tags array size after insert ${localEvent.tags!!.size}")
                            localsList.add(localEvent)
                        }
                    }
                }
//                localsList.add(LocalItem("Event 1", "Host 1", "Address 1", "Time 1", "Date 1", "Mon","20"))
//                localsList.sortBy { it.distance?.toDouble() }

                localsList.sortBy { geoHelper.calculateDistance(it.lat!!,it.long!!,currentLatitude,currentLongitude) }
                adapter = LocalsAdapter(arrayOf(currentLatitude,currentLongitude), geoHelper, localsList)
                recyclerView.adapter = adapter
            }

            override fun onCancelled(databaseError: DatabaseError) {
                Toast.makeText(requireContext(),"error fetching", Toast.LENGTH_LONG).show()
            }
        })


//        val localsList = mutableListOf<LocalItem>()
//        localsList.add(LocalItem("Event 1", "Host 1", "Address 1", "Time 1", "Date 1", "Mon","20"))
//        localsList.add(LocalItem("Event 2", "Host 2", "Address 2", "Time 2", "Date 2","Mon","20"))
//        adapter = LocalsAdapter(localsList)
//        recyclerView.adapter = adapter
        return view

    }
    private fun getDayOfWeek(date: LocalDate): String {
        val dayOfWeek = date.dayOfWeek
        return dayOfWeek.getDisplayName(TextStyle.SHORT, Locale.getDefault()).uppercase()
    }
    private fun getDayofMonth(date: LocalDate): String{
        return date.dayOfMonth.toString()
    }


    fun round(number: Double, decimals: Int): Double {
        if (decimals < 0) throw IllegalArgumentException("no work")
        val factor = 10.0.pow(decimals)
        return Math.round(number * factor) / factor
    }

}
