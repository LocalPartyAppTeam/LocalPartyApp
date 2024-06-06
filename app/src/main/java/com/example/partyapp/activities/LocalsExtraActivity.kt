package com.example.partyapp.activities

import android.content.Intent
import android.graphics.Bitmap
import android.graphics.Color
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.example.partyapp.GeoHelper
import com.example.partyapp.R
import com.example.partyapp.adapters.MiniEstAdapter
import com.example.partyapp.adapters.StaticImageAdapter
import com.example.partyapp.adapters.TagsAdapter
import com.example.partyapp.models.EstablishmentModel
import com.example.partyapp.models.EventModel
import com.example.partyapp.models.ImageModel
import com.example.partyapp.models.TagModel
import com.example.partyapp.models.UserModel
import com.google.android.flexbox.FlexboxLayoutManager
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.material.card.MaterialCardView
import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.auth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.google.firebase.storage.FirebaseStorage
import com.google.zxing.BarcodeFormat
import com.google.zxing.qrcode.QRCodeWriter
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

@RequiresApi(Build.VERSION_CODES.O)
class LocalsExtraActivity : AppCompatActivity(), OnMapReadyCallback {
    private lateinit var auth: FirebaseAuth
    private var lat: Double = 0.0
    private var long: Double = 0.0
    private var eventName: String = ""
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.locals_extra)
        auth = Firebase.auth
        val database = FirebaseDatabase.getInstance()
        val event = intent.getParcelableExtra<EventModel>("event")!!
        eventName = event.name!!
        val host = event.host!!
        val address = event.address!!
        val start = LocalDateTime.parse(event.start!!).format(DateTimeFormatter.ofPattern("dd MMMM yyyy HH:mma"))
        val end = LocalDateTime.parse(event.end!!).format(DateTimeFormatter.ofPattern("dd MMMM yyyy HH:mma"))
        val desc = event.desc!!
        val userID = auth.currentUser!!.uid
        lat = event.lat!!
        long = event.long!!
        val imagePathsArray = event.imgPaths ?: emptyList<String>()
        val imageItems = mutableListOf<ImageModel>()
        for(imagePath in imagePathsArray){
            imageItems.add(ImageModel(null,imagePath))
        }
        val tags = event.tags ?: emptyList<String>()
        val sanTags = event.sanitizedTags ?: emptyList<String>()
        val tagModelList = mutableListOf<TagModel>()
        for(tag in tags){
            tagModelList.add(TagModel(text=tag))
            Log.i("Tags",tag)
        }
        val nearbyMiniList = mutableListOf<EstablishmentModel>()
        val geoHelper = GeoHelper(this)
        val eventNameTextView = findViewById<TextView>(R.id.eventNameTextView)
//        val hostNameTextView = findViewById<TextView>(R.id.hostNameTextView)
        val startTimeTextView = findViewById<TextView>(R.id.startTimeTextView)
        val endTimeTextView = findViewById<TextView>(R.id.endTimeTextView)
        val addressTextView = findViewById<TextView>(R.id.addressTextView)
        val addressTextView2 = findViewById<TextView>(R.id.addressMapsTextView)
//        val distanceTextView = findViewById<TextView>(R.id.distanceTextView)
//        val dayOfWeekTextView = findViewById<TextView>(R.id.dayOfWeekTextView)
//        val dayOfMonthTextView = findViewById<TextView>(R.id.dayOfMonthTextView)
        val descriptionTextView = findViewById<TextView>(R.id.descriptionTextView)
        val whosGoingButton = findViewById<Button>(R.id.see_who_is_going)
        val qrMaskTV = findViewById<TextView>(R.id.qr_Mask)
        val qrImage = findViewById<ImageView>(R.id.qr_code_image)
        val qrCard = findViewById<MaterialCardView>(R.id.qr_card)
        val qrFullname = findViewById<TextView>(R.id.qr_full_name)
        val qrUsername = findViewById<TextView>(R.id.qr_username)
        val viewQrCardButton = findViewById<Button>(R.id.myQrButton)
        val qrCardBackButton = findViewById<ImageButton>(R.id.qrCardBackButton)

        val qrnamefill = database.getReference("Users").child(auth.currentUser!!.uid).get()
        qrnamefill.addOnSuccessListener {
            val user = it.getValue(UserModel::class.java)
            qrFullname.text = user!!.firstName + " " + user.lastName
            qrUsername.text = user!!.username
        }

        fun genBarcode() {
            val inputValue = auth.currentUser!!.uid
            if (inputValue.isNotEmpty()) {
                val mwriter = QRCodeWriter()
                try {
                    val matrix = mwriter.encode(inputValue, BarcodeFormat.QR_CODE, 300, 300)
                    val bitmap = Bitmap.createBitmap(300, 300, Bitmap.Config.RGB_565)
                    for (i in 0 until 300) {
                        for (j in 0 until 300) {
                            bitmap.setPixel(i, j, if (matrix[i, j]) Color.BLACK else Color.WHITE)
                        }
                    }
                    qrImage.setImageBitmap(bitmap)
                } catch (e: Exception) {
                    Toast.makeText(this, "Exception $e", Toast.LENGTH_SHORT).show()
                    Log.i( "barcode","Exception $e")
                }
            }
        }
        qrMaskTV.setOnClickListener {

        }
        viewQrCardButton.setOnClickListener{
            qrMaskTV.visibility = View.VISIBLE
            qrCard.visibility = View.VISIBLE
            genBarcode()
        }
        qrCardBackButton.setOnClickListener{
            qrMaskTV.visibility = View.GONE
            qrCard.visibility = View.GONE
        }
        whosGoingButton.setOnClickListener {
            val intent = Intent(this, SeeAttendeesActivity::class.java).apply {
                putExtra("pushId",event.pushId)
                putExtra("owner", event.host == auth.currentUser!!.uid)
            }
            this.startActivity(intent)
        }
        val joinButton = findViewById<Button>(R.id.join_event)
        val reveiewButton = findViewById<Button>(R.id.reviewButton)
        joinButton.setOnClickListener {
            event?.pushId?.let { it1 ->
                database.getReference("UsersAttending")
                    .child(auth.currentUser!!.uid).child(
                        it1
                    )
            }?.setValue(event)
            event?.pushId?.let { it1 ->
                database.getReference("EventAttendees")
                    .child(it1).child(
                        auth.currentUser!!.uid
                    )
            }?.setValue(false)
            joinButton.text = "EVENT JOINED!"
            reveiewButton.visibility = View.VISIBLE
            joinButton.visibility = View.GONE
            viewQrCardButton.visibility = View.VISIBLE
        }
        val eventAttendeesCall = database.getReference("EventAttendees").child(event.pushId!!).get()
        eventAttendeesCall.addOnSuccessListener { attendeesSnapshot ->
            for(uidSnapshot in attendeesSnapshot.children){
                val uid = uidSnapshot.key
                if( uid == userID){
                    joinButton.visibility = View.GONE
                    viewQrCardButton.visibility = View.VISIBLE
                }
            }
        }

        reveiewButton.setOnClickListener {
            var eventImage = "empty"
            if (imagePathsArray.isNotEmpty()) eventImage = imagePathsArray[0]

            val intent = Intent(this, AddReviewActivity::class.java).apply {
                putExtra("uid", auth.currentUser!!.uid)
                putExtra("eid", event.pushId)
                putExtra("eventName", event.name)
                putExtra("eventImage", eventImage)
            }
            this.startActivity(intent)
            reveiewButton.text = "EDIT REVIEW"
        }

        eventNameTextView.text = eventName
