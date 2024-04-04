package com.example.partyapp
import android.os.Bundle
import android.util.Log
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.RecyclerView
import com.google.android.flexbox.FlexboxLayoutManager
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.MapView
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions

class EstablishmentExtra : AppCompatActivity() {
    private lateinit var mapView: MapView
    private var lat: Double = 0.0
    private var long: Double = 0.0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.establishment_extra)

        val establishmentName = intent.getStringExtra("name")
        val owner = intent.getStringExtra("owner")
        val address = intent.getStringExtra("address")
        val description = intent.getStringExtra("desc")
        lat = intent.getDoubleExtra("lat",0.0)
        long = intent.getDoubleExtra("long",0.0)
        val tags = intent.getStringArrayExtra("tags")?.toList() ?: emptyList()
        val imagePathsArray = intent.getStringArrayExtra("imgPaths")?.toList() ?: emptyList()
        val tagModelList = mutableListOf<TagModel>()
        for(tag in tags){
            tagModelList.add(TagModel(text=tag))
            Log.i("Tags","$tag")
        }

        mapView = findViewById(R.id.EstExtraMapView)
        mapView.onCreate(savedInstanceState)
        mapView.getMapAsync(this)

        val establishmentNameTextView = findViewById<TextView>(R.id.textView_establishmentName)
//        val ownerTextView = findViewById<TextView>(R.id.textView_owner)
        val addressTextView = findViewById<TextView>(R.id.EstExtraAddress)
        val descriptionTextView = findViewById<TextView>(R.id.EstExtraDescription)
//        val distanceTextView = findViewById<TextView>(R.id.textView_distance)

        val tagsRV = findViewById<RecyclerView>(R.id.estExtraTagsRV)
        val tagsAdapter = TagsAdapter(false, tagModelList)
        tagsRV.layoutManager = FlexboxLayoutManager(this)
        tagsRV.adapter = tagsAdapter

        establishmentNameTextView.text = "$establishmentName"
//        ownerTextView.text = "Owner: $owner"
        addressTextView.text = "$address"
        descriptionTextView.text = "$description"
//        distanceTextView.text = "Distance: $distance"


    }
    fun onMapReady(googleMap: GoogleMap) {
        // Add a marker for the establishment and move the camera



        val establishmentLocation = LatLng(75.00, 75.00)
        googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(establishmentLocation, 15f))
        googleMap.addMarker(MarkerOptions().position(establishmentLocation).title("Establishment Name"))
    }

    override fun onResume() {
        super.onResume()
        mapView.onResume()
    }

    override fun onPause() {
        super.onPause()
        mapView.onPause()
    }

    override fun onDestroy() {
        super.onDestroy()
        mapView.onDestroy()
    }

    override fun onLowMemory() {
        super.onLowMemory()
        mapView.onLowMemory()
    }

}

private fun MapView.getMapAsync(establishmentExtra: EstablishmentExtra) {

}
