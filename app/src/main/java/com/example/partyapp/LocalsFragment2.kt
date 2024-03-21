package com.example.partyapp
import android.Manifest
import android.content.pm.PackageManager
import android.location.Address
import android.location.Geocoder
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.EditText
import android.widget.Toast
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
import org.threeten.bp.LocalDate
import org.threeten.bp.format.TextStyle
import org.threeten.bp.DayOfWeek
import org.threeten.bp.format.DateTimeFormatter
import java.util.Locale
import com.google.android.gms.location.LocationServices
import kotlin.math.pow

//import com.google.firebase.database.core.view.View
import com.google.firebase.database.database
import com.google.firebase.initialize

class LocalsFragment2 : Fragment() {
    private lateinit var recyclerView: RecyclerView
    private lateinit var adapter: LocalsAdapter
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

        requestLocation()
        val view = inflater.inflate(R.layout.fragment_locals, container, false)
        recyclerView = view.findViewById(R.id.recycler_locals)
        recyclerView.layoutManager = LinearLayoutManager(requireContext())
        ///random events will add from firebase later
        eventsReference = FirebaseDatabase.getInstance().getReference("Events")
        //Toast.makeText(requireContext(),"EventsReference path: ${eventsReference}",Toast.LENGTH_LONG).show()
        eventsReference.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                val localsList = mutableListOf<LocalItem>()
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
                            val dayOfWeek = getDayOfWeek(startDate)
                            val dayOfMonth = getDayofMonth(startDate)
                            val address = getAddress(eventLat,eventLong)
                            var distance = 0.0

                            if(eventLat != null && eventLong != null) distance = calculateDistance(currentLatitude, currentLongitude, eventLat, eventLong)
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
                            val localItem = LocalItem(eventName.toString(), eventHost.toString(), address.toString(), startTime.toString() + "-" +  endTime.toString(), round(distance*0.621371,2).toString() + " mi", dayOfWeek, dayOfMonth,eventStart, eventDesc,imagePaths)
                            localsList.add(localItem)
                        }
                    }
                }

//                localsList.add(LocalItem("Event 1", "Host 1", "Address 1", "Time 1", "Date 1", "Mon","20"))
                adapter = LocalsAdapter(localsList)
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
    private fun calculateDistance(
        lat1: Double, lon1: Double,
        lat2: Double, lon2: Double
    ): Double {
        val R = 6371 // Radius of the Earth in kilometers
        val latDistance = Math.toRadians(lat2 - lat1)
        val lonDistance = Math.toRadians(lon2 - lon1)
        val a = (Math.sin(latDistance / 2) * Math.sin(latDistance / 2)
                + (Math.cos(Math.toRadians(lat1)) * Math.cos(Math.toRadians(lat2))
                * Math.sin(lonDistance / 2) * Math.sin(lonDistance / 2)))
        val c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a))
        return R * c // Distance in kilometers
    }
    private fun requestLocation() {
        if (ActivityCompat.checkSelfPermission(
                requireContext(),
                Manifest.permission.ACCESS_FINE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(
                requireContext(),
                Manifest.permission.ACCESS_COARSE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            return
        }
        fusedLocationClient.lastLocation
            .addOnSuccessListener { location ->
                if (location != null) {
                     currentLatitude = location.latitude
                    currentLongitude = location.longitude
                    //Toast.makeText(requireContext(),currentLatitude.toString(), Toast.LENGTH_LONG).show()
                    //Toast.makeText(requireContext(),currentLongitude.toString(), Toast.LENGTH_LONG).show()
                }
            }
            .addOnFailureListener { e ->
                e.printStackTrace()
            }
    }

    fun round(number: Double, decimals: Int): Double {
        if (decimals < 0) throw IllegalArgumentException("no work")
        val factor = 10.0.pow(decimals)
        return Math.round(number * factor) / factor
    }

}
