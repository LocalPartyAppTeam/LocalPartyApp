package com.example.partyapp

import android.annotation.SuppressLint
import android.app.DatePickerDialog
import android.app.TimePickerDialog
import android.content.ContentValues
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.location.Location
import android.location.LocationManager
import android.os.Build
import android.os.Bundle
import android.os.Looper
import android.provider.Settings
import android.util.Log
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.ScrollView
import android.widget.TextView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.net.toUri
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
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
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.FirebaseStorage
import java.time.LocalDateTime

private lateinit var auth: FirebaseAuth
@RequiresApi(Build.VERSION_CODES.P)
class AddEstablishmentActivity : AppCompatActivity(), OnMapReadyCallback {

    private var lat = 40.7357
    private var long = -74.172363
    private val MAPVIEW_BUNDLE_KEY = "MapViewBundleKey"
    private var locationManager : LocationManager? = null
    private val PERMISSION_ID = 44
    private val today = LocalDateTime.now()
    private val blank = AddPictureRVEntryModel()
    private val photosArray: MutableList<AddPictureRVEntryModel> = mutableListOf<AddPictureRVEntryModel>(blank)
    private val storageRef = FirebaseStorage.getInstance().reference.child("establishmentImages")
    private lateinit var photosAdapter: EntryPhotoAdapter
    private lateinit var mFusedLocationClient: FusedLocationProviderClient

    val pickPhotos = registerForActivityResult(ActivityResultContracts.PickMultipleVisualMedia(6)) { uris ->
        // Callback is invoked after the user selects a media item or closes the
        // photo picker.
        if (uris.isNotEmpty()) {
            for(image in uris) {
                if(photosAdapter.itemCount <= 6){
                    photosArray.add(photosAdapter.itemCount-1,
                        AddPictureRVEntryModel(
                            image,
                            auth.currentUser!!.email,
                            auth.currentUser!!.email + image.toString()
                        )
                    )
                    photosAdapter.notifyItemInserted(photosAdapter.itemCount - 2)
                    Log.i("recycler", "current entry count ${photosAdapter.itemCount}, added at ${photosAdapter.itemCount - 1}")
                }
            }
            while(photosAdapter.itemCount > 6){
                photosArray.removeAt(6)
                photosAdapter.notifyItemRemoved(6)
                Log.i("recycler", "current entry count ${photosAdapter.itemCount}, removed extra at ${6}")
                /*
                    photosArray.add(blank)
                    Log.i("recycler", "${photosArray[photosAdapter.itemCount-1].imgPath}")
                    photosAdapter.notifyItemInserted(photosAdapter.itemCount-1)
                    Log.i("recycler", "current entry count ${photosAdapter.itemCount}, added blank at ${photosAdapter.itemCount - 1}")
                */}
            Log.d("PhotoPicker", "Selected URI: $uris")
        }else{
            Log.d("PhotoPicker", "None Selected")
        }
    }
    @SuppressLint("ClickableViewAccessibility")
    override fun onCreate(savedInstanceState: Bundle?) {
        auth = Firebase.auth
        val database = Firebase.database
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_establishment)
        val photosRV = findViewById<RecyclerView>(R.id.photosRecyclerView)
        photosAdapter = EntryPhotoAdapter(this,photosArray)
        photosRV.layoutManager = GridLayoutManager(this,3)
        photosRV.adapter = photosAdapter

        mFusedLocationClient = LocationServices.getFusedLocationProviderClient(this)
        getLastLocation()
        val mapF =findViewById<View>(R.id.mapOverlay)
        val mainScrollView = findViewById<ScrollView>(R.id.addEstablishmentScrollView)
        val establishmentDescription = findViewById<EditText>(R.id.EstablishmentDescEntry)
        val establishmentName = findViewById<TextView>(R.id.EstablishmentNameEntry)
        val submitButton = findViewById<Button>(R.id.EstablishmentSubmitButton)
        submitButton.setOnClickListener {
            val myRef = database.getReference("Establishments")
            val name = establishmentName.text.toString()
            val desc = establishmentDescription.text.toString()
            val photoList = mutableListOf<String>()
            for(entry in photosArray){
                if(entry.image.toString() != "null") {
                    photoList.add(entry.image.toString())
                }

            }
            val establishment = EstablishmentModel(auth.currentUser!!.uid, lat, long, name, desc,
                photoList
            )
            myRef.child(auth.currentUser!!.uid).child(name).setValue(establishment).addOnSuccessListener {
                Log.d(ContentValues.TAG, ":D")
            }
            auth.currentUser?.let {
                for(photo in photoList){
                    storageRef.child(photo.toString().replace('/','_')).putFile(photo.toUri()).addOnSuccessListener {
                        Log.d("Update: ", "Uploaded Establishment Photos")
                    }
                }
            }
            startActivity(Intent(this@AddEstablishmentActivity, MainActivity::class.java))
            finish()
        }

