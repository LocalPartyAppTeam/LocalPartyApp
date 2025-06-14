package com.example.festivviews.activities

import android.annotation.SuppressLint
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
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.festivviews.GeoHelper
import com.example.festivviews.R
import com.example.festivviews.adapters.TagsAdapter
import com.example.festivviews.models.EstablishmentModel
import com.example.festivviews.models.ImageModel
import com.example.festivviews.models.TagModel
import com.google.android.flexbox.FlexboxLayoutManager
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

private var auth: FirebaseAuth = Firebase.auth
class AddEstablishmentActivity : AppCompatActivity(), OnMapReadyCallback {

    private var lat = 40.7357
    private var long = -74.172363
    private val MAPVIEW_BUNDLE_KEY = "MapViewBundleKey"
    private var locationManager : LocationManager? = null
    private val PERMISSION_ID = 44
    @RequiresApi(Build.VERSION_CODES.O)
    private val today = LocalDateTime.now()
    private val blank = ImageModel()
    private val photosArray: MutableList<ImageModel> = mutableListOf<ImageModel>(blank)
    private val tagsArray: MutableList<TagModel> = mutableListOf<TagModel>()
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
                        ImageModel(
                            image,
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

        val tagEntry = findViewById<EditText>(R.id.EstTagEntry)
        val addTagButton = findViewById<Button>(R.id.EstAddTagButton)
        val tagsRV = findViewById<RecyclerView>(R.id.estTagsRecyclerView)
        val tagsAdapter = TagsAdapter(true, tagsArray)
        tagsRV.layoutManager = FlexboxLayoutManager(this)
        tagsRV.adapter = tagsAdapter

        addTagButton.setOnClickListener{
            if(tagEntry.text.toString() != ""){
                val tag = TagModel(text = tagEntry.text.toString())
                tagsArray.add(tag)
                tagsAdapter.notifyItemInserted(tagsAdapter.itemCount-1)
                tagEntry.setText("")
            }
        }
        mFusedLocationClient = LocationServices.getFusedLocationProviderClient(this)
        val mapF =findViewById<View>(R.id.mapOverlay)
        val mainScrollView = findViewById<ScrollView>(R.id.addEstablishmentScrollView)
        val establishmentDescription = findViewById<EditText>(R.id.EstablishmentDescEntry)
        val establishmentName = findViewById<TextView>(R.id.EstablishmentNameEntry)
        val submitButton = findViewById<Button>(R.id.EstablishmentSubmitButton)
        submitButton.setOnClickListener {
            val myRef = database.getReference("TaggedEstablishments")
            val pushRef = myRef.child(auth.currentUser!!.uid).push()
            val name = establishmentName.text.toString()
            val desc = establishmentDescription.text.toString()
            val photoList = mutableListOf<String>()
            val tagsList = mutableListOf<String>()
            val sanTagsList = mutableListOf<String>()
            val geoHelper = GeoHelper(this)
            val address = geoHelper.getAddress(lat,long)
            auth.currentUser?.let {
                var i = 0
                for(photo in photosArray){
                    photosArray[i].image?.let { it1 ->
                        photoList.add(pushRef.key!!+ i.toString())
                        storageRef.child(photoList[i]).putFile(it1).addOnSuccessListener {
                            Log.d("Update: ", "Uploaded Event Photos")
                        }
                    }
                    i+=1
                }
            }
            for(entry in tagsArray){
                var tagText = entry.text!!
                tagsList.add(tagText)
                tagText = tagText.lowercase()
                val re = Regex("[^A-Za-z0-9 ]")
                tagText = re.replace(tagText, "")
                sanTagsList.add(tagText)
            }
            val establishment = EstablishmentModel(pushRef.key, auth.currentUser!!.uid, lat, long, name, desc, address,
                photoList,
                tagsList,
                sanTagsList
            )
            pushRef.setValue(establishment).addOnSuccessListener {
                Log.d(ContentValues.TAG, ":D")
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
        getLastLocation(googleMap)
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
            markerOptions.title("Establishment Here")
            markerOptions.snippet("Snippet")
            markerOptions.visible(true)
            googleMap.addMarker(
                markerOptions
            )
            lat = it.latitude
            long = it.longitude
        }
    }
    @SuppressLint("MissingPermission")
    private fun getLastLocation(googleMap: GoogleMap){
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
                        googleMap.clear()
                        val loca =  LatLng(location.getLatitude(), location.getLongitude())
                        googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(loca, 12F))
                        googleMap.addMarker(
                            MarkerOptions()
                                .position(loca)
                                .title("Newark")
                        )
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
                getLastLocation(googleMap)
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
            lat = mLastLocation!!.latitude
            long = mLastLocation.longitude
        }
    }
    private fun checkPermissions(): Boolean
    {
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
    override fun onResume() {
        super.onResume()
        if (checkPermissions()) {
        }
    }
    inner class EntryPhotoAdapter
        (
        private val context: Context,
        private val entries: MutableList<ImageModel>,
    ): RecyclerView.Adapter<EntryPhotoAdapter.EntryViewHolder>() {
        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): EntryViewHolder {
            val view = LayoutInflater.from(parent.context)
                .inflate(R.layout.add_image_item, parent, false)
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
            var item: ImageModel? = null
            val photo: ImageView = itemView.findViewById(R.id.addedPhoto)
            val plus: ImageView = itemView.findViewById(R.id.addPhotoIcon)

            init {
                itemView.setOnClickListener(this)
            }

            override fun onClick(p0: View?) {
            }
        }
    }
}