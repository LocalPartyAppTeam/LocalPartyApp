package com.example.festivviews.activities

import android.os.Bundle
import android.util.Log
import android.widget.EditText
import android.widget.ImageView
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.ActivityResultLauncher
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.widget.doOnTextChanged
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.festivviews.CaptureAct
import com.example.festivviews.R
import com.example.festivviews.adapters.CheckInUserAdapter
import com.example.festivviews.adapters.InviteUserAdapter
import com.example.festivviews.models.CheckInUserModel
import com.example.festivviews.models.InviteModel
import com.example.festivviews.models.InviteUserModel
import com.example.festivviews.models.UserModel
import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.auth
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.database
import com.journeyapps.barcodescanner.ScanContract
import com.journeyapps.barcodescanner.ScanOptions

class InviteUsersActivity : AppCompatActivity() {
    private lateinit var userList: MutableList<UserModel>
    private lateinit var userListPool: MutableList<UserModel>
    private lateinit var attendees: MutableList<String>
    private lateinit var previouslyInvited: MutableList<String>
    private lateinit var eventID: String
    private lateinit var usersAdapter: InviteUserAdapter
    private lateinit var nameFilter: EditText
    var filterString = ""
    private lateinit var auth: FirebaseAuth
    override fun onCreate(savedInstanceState: Bundle?) {
        auth = Firebase.auth
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_invite)
        userList = mutableListOf()
        userListPool = mutableListOf()
        previouslyInvited = mutableListOf<String>()
        attendees = mutableListOf<String>()
        eventID = intent.getStringExtra("pushId")!!
        val userRV =  findViewById<RecyclerView>(R.id.userList)
        nameFilter = findViewById<EditText>(R.id.nameEntry)
        usersAdapter = InviteUserAdapter(userList,eventID, auth, attendees, previouslyInvited)
        val usersLayoutManager = LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false)
        userRV.adapter = usersAdapter
        userRV.layoutManager = usersLayoutManager

        val invitesRef = Firebase.database.getReference("Invites")
        val relevantInvitesCall = invitesRef.child("from").child(auth.currentUser!!.uid).get()
        relevantInvitesCall.addOnSuccessListener { invitesSnapshot ->
            for(inviteSnapshot in invitesSnapshot.children){
                val invite = inviteSnapshot.getValue(InviteModel::class.java)
                if(invite!!.event_id == eventID){
                    previouslyInvited.add(invite.to_uid!!)
                    usersAdapter.notifyDataSetChanged()
                }
            }
        }

        val eventAttendeesRef = Firebase.database.getReference("EventAttendees")
        val usersRef = Firebase.database.getReference("Users")
        val eventAttendeesCall = eventAttendeesRef.child(eventID).get()
        eventAttendeesCall.addOnSuccessListener { attendeesSnapshot ->
            for(uidSnapshot in attendeesSnapshot.children){
                val uid = uidSnapshot.key
                val userCall = usersRef.child(uid!!).get()
                userCall.addOnSuccessListener{userSnapshot->
                    val user = userSnapshot.key
                    attendees.add(user!!)
                    usersAdapter.notifyDataSetChanged()
                }
            }
        }

        val usersCall = usersRef.get()
        usersCall.addOnSuccessListener { attendeesSnapshot ->
            for(uidSnapshot in attendeesSnapshot.children){
                val uid = uidSnapshot.key
                val userCall = usersRef.child(uid!!).get()
                userCall.addOnSuccessListener{userSnapshot->
                    val user = userSnapshot.getValue(UserModel::class.java)!!
                    userList.add(user)
                    userListPool.add(user)
                    usersAdapter.notifyDataSetChanged()
                }
            }
        }
        nameFilter.doOnTextChanged { text, _, _, _ ->
            filterString = (text ?: "").toString().lowercase().trim()
            userList.clear()
            for(user in userListPool) {
                val s1 = (user.firstName!! + user.lastName!!).lowercase()
                val s2 = user.username!!.lowercase()
                if (s1.contains(filterString) or s2.contains(filterString)
                ) {
                    userList.add(user)
                }
                Log.i("nameFilter", s1+ " " + s2 + " " + userList.size.toString())
            }
            usersAdapter.notifyDataSetChanged()
            Log.i("nameFilter", userList.size.toString() + " ")
        }
    }
}