package com.example.partyapp

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.location.Address
import android.location.Geocoder
import androidx.core.app.ActivityCompat
import androidx.core.content.ContentProviderCompat.requireContext
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import java.util.Locale
import kotlin.math.atan2
import kotlin.math.cos
import kotlin.math.sin
import kotlin.math.sqrt

private lateinit var geocoder: Geocoder
private lateinit var fusedLocationClient: FusedLocationProviderClient
class GeoHelper(private val context: Context) {
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
        return R * c // Distance in kilometers gotta change later
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
}