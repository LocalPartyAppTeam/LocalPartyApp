package com.example.festivviews.fragments
import android.location.Geocoder
import android.os.Build
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.ImageButton
import android.widget.TextView
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.festivviews.GeoHelper
import com.example.festivviews.R
import com.example.festivviews.adapters.LocalEstablishmentAdapter
import com.example.festivviews.adapters.LocalsAdapter
import com.example.festivviews.adapters.TagsAdapter
import com.example.festivviews.models.EstablishmentModel
import com.example.festivviews.models.EventModel
import com.example.festivviews.models.TagModel
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

//import com.google.firebase.database.core.view.View
import java.time.LocalDate
import java.time.format.TextStyle

@RequiresApi(Build.VERSION_CODES.TIRAMISU)
class LocalsFragment : Fragment() {
    private lateinit var recyclerView: RecyclerView
    private lateinit var recyclerViewE: RecyclerView
    private lateinit var adapter: LocalsAdapter
    private lateinit var staticTagsAdapter: TagsAdapter
    private lateinit var filterTagsAdapter: TagsAdapter
    private lateinit var adapterE: LocalEstablishmentAdapter
    private lateinit var eventsReference: DatabaseReference
    private lateinit var estReference: DatabaseReference
    private lateinit var geocoder: Geocoder
    private lateinit var staticTagsRV: RecyclerView
    private lateinit var filterTagsRV: RecyclerView
    private lateinit var fusedLocationClient: FusedLocationProviderClient
    private lateinit var searchbar : EditText
    private lateinit var auth: FirebaseAuth


    private var currentLatitude: Double = 40.7357
    private var currentLongitude: Double = -74.172363


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
        auth = Firebase.auth
        estReference = FirebaseDatabase.getInstance().getReference("TaggedEstablishments")
        eventsReference = FirebaseDatabase.getInstance().getReference("TaggedEvents")

        val geoHelper = GeoHelper(requireContext())
        val loc = geoHelper.requestLocation()
        val establishmentsList = mutableListOf<EstablishmentModel>()
        val localsList = mutableListOf<EventModel>()
        val searchTagStaticList = mutableListOf<TagModel>()
        val searchTagFilterList = mutableListOf<TagModel>()
        val searchTagFilterListStrings = mutableListOf<String>()
        if(loc != null){
            currentLatitude = loc[0]
            currentLongitude = loc[1]
        }
        val view = inflater.inflate(R.layout.fragment_locals, container, false)

        recyclerView = view.findViewById(R.id.recycler_locals)
        recyclerViewE = view.findViewById(R.id.recycler_establishments)
        staticTagsRV = view.findViewById(R.id.tag_list_static_rv)
        filterTagsRV = view.findViewById(R.id.tagFilterTagsRecyclerView)

        staticTagsRV.layoutManager = LinearLayoutManager(requireContext(), RecyclerView.HORIZONTAL, false)
        filterTagsRV.layoutManager = LinearLayoutManager(requireContext(), RecyclerView.HORIZONTAL, false)
        recyclerView.layoutManager = LinearLayoutManager(requireContext(),LinearLayoutManager.HORIZONTAL,false)
        recyclerViewE.layoutManager = LinearLayoutManager(requireContext(),LinearLayoutManager.HORIZONTAL,false)

        staticTagsAdapter = TagsAdapter(false,searchTagStaticList)
        filterTagsAdapter = TagsAdapter(true,searchTagFilterList)
        adapter = LocalsAdapter(requireContext(),arrayOf(currentLatitude,currentLongitude), geoHelper, localsList)
        adapterE = LocalEstablishmentAdapter(requireContext(),arrayOf(currentLatitude, currentLongitude), geoHelper, establishmentsList)

        staticTagsRV.adapter = staticTagsAdapter
        filterTagsRV.adapter = filterTagsAdapter
        recyclerView.adapter = adapter
        recyclerViewE.adapter = adapterE

