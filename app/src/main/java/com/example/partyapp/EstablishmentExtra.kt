package com.example.partyapp
import android.os.Bundle
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity

class EstablishmentExtra : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.establishment_extra)

        val establishmentName = intent.getStringExtra("establishmentName")
        val owner = intent.getStringExtra("owner")
        val address = intent.getStringExtra("address")
        val description = intent.getStringExtra("description")
        val distance = intent.getStringExtra("distance")

        val establishmentNameTextView = findViewById<TextView>(R.id.textView_establishmentName)
        val ownerTextView = findViewById<TextView>(R.id.textView_owner)
        val addressTextView = findViewById<TextView>(R.id.textView_address)
        val descriptionTextView = findViewById<TextView>(R.id.textView_description)
        val distanceTextView = findViewById<TextView>(R.id.textView_distance)

        establishmentNameTextView.text = "Establishment Name: $establishmentName"
        ownerTextView.text = "Owner: $owner"
        addressTextView.text = "Address: $address"
        descriptionTextView.text = "Description: $description"
        distanceTextView.text = "Distance: $distance"

    }
}
