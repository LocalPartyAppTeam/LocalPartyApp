package com.example.festivviews.activities

import android.os.Bundle
import android.widget.Button
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.bumptech.glide.Glide
import com.example.festivviews.R
import com.example.festivviews.databinding.ActivityAddReviewBinding
import com.google.firebase.Firebase
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.database
import com.google.firebase.storage.FirebaseStorage

class AddReviewActivity : AppCompatActivity() {
    private lateinit var binding: ActivityAddReviewBinding
    private lateinit var database: DatabaseReference

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAddReviewBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val eid = intent.getStringExtra("eid")
        val uid = intent.getStringExtra("uid")
        val eventName = intent.getStringExtra("eventName")
        val eventImage = intent.getStringExtra("eventImage")

        val storageRef = FirebaseStorage.getInstance().reference.child("eventImages")
        if (!eventImage.equals("empty")) {
            Glide.with(binding.eventImage)
                .load(eventImage?.let { storageRef.child(it) })
                .centerCrop()
                .error(R.drawable.baseline_pictures_24)
                .into(binding.eventImage)
        }
        binding.eventName.text = eventName

        database = Firebase.database.reference
        val submitButton: Button = binding.submitButton
        submitButton.setOnClickListener {
            val detailSafetyRating = binding.detailSafetyRating.text.toString()
            val detailEventRating = binding.detailEventRating.text.toString()
            val safetyRating = binding.safetyRatingBar.rating
            val eventRating = binding.eventRatingBar.rating

            if (uid != null && eid != null) {
                writeReview(uid, eid, safetyRating, eventRating, detailEventRating, detailSafetyRating)
                Toast.makeText(
                    this,
                    "Thank you for your feedback.",
                    Toast.LENGTH_SHORT,
                ).show()
                finish()
            } else {
                Toast.makeText(
                    this,
                    "Something went wrong. Please try again.",
                    Toast.LENGTH_SHORT,
                ).show()
            }
        }
    }

    private fun writeReview(uid: String, eid: String, safetyRating: Float, eventRating: Float,
                            detailEventRating: String, detailSafetyRating: String) {
        database.child("UserReviews").child(eid).child("uid").setValue(uid)
        database.child("UserReviews").child(eid).child("safetyRating").setValue(safetyRating)
        database.child("UserReviews").child(eid).child("eventRating").setValue(eventRating)
        database.child("UserReviews").child(eid).child("eventDetail").setValue(detailEventRating)
        database.child("UserReviews").child(eid).child("safetyDetail").setValue(detailSafetyRating)
    }
}