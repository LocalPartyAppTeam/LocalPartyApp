package com.example.partyapp.activities

import android.os.Bundle
import android.util.Log
import android.widget.EditText
import android.widget.ImageView
import androidx.activity.result.ActivityResultLauncher
import androidx.appcompat.app.AppCompatActivity
import androidx.core.widget.doOnTextChanged
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.partyapp.CaptureAct
import com.example.partyapp.R
import com.example.partyapp.adapters.CheckInUserAdapter
import com.example.partyapp.models.CheckInUserModel
import com.example.partyapp.models.UserModel
import com.google.firebase.Firebase
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.database
import com.journeyapps.barcodescanner.ScanContract
import com.journeyapps.barcodescanner.ScanOptions

class CheckInActivity : AppCompatActivity() {
    private lateinit var userList: MutableList<CheckInUserModel>
    private lateinit var userListPool: MutableList<CheckInUserModel>
    private lateinit var eventID: String
    private lateinit var usersAdapter: CheckInUserAdapter
    private lateinit var nameFilter: EditText
    var filterString = ""
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_check_in)
        userList = mutableListOf()
        userListPool = mutableListOf()
        eventID = intent.getStringExtra("pushId")!!
        val userRV =  findViewById<RecyclerView>(R.id.userList)
        nameFilter = findViewById<EditText>(R.id.nameEntry)
        val barcodeScanButton = findViewById<ImageView>(R.id.barcodeImage)
        usersAdapter = CheckInUserAdapter(userList, eventID)
        val usersLayoutManager = LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false)
        userRV.adapter = usersAdapter
        userRV.layoutManager = usersLayoutManager
        val databaseRef = Firebase.database.getReference("EventAttendees")
        val usersRef = Firebase.database.getReference("Users")
        val eventAttendeesCall = databaseRef.child(eventID).get()
        eventAttendeesCall.addOnSuccessListener { attendeesSnapshot ->
            for(uidSnapshot in attendeesSnapshot.children){
                val uid = uidSnapshot.key
                val checked = uidSnapshot.getValue(Boolean::class.java)
                val userCall = usersRef.child(uid!!).get()
                userCall.addOnSuccessListener{userSnapshot->
                    val user = CheckInUserModel(userSnapshot.getValue(UserModel::class.java)!!, checked!!)
                    userList.add(user)
                    usersAdapter.notifyDataSetChanged()
                }
            }
        }
        barcodeScanButton.setOnClickListener {
            scanCode()
        }
        nameFilter.doOnTextChanged { text, _, _, _ ->
            filterString = (text ?: "").toString()
            for(userCheck in userListPool) {
                val user = userCheck.user!!
                if ((user.firstName!! + user.lastName!!).contains(filterString) or user.username!!.contains(filterString)
                ) {
                    userList.add(userCheck)
                    userListPool.remove(userCheck)
                }
            }
            for(userCheck in userList) {
                val user = userCheck.user!!
                if (!((user.firstName!! + user.lastName!!).contains(filterString) or user.username!!.contains(filterString)
                )) {
                    userListPool.add(userCheck)
                    userList.remove(userCheck)
                }
            }
            usersAdapter.notifyDataSetChanged()
        }
    }

    private fun scanCode() {
        val options: ScanOptions = ScanOptions()
        options.setPrompt("Volume up to flash on")
        options.setBeepEnabled(true)
        options.setOrientationLocked(true)
        options.setCaptureActivity(CaptureAct::class.java)
        barLauncher.launch(options)
    }
    val barLauncher: ActivityResultLauncher<ScanOptions> = registerForActivityResult(ScanContract()) { result ->
        if (result.contents != null) {
            var i = 0
            for(user in userList){
                if(user.user!!.uid == result.contents){
                    val databaseReference = FirebaseDatabase.getInstance().getReference("EventAttendees")
                    val call = databaseReference.child(eventID).child(user.user!!.uid!!).setValue(true)
                    call.addOnSuccessListener {
                        Log.i("UserCheckIn","User ${user.user!!.firstName + " " + user.user!!.lastName} checked in")
                        userList[i].checkedIn = true
                        usersAdapter.notifyItemChanged(i)
                    }
                }
                i+=1
            }
            i = 0
            for(user in userListPool){
                if(user.user!!.uid == result.contents){
                    val databaseReference = FirebaseDatabase.getInstance().getReference("EventAttendees")
                    val call = databaseReference.child(eventID).child(user.user!!.uid!!).setValue(true)
                    call.addOnSuccessListener {
                        Log.i("UserCheckIn","User ${user.user!!.firstName + " " + user.user!!.lastName} checked in")
                        userListPool[i].checkedIn = true
                    }
                }
                i+=1
            }
        }
    }
}
