package com.example.festivviews.adapters

import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.view.menu.MenuView.ItemView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.example.festivviews.R
import com.example.festivviews.adapters.CheckInUserAdapter.ViewHolder
import com.example.festivviews.models.CheckInUserModel
import com.example.festivviews.models.InviteModel
import com.example.festivviews.models.InviteUserModel
import com.example.festivviews.models.UserModel
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.storage.FirebaseStorage

class InviteUserAdapter( val userList: MutableList<UserModel>, private val eventID: String, private val auth: FirebaseAuth, private val attendees: MutableList<String>, private val invitedUsers: MutableList<String>): RecyclerView.Adapter<InviteUserAdapter.ViewHolder>() {
    private val storageRef = FirebaseStorage.getInstance().reference.child("pfps")
    private val databaseReference = FirebaseDatabase.getInstance().getReference("Invites")
    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): InviteUserAdapter.ViewHolder {
        val context = parent.context
        val inflater = LayoutInflater.from(context)
        val contactView = inflater.inflate(R.layout.invite_user_item, parent, false)
        return ViewHolder(contactView)
    }

    inner class ViewHolder(itemView: View): RecyclerView.ViewHolder(itemView) {
        val userObject: View
        val inviteButton: Button
        val loadingButton: Button
        val invitedButton: Button
        val goingButton: Button
        val fullnameTV: TextView
        val ageTV: TextView
        val usernameTV: TextView
        val pfpIV: ImageView
        val verifiedIV: ImageView
        init {
            userObject = itemView.findViewById(R.id.userOjbect)
            inviteButton = itemView.findViewById(R.id.inviteButton)
            loadingButton = itemView.findViewById(R.id.loadingButton)
            invitedButton = itemView.findViewById(R.id.invitedButton)
            goingButton = itemView.findViewById(R.id.goingButton)
            fullnameTV = userObject.findViewById(R.id.userFullName)
            ageTV = userObject.findViewById(R.id.age)
            usernameTV = userObject.findViewById(R.id.username)
            pfpIV = userObject.findViewById(R.id.userPicture)
            verifiedIV = userObject.findViewById(R.id.userVerified)

            /*val userListItem = userList[adapterPosition]
            inviteButton.setOnClickListener {
                val invite = InviteModel()
                inviteButton.visibility = View.GONE
                loadingButton.visibility = View.VISIBLE
                invite.from_uid = auth.currentUser?.uid
                invite.to_uid = userListItem.uid
                invite.event_id = eventID
                invite.pushId = invite.from_uid + "-" + invite.to_uid + "-" + invite.event_id
                invite.accepted = false
                databaseReference.child("to")
                    .child(invite.to_uid!!)
                    .child(invite.pushId!!)
                    .setValue(invite).addOnSuccessListener {
                        Log.i("UserCheckIn","User ${userListItem.firstName + " " + userListItem.lastName} checked in")
                        inviteButton.visibility = View.GONE
                        loadingButton.visibility = View.GONE
                        invitedButton.visibility = View.VISIBLE
                        goingButton.visibility = View.GONE
                    }
                databaseReference.child("from")
                    .child(invite.from_uid!!)
                    .child(invite.pushId!!)
                    .setValue(invite).addOnSuccessListener {
                        Log.i("UserCheckIn","User ${userListItem.firstName + " " + userListItem.lastName} checked in")
                        inviteButton.visibility = View.GONE
                        loadingButton.visibility = View.GONE
                        invitedButton.visibility = View.VISIBLE
                        goingButton.visibility = View.GONE
                    }
            }*/
        }
    }

    override fun onBindViewHolder(holder: InviteUserAdapter.ViewHolder, position: Int) {
        val userListItem = userList[position]!!
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
        }
        else{
            holder.verifiedIV.visibility = View.VISIBLE
        }
        if(invitedUsers.contains(userListItem.uid) and (!attendees.contains(userListItem.uid))){
            holder.inviteButton.visibility = View.GONE
            holder.loadingButton.visibility = View.GONE
            holder.invitedButton.visibility = View.VISIBLE
            holder.goingButton.visibility = View.GONE
        }
        if(attendees.contains(userListItem.uid)){
            holder.inviteButton.visibility = View.GONE
            holder.loadingButton.visibility = View.GONE
            holder.invitedButton.visibility = View.GONE
            holder.goingButton.visibility = View.VISIBLE
        }
        holder.inviteButton.setOnClickListener {
            val invite = InviteModel()
            holder.inviteButton.visibility = View.GONE
            holder.loadingButton.visibility = View.VISIBLE
            invite.from_uid = auth.currentUser?.uid
            invite.to_uid = userListItem.uid
            invite.event_id = eventID
            invite.pushId = invite.from_uid + "-" + invite.to_uid + "-" + invite.event_id
            invite.accepted = false
            databaseReference.child("to")
                .child(invite.to_uid!!)
                .child(invite.pushId!!)
                .setValue(invite).addOnSuccessListener {
                    Log.i("UserCheckIn","User ${userListItem.firstName + " " + userListItem.lastName} checked in")
                    holder.inviteButton.visibility = View.GONE
                    holder.loadingButton.visibility = View.GONE
                    holder.invitedButton.visibility = View.VISIBLE
                    holder.goingButton.visibility = View.GONE
                }
            databaseReference.child("from")
                .child(invite.from_uid!!)
                .child(invite.pushId!!)
                .setValue(invite).addOnSuccessListener {
                    Log.i("UserCheckIn","User ${userListItem.firstName + " " + userListItem.lastName} checked in")
                    holder.inviteButton.visibility = View.GONE
                    holder.loadingButton.visibility = View.GONE
                    holder.invitedButton.visibility = View.VISIBLE
                    holder.goingButton.visibility = View.GONE
                }
        }
    }

    override fun getItemCount(): Int {
        return userList.size
    }
}