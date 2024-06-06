package com.example.partyapp.activities
import android.os.Bundle
import android.util.Log
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.example.partyapp.R
import com.example.partyapp.adapters.StaticImageAdapter
import com.example.partyapp.adapters.TagsAdapter
import com.example.partyapp.models.EstablishmentModel
import com.example.partyapp.models.ImageModel
import com.example.partyapp.models.TagModel
import com.google.android.flexbox.FlexboxLayoutManager
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.firebase.storage.FirebaseStorage

class EstablishmentExtraActivity : AppCompatActivity(), OnMapReadyCallback {
    private var lat: Double = 0.0
    private var long: Double = 0.0
    private var establishmentName: String = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.establishment_extra)
        val establishment = intent.getParcelableExtra<EstablishmentModel>("establishment")!!
        establishmentName = establishment.name!!
        val owner = establishment.ownerAccount!!
        val address = establishment.address!!
        val description = establishment.desc!!
        lat = establishment.lat!!
        long = establishment.long!!
        val tags = establishment.tags ?: emptyList<String>()
        val imagePathsArray = establishment.imgPaths ?: emptyList<String>()
        val imageItems = mutableListOf<ImageModel>()
        for(imagePath in imagePathsArray){
            imageItems.add(ImageModel(null,imagePath))
        }
        val tagModelList = mutableListOf<TagModel>()
        for(tag in tags){
            tagModelList.add(TagModel(text=tag))
            Log.i("Tags","$tag")
        }

        val mapFragment = supportFragmentManager
            .findFragmentById(R.id.mapFragment) as SupportMapFragment
        mapFragment.getMapAsync(this)

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

        val imagesRV = findViewById<RecyclerView>(R.id.imagesRV)
        val imagesAdapter = StaticImageAdapter(imageItems, "establishmentImages")
        val imageLayoutManager = LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false)
        imagesRV.layoutManager = imageLayoutManager
        imagesRV.adapter = imagesAdapter

        val headerImage = findViewById<ImageView>(R.id.estExtraHeaderImage)
        var storageRef = FirebaseStorage.getInstance().reference.child("establishmentImages")
        if(imagePathsArray.isNotEmpty()){
            Glide.with(headerImage)
                .load(storageRef.child(imagePathsArray[0]) )
                .diskCacheStrategy(DiskCacheStrategy.NONE)
                .skipMemoryCache(true)
                .centerCrop()
                .error(R.drawable.baseline_pictures_24)
                .into(headerImage)
        }


    }
    override fun onMapReady(googleMap: GoogleMap) {
        // Add a marker for the establishment and move the camera
        val localLocation = LatLng(lat, long)
        googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(localLocation, 15f))
        googleMap.addMarker(MarkerOptions().position(localLocation).title(establishmentName))
    }

}

