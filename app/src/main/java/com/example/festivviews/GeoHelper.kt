package com.example.festivviews

import android.Manifest
import android.annotation.SuppressLint
import android.app.Activity
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.location.Address
import android.location.Geocoder
import android.location.Location
import android.location.LocationManager
import android.os.Looper
import android.provider.Settings
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContentProviderCompat.requireContext
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationCallback
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.location.LocationResult
import com.google.android.gms.location.LocationServices
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import com.google.android.gms.tasks.OnCompleteListener
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.delay
import java.math.RoundingMode
import java.util.Locale
import kotlin.math.atan2
import kotlin.math.cos
import kotlin.math.round
import kotlin.math.roundToInt
import kotlin.math.sin
import kotlin.math.sqrt

private lateinit var geocoder: Geocoder
private lateinit var fusedLocationClient: FusedLocationProviderClient
private lateinit var mFusedLocationClient: FusedLocationProviderClient
private val PERMISSION_ID = 44
 val UPDATE_INTERVAL_IN_MILLISECONDS: Long = 2000
 val UPDATE_FASTEST_INTERVAL_IN_MILLISECONDS: Long = UPDATE_INTERVAL_IN_MILLISECONDS / 2
class GeoHelper(private val context: Context) {
    var lat: Double? = null
    var long: Double? = null

    fun getAddress(latitude: Double?, longitude: Double?): String? {
        geocoder = Geocoder(context, Locale.getDefault())
        if (latitude == null || longitude == null) return null
        try {
            val addresses: List<Address>? = geocoder.getFromLocation(latitude, longitude, 1)
            if (!addresses.isNullOrEmpty()) {
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
    fun calculateDistance(
        lat1: Double, lon1: Double,
        lat2: Double, lon2: Double
    ): Double {
        val R = 6371 // Radius of the Earth in kilometers
        val latDistance = Math.toRadians(lat2 - lat1)
        val lonDistance = Math.toRadians(lon2 - lon1)
        val a = (sin(latDistance / 2) * sin(latDistance / 2)
                + (cos(Math.toRadians(lat1)) * cos(Math.toRadians(lat2))
                * sin(lonDistance / 2) * sin(lonDistance / 2)))
        val c = 2 * atan2(sqrt(a), sqrt(1 - a))
        return  (R*c*0.621371).toBigDecimal().setScale(1, RoundingMode.HALF_UP).toDouble()// Distance in kilometers gotta change later
    }

    fun requestLocation(): Array<Double>? {
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(context)
        if (ActivityCompat.checkSelfPermission(
                context,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(
                context,
                Manifest.permission.ACCESS_COARSE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            return null
        }
        var loc: Array<Double>? = null
        fusedLocationClient.lastLocation
            .addOnSuccessListener { location ->
                if (location != null) {
                    loc = arrayOf(location.latitude, location.longitude)
                    //Toast.makeText(requireContext(),currentLatitude.toString(), Toast.LENGTH_LONG).show()
                    //Toast.makeText(requireContext(),currentLongitude.toString(), Toast.LENGTH_LONG).show()
                }
            }
            .addOnFailureListener { e ->
                e.printStackTrace()
            }
        return loc
    }

    @SuppressLint("MissingPermission")
    fun getLastLocation(): Array<Double> {

        mFusedLocationClient = LocationServices.getFusedLocationProviderClient(context)
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
                        lat = location.latitude
                        long = location.longitude
                        Log.i("loc","$lat, $long")

                    }
                })
            } else {
                Toast.makeText(context, "Please turn on" + " your location...", Toast.LENGTH_LONG)
                    .show()
                val intent = Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS)
                context.startActivity(intent)
            }
        } else {
            // if permissions aren't available,
            // request for permissions
            requestPermissions()
            if(checkPermissions()){
                getLastLocation()
            }
        }
        return arrayOf<Double>(lat?:0.0,long?:0.0)
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
        mFusedLocationClient = LocationServices.getFusedLocationProviderClient(context)
        mFusedLocationClient.requestLocationUpdates(
            mLocationRequest,
            mLocationCallback,
            Looper.myLooper()
        )
    }

    private val mLocationCallback: LocationCallback = object : LocationCallback() {
        override fun onLocationResult(locationResult: LocationResult) {
            val mLastLocation = locationResult.lastLocation
            lat = mLastLocation!!.latitude
            long = mLastLocation.longitude
        }
    }
    private fun checkPermissions(): Boolean
    {
        return ActivityCompat.checkSelfPermission(
            context,
            android.Manifest.permission.ACCESS_COARSE_LOCATION
        ) == PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(
            context,
            android.Manifest.permission.ACCESS_FINE_LOCATION
        ) == PackageManager.PERMISSION_GRANTED;
    }
    private fun requestPermissions() {
        ActivityCompat.requestPermissions(
            context as Activity, arrayOf<String>(
                android.Manifest.permission.ACCESS_COARSE_LOCATION,
                android.Manifest.permission.ACCESS_FINE_LOCATION
            ), PERMISSION_ID
        )
    }
    private fun isLocationEnabled(): Boolean {
        val locationManager = context.getSystemService(AppCompatActivity.LOCATION_SERVICE) as LocationManager
        return locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER) || locationManager.isProviderEnabled(
            LocationManager.NETWORK_PROVIDER
        )
    }
}