        val changeTagFiltersTV:TextView = view.findViewById(R.id.tag_list_static_text)
        val filterCardMask:TextView = view.findViewById(R.id.tagCardMask)
        val filterCard:MaterialCardView = view.findViewById(R.id.tag_filter_card)
        val filterCardBackButton: ImageButton = view.findViewById(R.id.filterCardBackButton)
        val filterCardAddTagButton: Button = view.findViewById(R.id.tagFilterAddTagButton)
        val filterCardSubmitButton: Button = view.findViewById(R.id.tagFilterSubmitButton)
        val filterCardTagEntry: EditText = view.findViewById(R.id.tagFilterTagEntry)
        changeTagFiltersTV.setOnClickListener{
            searchTagFilterList.clear()
            searchTagFilterList.addAll(searchTagStaticList)
            filterTagsAdapter.notifyDataSetChanged()
            filterCardMask.visibility = View.VISIBLE
            filterCard.visibility = View.VISIBLE
        }
        filterCardMask.setOnClickListener {
        }
        filterCardBackButton.setOnClickListener{
            filterCardMask.visibility = View.GONE
            filterCard.visibility = View.GONE
            searchTagFilterList.clear()
            searchTagFilterListStrings.clear()
            filterTagsAdapter.notifyDataSetChanged()
        }
        filterCardAddTagButton.setOnClickListener{

            val tagS = filterCardTagEntry.text.toString()
            if(tagS != ""){
                val tag = TagModel(text = tagS)
                searchTagFilterList.add(tag)
                searchTagFilterListStrings.add(Regex("[^A-Za-z0-9 ]").
                replace(tagS.lowercase(), ""))
                filterTagsAdapter.notifyItemInserted(filterTagsAdapter.itemCount-1)
            }
        }
        filterCardSubmitButton.setOnClickListener{
            searchTagStaticList.clear()
            searchTagStaticList.addAll(searchTagFilterList)
            staticTagsAdapter.notifyDataSetChanged()
            establishmentsList.clear()
            localsList.clear()
            adapterE.notifyDataSetChanged()
            adapter.notifyDataSetChanged()
            val filterListSize = searchTagFilterListStrings.size
            estReference.addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onDataChange(dataSnapshot: DataSnapshot) {
                    for (uniqueIDSnapshot in dataSnapshot.children) {
                        val uniqueID = uniqueIDSnapshot.key
                        uniqueID?.let {
                            if(filterListSize > 0) {
                                for (establishmentSnapshot in uniqueIDSnapshot.children) {
                                    val establishmentName = establishmentSnapshot.key
                                    establishmentName?.let {
                                        val establishment =
                                            establishmentSnapshot.getValue(EstablishmentModel::class.java)
                                        if (establishment != null && !establishment.sanitizedTags.isNullOrEmpty()) {
                                            val tagCheck = searchTagFilterListStrings.toSet()
                                                .subtract(establishment.sanitizedTags!!.toSet())
                                            if (tagCheck.isEmpty()) {
                                                establishmentsList.add(establishment)
                                                adapterE.notifyItemInserted(adapterE.itemCount - 1)
                                            }
                                        }
                                    }
                                }
                            }else{
                                for (establishmentSnapshot in uniqueIDSnapshot.children) {
                                    val establishmentName = establishmentSnapshot.key
                                    establishmentName?.let {
                                        val establishment = establishmentSnapshot.getValue(
                                            EstablishmentModel::class.java)
                                        establishmentsList.add(establishment!!)
                                        adapterE.notifyItemInserted(adapterE.itemCount-1)
                                    }
                                }
                            }
                        }
                    }
                }
                override fun onCancelled(databaseError: DatabaseError) {
                    Toast.makeText(requireContext(),"error",Toast.LENGTH_LONG).show()
                }
            })
            eventsReference.addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onDataChange(dataSnapshot: DataSnapshot) {
                    for (userSnapshot in dataSnapshot.children){
                        val userId = userSnapshot.key ///the host
                        userId?.let {
                            if(filterListSize > 0) {
                                for (eventSnapshot in userSnapshot.children) {
                                    val localEvent = eventSnapshot.getValue(EventModel::class.java)
                                    if (localEvent != null && !localEvent.sanitizedTags.isNullOrEmpty()) {
                                        val tagCheck = searchTagFilterListStrings.toSet().subtract(
                                            localEvent.sanitizedTags!!.toSet()
                                        )
                                        if (tagCheck.isEmpty()) {
                                            localsList.add(localEvent)
                                            adapter.notifyItemInserted(adapter.itemCount - 1)
                                        }
                                    }
                                }
                            }else{
                                for (eventSnapshot in userSnapshot.children) {
                                    val localEvent = eventSnapshot.getValue(EventModel::class.java)!!
                                    localsList.add(localEvent)
                                }
                            }
                        }
                    }
                    localsList.sortBy { geoHelper.calculateDistance(it.lat!!,it.long!!,currentLatitude,currentLongitude) }
                    adapter.notifyDataSetChanged()
                }
                override fun onCancelled(databaseError: DatabaseError) {
                    Toast.makeText(requireContext(),"error fetching", Toast.LENGTH_LONG).show()
                }
            })
        }



        /*
        i have to add the data from the local establishments google firebase
         */


        estReference.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                for (uniqueIDSnapshot in dataSnapshot.children) {
                    val uniqueID = uniqueIDSnapshot.key
                    uniqueID?.let {
                        for (establishmentSnapshot in uniqueIDSnapshot.children) {
                            val establishmentName = establishmentSnapshot.key
                            establishmentName?.let {
                                val establishment = establishmentSnapshot.getValue(
                                    EstablishmentModel::class.java)
                                establishmentsList.add(establishment!!)
                                adapterE.notifyItemInserted(adapterE.itemCount-1)
                            }
                        }
                    }
                }
            }
            override fun onCancelled(databaseError: DatabaseError) {
                Toast.makeText(requireContext(),"error",Toast.LENGTH_LONG).show()
            }
        })
        eventsReference.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                for (userSnapshot in dataSnapshot.children){
                    val userId = userSnapshot.key ///the host
                    userId?.let {
                        for (eventSnapshot in userSnapshot.children) {
                            val eventLat = eventSnapshot.child("lat").getValue(Double::class.java)
                            val eventLong = eventSnapshot.child("long").getValue(Double::class.java)
                            var distance = 0.0

                            if(eventLat != null && eventLong != null) distance = geoHelper.calculateDistance(currentLatitude, currentLongitude, eventLat, eventLong)
                            val localEvent = eventSnapshot.getValue(EventModel::class.java)!!
                            localsList.add(localEvent)
                        }
                    }
                }

                localsList.sortBy { geoHelper.calculateDistance(it.lat!!,it.long!!,currentLatitude,currentLongitude) }
                adapter.notifyDataSetChanged()
            }
            override fun onCancelled(databaseError: DatabaseError) {
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


    fun round(number: Double, decimals: Int): Double {
        if (decimals < 0) throw IllegalArgumentException("no work")
        val factor = 10.0.pow(decimals)
        return Math.round(number * factor) / factor
    }

}
