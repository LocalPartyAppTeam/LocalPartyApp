package com.example.festivviews.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.example.festivviews.R
import com.example.festivviews.models.UserModel
import com.google.firebase.storage.FirebaseStorage

class UsersAdapter(private val userList: MutableList<UserModel>, private val owner: Boolean): RecyclerView.Adapter<UsersAdapter.ViewHolder>() {

    private val storageRef = FirebaseStorage.getInstance().reference.child("pfps")
    class ViewHolder(itemView: View): RecyclerView.ViewHolder(itemView) {
        val fullnameTV: TextView
        val ageTV: TextView
        val usernameTV: TextView
        val pfpIV: ImageView
        val verifiedIV: ImageView
    init {
        fullnameTV = itemView.findViewById(R.id.userFullName)
        ageTV = itemView.findViewById(R.id.age)
        usernameTV = itemView.findViewById(R.id.username)
        pfpIV = itemView.findViewById(R.id.userPicture)
        verifiedIV = itemView.findViewById(R.id.userVerified)
    }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val context = parent.context
        val inflater = LayoutInflater.from(context)
        val contactView = inflater.inflate(R.layout.user_item, parent, false)
        return ViewHolder(contactView)
    }

    override fun getItemCount(): Int {
        return userList.size
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val userListItem = userList[position]
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
        if(owner){
            holder.ageTV.visibility = View.VISIBLE
        }else{
            holder.ageTV.visibility = View.GONE
            holder.verifiedIV.visibility = View.GONE
        }
    }
}