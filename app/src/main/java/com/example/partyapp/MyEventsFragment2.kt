package com.example.partyapp

import android.Manifest
import android.content.pm.PackageManager
import android.location.Address
import android.location.Geocoder
import android.os.Build
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.EditText
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.core.app.ActivityCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.firebase.Firebase
import com.google.firebase.FirebaseApp
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import java.util.Locale
import com.google.android.gms.location.LocationServices
import com.google.firebase.auth.FirebaseAuth
import kotlin.math.pow

import com.google.firebase.database.database
import com.google.firebase.initialize
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.time.format.TextStyle

@RequiresApi(Build.VERSION_CODES.O)
class MyEventsFragment2 : Fragment(){
    private lateinit var recyclerView: RecyclerView
    //private lateinit var recyclerViewA: RecyclerView
    private lateinit var adapter: MyEventsAdapter
    private lateinit var eventsReference: DatabaseReference
    private lateinit var auth: FirebaseAuth
    private lateinit var geocoder: Geocoder
    private lateinit var fusedLocationClient: FusedLocationProviderClient


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        geocoder = Geocoder(requireContext(), Locale.getDefault())
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(requireContext())
        FirebaseApp.initializeApp(requireContext())
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_my_events, container, false)
        recyclerView = view.findViewById(R.id.recycler_locals)
        val navigationBarHeight = resources.getDimensionPixelSize(com.google.android.material.R.dimen.design_bottom_navigation_height)
        recyclerView.setPadding(0, 0, 0, navigationBarHeight)
        recyclerView.layoutManager = LinearLayoutManager(requireContext())

        eventsReference = FirebaseDatabase.getInstance().getReference("Events")
        //Toast.makeText(requireContext(),"EventsReference path: ${eventsReference}",Toast.LENGTH_LONG).show()
        eventsReference.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                val eventList = mutableListOf<MyItem>()
                val curruser = auth.currentUser
                val curruserid = curruser?.uid
                for (userSnapshot in dataSnapshot.children) {
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
                            val dayOfWeek = getDayOfWeek(startDate)
                            val dayOfMonth = getDayofMonth(startDate)
                            val address = getAddress(eventLat,eventLong)
                            var distance = 0.0

                            val imgPathsSnapshot = eventSnapshot.child("imgPaths")
                            val imagePaths = mutableListOf<String>()
                            for (imageSnapshot in imgPathsSnapshot.children) {
                                val imagePath = imageSnapshot.getValue(String::class.java)
                                imagePath?.let {
                                    imagePaths.add(it)
                                }
                            }

                            val eventItem =MyItem(eventName.toString(), eventHost.toString(), address.toString(), startTime.toString() + "-" +  endTime.toString(), (distance).toString() + " mi", dayOfWeek, dayOfMonth,eventStart, eventDesc,imagePaths)
                                if (userId == curruserid) {
                                    eventList.add(eventItem)
                                } else {
                                    userId == curruserid
                                }
                        }
                    }
                }
//                localsList.add(LocalItem("Event 1", "Host 1", "Address 1", "Time 1", "Date 1", "Mon","20"))
//                localsList.sortBy { it.distance?.toDouble() }

                eventList.sortBy { it.distance?.replace(" mi", "")?.toDouble() }
                adapter = MyEventsAdapter(eventList)
                recyclerView.adapter = adapter
            }

            override fun onCancelled(error: DatabaseError) {
                //TODO("Not yet implemented")
                Toast.makeText(requireContext(),"error fetching", Toast.LENGTH_LONG).show()
            }
        })

        return view
    }

    private fun getDayOfWeek(date: LocalDate): String {
        val dayOfWeek = date.dayOfWeek
        return dayOfWeek.getDisplayName(TextStyle.SHORT, Locale.getDefault()).uppercase()
    }
    private fun getDayofMonth(date: LocalDate): String{
        return date.dayOfMonth.toString()
    }

    private fun getAddress(latitude: Double?, longitude: Double?): String? {
        if (latitude == null || longitude == null) return null
        try {
            val addresses: List<Address>? = geocoder.getFromLocation(latitude, longitude, 1)
            if (addresses != null && addresses.isNotEmpty()) {
                val address = addresses[0]
                val stringBuilder = StringBuilder()

                for (i in 0..address.maxAddressLineIndex) stringBuilder.append(address.getAddressLine(i)).append("\n")

                return stringBuilder.toString()
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }

        return null
    }
}