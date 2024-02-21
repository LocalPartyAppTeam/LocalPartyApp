package com.example.partyapp

import android.annotation.SuppressLint
import android.content.Intent
import android.content.pm.PackageManager
import android.location.Location
import android.location.LocationManager
import android.os.Bundle
import android.os.Looper
import android.provider.Settings
import android.util.Log
import android.view.MotionEvent
import android.view.View
import android.widget.EditText
import android.widget.ScrollView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import com.example.partyapp.place.Place
import com.example.partyapp.place.PlacesReader
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationCallback
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.location.LocationResult
import com.google.android.gms.location.LocationServices
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import com.google.android.gms.tasks.OnCompleteListener


class AddPartyActivity : AppCompatActivity(), OnMapReadyCallback {
    private var lat = 40.7357
    private var long = -74.172363
    private val MAPVIEW_BUNDLE_KEY = "MapViewBundleKey"
    private var locationManager : LocationManager? = null
    private val PERMISSION_ID = 44

    private lateinit var mFusedLocationClient: FusedLocationProviderClient
    @SuppressLint("ClickableViewAccessibility")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_party)
        mFusedLocationClient = LocationServices.getFusedLocationProviderClient(this)

        getLastLocation()
        val mapF =findViewById<View>(R.id.mapOverlay)
        val mainScrollView = findViewById<ScrollView>(R.id.addPartyScrollView)
        val partyDescription = findViewById<EditText>(R.id.PartyDescEntry)

        val mapFragment = supportFragmentManager
            .findFragmentById(R.id.mapFragment) as SupportMapFragment
        mapFragment.getMapAsync(this)

        partyDescription.setOnTouchListener { v, motionEvent ->
            when (motionEvent?.action) {
                MotionEvent.ACTION_DOWN -> mainScrollView.requestDisallowInterceptTouchEvent(true)
                MotionEvent.ACTION_UP -> mainScrollView.requestDisallowInterceptTouchEvent(false)
            }
            return@setOnTouchListener v?.onTouchEvent(motionEvent) ?: true
        }
        mapF.setOnTouchListener { v, motionEvent ->
            when (motionEvent?.action) {
                MotionEvent.ACTION_DOWN -> mainScrollView.requestDisallowInterceptTouchEvent(true)
                MotionEvent.ACTION_UP -> mainScrollView.requestDisallowInterceptTouchEvent(false)
            }
            return@setOnTouchListener v?.onTouchEvent(motionEvent) ?: true
        }


    }

    override fun onMapReady(googleMap: GoogleMap) {

        // method to get the location
        getLastLocation()
        val location = LatLng(lat, long)
        Log.i("testdata","lat: ${long}")
        Log.i("testdata","lat: ${lat}")
        googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(location, 12F))
        googleMap.addMarker(
            MarkerOptions()
                .position(location)
                .title("Newark")
        )
        googleMap.setOnMapClickListener {
                googleMap.clear()
                val markerOptions = MarkerOptions()
                markerOptions.position(it)
                markerOptions.title("Party Here")
                markerOptions.snippet("Snippet")
                markerOptions.visible(true)
                googleMap.addMarker(
                    markerOptions
                )
        }
    }
    @SuppressLint("MissingPermission")
    private fun getLastLocation(){
        // check if permissions are given
        if (checkPermissions()) {

            // check if location is enabled
            if (isLocationEnabled()) {

                // getting last
                // location from
                // FusedLocationClient
                // object
                mFusedLocationClient.lastLocation.addOnCompleteListener(OnCompleteListener<Location?> { task ->
                    val location: Location? = task.result
                    if (location == null) {
                        requestNewLocationData()
                    } else {
                        lat = location.getLatitude()
                        long = location.getLongitude()
                    }
                })
            } else {
                Toast.makeText(this, "Please turn on" + " your location...", Toast.LENGTH_LONG)
                    .show()
                val intent = Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS)
                startActivity(intent)
            }
        } else {
            // if permissions aren't available,
            // request for permissions
            requestPermissions()
            if(checkPermissions()){
                getLastLocation()
            }
        }
    }
    @SuppressLint("MissingPermission")
    private fun requestNewLocationData() {

        // Initializing LocationRequest
        // object with appropriate methods
        val mLocationRequest = LocationRequest()
        mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY)
        mLocationRequest.setInterval(5)
        mLocationRequest.setFastestInterval(0)
        mLocationRequest.setNumUpdates(1)

        // setting LocationRequest
        // on FusedLocationClient
        mFusedLocationClient = LocationServices.getFusedLocationProviderClient(this)
        mFusedLocationClient.requestLocationUpdates(
            mLocationRequest,
            mLocationCallback,
            Looper.myLooper()
        )
    }

    private val mLocationCallback: LocationCallback = object : LocationCallback() {
        override fun onLocationResult(locationResult: LocationResult) {
            val mLastLocation = locationResult.lastLocation
            lat = mLastLocation.latitude
            long = mLastLocation.longitude
        }
    }
    private fun checkPermissions(): Boolean
    {
        LocationCallback()
        return ActivityCompat.checkSelfPermission(
            this,
            android.Manifest.permission.ACCESS_COARSE_LOCATION
        ) == PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(
            this,
            android.Manifest.permission.ACCESS_FINE_LOCATION
        ) == PackageManager.PERMISSION_GRANTED;
    }
        private fun requestPermissions() {
        ActivityCompat.requestPermissions(
            this, arrayOf<String>(
                android.Manifest.permission.ACCESS_COARSE_LOCATION,
                android.Manifest.permission.ACCESS_FINE_LOCATION
            ), PERMISSION_ID
        )
    }
    private fun isLocationEnabled(): Boolean {
        val locationManager = getSystemService(LOCATION_SERVICE) as LocationManager
        return locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER) || locationManager.isProviderEnabled(
            LocationManager.NETWORK_PROVIDER
        )
    }
    @Override
    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<String?>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == PERMISSION_ID) {
            if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                getLastLocation()
            }
        }
    }
    override fun onResume() {
        super.onResume()
        if (checkPermissions()) {
            getLastLocation()
        }
    }

}