package com.example.festivviews.adapters

import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.example.festivviews.R
import com.example.festivviews.models.CheckInUserModel
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.storage.FirebaseStorage

class CheckInUserAdapter(private val userList: MutableList<CheckInUserModel>, private val eventID: String): RecyclerView.Adapter<CheckInUserAdapter.ViewHolder>() {

    private val storageRef = FirebaseStorage.getInstance().reference.child("pfps")
    val databaseReference = FirebaseDatabase.getInstance().getReference("EventAttendees")
    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): ViewHolder {
        val context = parent.context
        val inflater = LayoutInflater.from(context)
        val contactView = inflater.inflate(R.layout.check_in_user_item, parent, false)
        return ViewHolder(contactView)
    }

    class ViewHolder(itemView: View): RecyclerView.ViewHolder(itemView)  {
        val userObject: View
        val checkInButton: Button
        val loadingButton: Button
        val checkedButton: Button
        val fullnameTV: TextView
        val ageTV: TextView
        val usernameTV: TextView
        val pfpIV: ImageView
        val verifiedIV: ImageView
        init {
            userObject = itemView.findViewById(R.id.userOjbect)
            checkInButton = itemView.findViewById(R.id.checkInButton)
            loadingButton = itemView.findViewById(R.id.loadingButton)
            checkedButton = itemView.findViewById(R.id.checkedButton)
            fullnameTV = userObject.findViewById(R.id.userFullName)
            ageTV = userObject.findViewById(R.id.age)
            usernameTV = userObject.findViewById(R.id.username)
            pfpIV = userObject.findViewById(R.id.userPicture)
            verifiedIV = userObject.findViewById(R.id.userVerified)
        }

    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val checkInUserListItem = userList[position]
        val userListItem = checkInUserListItem.user!!
        holder.fullnameTV.text = userListItem.firstName + " " + userListItem.lastName
        holder.ageTV.text = userListItem.age.toString()
        holder.usernameTV.text = userListItem.username.toString()
        Glide.with(holder.itemView)
            .load(userListItem.profilePicture?.let { storageRef.child(it) })
            .diskCacheStrategy(DiskCacheStrategy.NONE)
            .skipMemoryCache(true)
            .centerCrop()
            .circleCrop()
            .error(R.drawable.profile_24)
            .into(holder.pfpIV)
        if(userListItem.idVerified != true){
            holder.verifiedIV.visibility = View.GONE
        }else{
            holder.verifiedIV.visibility = View.VISIBLE
        }
        if(checkInUserListItem.checkedIn!!){
            holder.checkInButton.visibility = View.GONE
            holder.loadingButton.visibility = View.GONE
            holder.checkedButton.visibility = View.VISIBLE
        }
        holder.checkInButton.setOnClickListener {
            holder.checkInButton.visibility = View.GONE
            holder.loadingButton.visibility = View.VISIBLE
            val call = databaseReference.child(eventID).child(userListItem.uid!!).setValue(true)
            call.addOnSuccessListener {
                Log.i("UserCheckIn","User ${userListItem.firstName + " " + userListItem.lastName} checked in")
                holder.checkedButton.visibility = View.VISIBLE
                checkInUserListItem.checkedIn = true
            }
        }

    }

    override fun getItemCount(): Int {
        return userList.size
    }
}