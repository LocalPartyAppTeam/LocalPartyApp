package com.example.partyapp

import android.content.Intent
import android.location.Geocoder
import android.os.Build
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.annotation.RequiresApi
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
import com.google.android.material.card.MaterialCardView
import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.auth
import kotlin.math.pow



@RequiresApi(Build.VERSION_CODES.P)
class MyEventsFragment : Fragment() {
    private lateinit var attendingRecyclerView: RecyclerView
    private lateinit var ownRecyclerView: RecyclerView

    private lateinit var attendingAdapter: MyEventsAdapter
    private lateinit var ownEventsAdapter: MyEventsAdapter

    private lateinit var attendingRef: DatabaseReference
    private lateinit var ownEventsRef: DatabaseReference

    private lateinit var geocoder: Geocoder
    private lateinit var fusedLocationClient: FusedLocationProviderClient
    private lateinit var auth: FirebaseAuth

    private var currentLatitude: Double = 40.7357
    private var currentLongitude: Double = -74.172363

    override fun onCreate(savedInstanceState: Bundle?) {super.onCreate(savedInstanceState)
        FirebaseApp.initializeApp(requireContext())
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(requireContext())
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        auth = Firebase.auth
        val geoHelper = GeoHelper(requireContext())
        val attendingEventList = mutableListOf<EventModel>()
        val ownEventList = mutableListOf<EventModel>()
        val view = inflater.inflate(R.layout.fragment_my_events, container, false)
        val loc = geoHelper.requestLocation()
        if(loc != null){
            currentLatitude = loc[0]
            currentLongitude = loc[1]
        }
        val launchAddEventActivity = view.findViewById<ImageView>(R.id.eventsFragmentAddEventButton)
        val launchAddEstActivity = view.findViewById<ImageView>(R.id.eventsFragmentAddEstablishmentButton)

        attendingRecyclerView = view.findViewById(R.id.myEventsAttendRV)
        ownRecyclerView = view.findViewById(R.id.myEventsOwnRV)

        attendingRecyclerView.layoutManager = LinearLayoutManager(requireContext(),LinearLayoutManager.HORIZONTAL,false)
        ownRecyclerView.layoutManager = LinearLayoutManager(requireContext(),LinearLayoutManager.HORIZONTAL,false)

        attendingAdapter = MyEventsAdapter(requireContext(),arrayOf(currentLatitude,currentLongitude), geoHelper, attendingEventList, false)
        ownEventsAdapter = MyEventsAdapter(requireContext(),arrayOf(currentLatitude, currentLongitude), geoHelper, ownEventList, true)

        attendingRecyclerView.adapter = attendingAdapter
        ownRecyclerView.adapter = ownEventsAdapter


        launchAddEventActivity.setOnClickListener {
            startActivity(Intent(activity, AddPartyActivity::class.java))
        }
        launchAddEstActivity.setOnClickListener {
            startActivity(Intent(activity, AddEstablishmentActivity::class.java))
        }
        attendingRef = FirebaseDatabase.getInstance().getReference("UsersAttending").child(auth.currentUser!!.uid)
        ownEventsRef = FirebaseDatabase.getInstance().getReference("TaggedEvents").child(auth.currentUser!!.uid)
        attendingRef.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                for (uniqueIDSnapshot in dataSnapshot.children) {
                    val uniqueID = uniqueIDSnapshot.key
                    uniqueID?.let {
                            val event = uniqueIDSnapshot.getValue(EventModel::class.java)
                            attendingEventList.add(event!!)
                            attendingAdapter.notifyItemInserted(attendingAdapter.itemCount - 1)
                    }
                }
                attendingEventList.sortBy { geoHelper.calculateDistance(it.lat!!,it.long!!,currentLatitude,currentLongitude) }
                attendingAdapter.notifyDataSetChanged()
            }
            override fun onCancelled(databaseError: DatabaseError) {
                Toast.makeText(requireContext(),"error",Toast.LENGTH_LONG).show()
            }
        })
        ownEventsRef.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                for (uniqueIDSnapshot in dataSnapshot.children) {
                    val uniqueID = uniqueIDSnapshot.key
                    uniqueID?.let {
                            val event = uniqueIDSnapshot.getValue(EventModel::class.java)
                            Log.i("CHECK","${event?.tags?.size}")
                            ownEventList.add(event!!)
                            ownEventsAdapter.notifyItemInserted(ownEventsAdapter.itemCount - 1)
                    }
                }
            }
            override fun onCancelled(databaseError: DatabaseError) {
                Toast.makeText(requireContext(),"error",Toast.LENGTH_LONG).show()
            }
        })
        return view
    }

}