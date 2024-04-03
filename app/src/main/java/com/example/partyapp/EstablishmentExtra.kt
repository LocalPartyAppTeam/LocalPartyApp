package com.example.partyapp
import android.os.Bundle
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.MapView
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions

class EstablishmentExtra : AppCompatActivity() {
    private lateinit var mapView: MapView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.establishment_extra)

        val establishmentName = intent.getStringExtra("establishmentName")
        val owner = intent.getStringExtra("owner")
        val address = intent.getStringExtra("address")
        val description = intent.getStringExtra("description")
        val distance = intent.getStringExtra("distance")

        mapView = findViewById(R.id.mapView)
        mapView.onCreate(savedInstanceState)
        mapView.getMapAsync(this)

        val establishmentNameTextView = findViewById<TextView>(R.id.textView_establishmentName)
//        val ownerTextView = findViewById<TextView>(R.id.textView_owner)
        val addressTextView = findViewById<TextView>(R.id.textView_address)
        val descriptionTextView = findViewById<TextView>(R.id.textView_description)
//        val distanceTextView = findViewById<TextView>(R.id.textView_distance)

        establishmentNameTextView.text = "$establishmentName"
//        ownerTextView.text = "Owner: $owner"
        addressTextView.text = "$address"
        descriptionTextView.text = "$description"
//        distanceTextView.text = "Distance: $distance"


    }
    fun onMapReady(googleMap: GoogleMap) {
        // Add a marker for the establishment and move the camera



        val establishmentLocation = LatLng(75.00, 75.00)
        googleMap.addMarker(MarkerOptions().position(establishmentLocation).title("Establishment Name"))
        googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(establishmentLocation, 15f))
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