        val mapFragment = supportFragmentManager
            .findFragmentById(R.id.mapFragment) as SupportMapFragment
        mapFragment.getMapAsync(this)

        establishmentDescription.setOnTouchListener { v, motionEvent ->
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
    }/*
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
    }*/
    override fun onResume() {
        super.onResume()
        if (checkPermissions()) {
            getLastLocation()
        }
    }
    inner class EntryPhotoAdapter
        (
        private val context: Context,
        private val entries: MutableList<AddPictureRVEntryModel>,
    ): RecyclerView.Adapter<EntryPhotoAdapter.EntryViewHolder>() {
        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): EntryViewHolder {
            val view = LayoutInflater.from(parent.context)
                .inflate(R.layout.rv_listing_image_item, parent, false)
            return EntryViewHolder(view)
        }

        override fun getItemCount() = entries.size

        override fun onBindViewHolder(holder: EntryViewHolder, position: Int) {
            val entry = entries[position]
            /*if(position < itemCount-1) {
                holder.item = entry
                holder.plus.isEnabled = false
                Glide.with(holder.itemView)
                    .load(storageRef.child(entry.imgPath!!))
                    .diskCacheStrategy(DiskCacheStrategy.NONE)
                    .skipMemoryCache(true)
                    .error(R.drawable.profile_24)
                    .into(holder.photo)
            }*/
            if(entry.imgPath == null){
                holder.plus.isEnabled = true
                holder.photo.isEnabled = false
            }else{
                holder.photo.setImageURI(entry.image)
                holder.plus.isEnabled = false
                holder.plus.visibility = View.GONE
                holder.photo.isEnabled = true
            }
            /*
            val pickPhotos = registerForActivityResult(ActivityResultContracts.PickMultipleVisualMedia(6-(entries.size-1))) { uris ->
                // Callback is invoked after the user selects a media item or closes the
                // photo picker.
                if (uris.isNotEmpty()) {
                    entries.removeAt(entries.size-1)
                    this.notifyItemRemoved(entries.size-1)
                    for(image in uris){
                        entries.add(AddPictureRVEntryModel(image, auth.currentUser!!.email,auth.currentUser!!.email+image.toString()))
                        this.notifyItemInserted(entries.size-1)
                    }
                    if(entries.size < 6){
                        entries.add(AddPictureRVEntryModel())
                        this.notifyItemInserted(entries.size-1)
                    }
                    Log.d("PhotoPicker", "Selected URI: $uris")
                }else{
                    Log.d("PhotoPicker", "None Selected")
                }
            }
            */
            holder.plus.setOnClickListener(){
                pickPhotos.launch(PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly))
                Log.i("recycler", "${entries[itemCount-1].imgPath}")
            }
            holder.photo.setOnLongClickListener {
                val pos = holder.adapterPosition
                entries.removeAt(pos)
                this.notifyItemRemoved(pos)
                Log.i("recycler", "current entry count ${itemCount}, removed $position")
                if(itemCount == 5 && entries[4].imgPath != null){
                    entries.add(blank)
                    this.notifyItemInserted(itemCount)
                    Log.i("recycler", "current entry count ${itemCount}, added blank at ${itemCount-1}")
                }
                return@setOnLongClickListener true
            }
        }




        inner class EntryViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView), View.OnClickListener {
            var item: AddPictureRVEntryModel? = null
            val photo: ImageView = itemView.findViewById(R.id.photo)
            val plus: ImageView = itemView.findViewById(R.id.addPhotoIcon)

            init {
                itemView.setOnClickListener(this)
            }

            override fun onClick(p0: View?) {
            }
        }
    }
}