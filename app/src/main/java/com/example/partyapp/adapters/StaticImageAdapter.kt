package com.example.partyapp.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.example.partyapp.R
import com.example.partyapp.models.ImageModel
import com.google.firebase.storage.FirebaseStorage


class StaticImageAdapter(private val imageList: MutableList<ImageModel>, private val storageFolder: String): RecyclerView.Adapter<StaticImageAdapter.ViewHolder>() {

    private val storageRef = FirebaseStorage.getInstance().reference.child(storageFolder)
    class ViewHolder(itemView: View): RecyclerView.ViewHolder(itemView) {
    val imageView: ImageView
        init {
            imageView = itemView.findViewById(R.id.large_image)
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val context = parent.context
        val inflater = LayoutInflater.from(context)
        val contactView = inflater.inflate(R.layout.listing_large_static_image_item, parent, false)
        return ViewHolder(contactView)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val imageItem = imageList[position]
        if(imageItem.image == null) {
            Glide.with(holder.itemView)
                .load(imageItem.imgPath?.let { storageRef.child(it) })
                .diskCacheStrategy(DiskCacheStrategy.NONE)
                .skipMemoryCache(true)
                .centerCrop()
                .error(R.drawable.profile_24)
                .into(holder.imageView)
        }
        holder.imageView.setOnClickListener{

        }
    }

    override fun getItemCount(): Int {
        return imageList.size
    }

}