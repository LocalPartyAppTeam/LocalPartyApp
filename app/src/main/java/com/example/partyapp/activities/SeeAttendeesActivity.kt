package com.example.partyapp.activities

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.partyapp.R
import com.example.partyapp.adapters.UsersAdapter
import com.example.partyapp.models.UserModel
import com.google.firebase.Firebase
import com.google.firebase.database.database
class SeeAttendeesActivity : AppCompatActivity() {
    private lateinit var userList: MutableList<UserModel>
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_see_attendees)
        userList = mutableListOf()
        val eventID = intent.getStringExtra("pushId")!!
        val owner = intent.getBooleanExtra("owner", false)
        val userRV =  findViewById<RecyclerView>(R.id.userList)
        val usersAdapter = UsersAdapter(userList, owner)
        val usersLayoutManager = LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false)
        userRV.adapter = usersAdapter
        userRV.layoutManager = usersLayoutManager
        val databaseRef = Firebase.database.getReference("EventAttendees")
        val usersRef = Firebase.database.getReference("Users")
        val eventAttendeesCall = databaseRef.child(eventID).get()
        eventAttendeesCall.addOnSuccessListener { attendeesSnapshot ->
            for(uidSnapshot in attendeesSnapshot.children){
                val uid = uidSnapshot.key
                val userCall = usersRef.child(uid!!).get()
                userCall.addOnSuccessListener{userSnapshot->
                    userSnapshot.getValue(UserModel::class.java)?.let { userList.add(it) }
                    usersAdapter.notifyDataSetChanged()
                }
            }
        }
    }
}