//        hostNameTextView.text = host
        addressTextView.text = address
        addressTextView2.text = address
        startTimeTextView.text = start
        endTimeTextView.text = end
//        distanceTextView.text = distance
//        dayOfWeekTextView.text = dayOfWeek
//        dayOfMonthTextView.text = dayOfMonth
        descriptionTextView.text = desc

        val tagsRV = findViewById<RecyclerView>(R.id.localEventTagsRV)
        val tagsAdapter = TagsAdapter(false, tagModelList)
        tagsRV.layoutManager = FlexboxLayoutManager(this)
        tagsRV.adapter = tagsAdapter

        val nearbyRV = findViewById<RecyclerView>(R.id.nearbyEstablishmentRV)
        val miniEstAdapter = MiniEstAdapter(nearbyMiniList, this, arrayOf<Double>(lat,long))
        nearbyRV.layoutManager = LinearLayoutManager(this, RecyclerView.HORIZONTAL,false)
        nearbyRV.adapter = miniEstAdapter

        val databaseReference = database.getReference("TaggedEstablishments")
        databaseReference.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                for (uniqueIDSnapshot in dataSnapshot.children) {
                    val uniqueID = uniqueIDSnapshot.key
                    uniqueID?.let {
                        for (establishmentSnapshot in uniqueIDSnapshot.children) {
                            val establishmentName = establishmentSnapshot.key
                            establishmentName?.let {
                                val establishmentM = establishmentSnapshot.getValue(
                                    EstablishmentModel::class.java)!!
                                nearbyMiniList.add(establishmentM)

                            }
                        }
                    }
                }
                nearbyMiniList.sortBy { geoHelper.calculateDistance(it.lat!!,it.long!!,lat,long) }
                miniEstAdapter.notifyDataSetChanged()
            }

            override fun onCancelled(databaseError: DatabaseError) {
                Toast.makeText(applicationContext,"error", Toast.LENGTH_LONG).show()
            }
        })



        val mapFragment = supportFragmentManager
            .findFragmentById(R.id.mapFragment) as SupportMapFragment
        mapFragment.getMapAsync(this)

//        Toast.makeText(this,imagePathsArray[0].toString(),Toast.LENGTH_LONG).show()

        val imagesRV = findViewById<RecyclerView>(R.id.imagesRV)
        val imagesAdapter = StaticImageAdapter(imageItems, "eventImages")
        val imageLayoutManager = LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false)
        imagesRV.layoutManager = imageLayoutManager
        imagesRV.adapter = imagesAdapter

        val headerImage = findViewById<ImageView>(R.id.headerImage)
        var storageRef = FirebaseStorage.getInstance().reference.child("eventImages")
        if(imagePathsArray.isNotEmpty()){
            Glide.with(headerImage)
                .load(storageRef.child(imagePathsArray[0]) )
                .diskCacheStrategy(DiskCacheStrategy.NONE)
                .skipMemoryCache(true)
                .centerCrop()
                .error(R.drawable.baseline_pictures_24)
                .into(headerImage)
        }

        val concatenatedPaths = imagePathsArray.take(6).joinToString("\n")
//        val imagePathsTextView = findViewById<TextView>(R.id.imagePathsTextView)
//        imagePathsTextView.text = concatenatedPaths
//        if (imagePathsArray.size > 6) {
//            Toast.makeText(this, "Only the first 6 image paths are displayed", Toast.LENGTH_SHORT).show()
//        }

//        Toast.makeText(this,eventName,Toast.LENGTH_LONG).show()


    }

    override fun onMapReady(googleMap: GoogleMap) {
        val localLocation = LatLng(lat, long)
        googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(localLocation, 15f))
        googleMap.addMarker(MarkerOptions().position(localLocation).title(eventName))
    }


}
