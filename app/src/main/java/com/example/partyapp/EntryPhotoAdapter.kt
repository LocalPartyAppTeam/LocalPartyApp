package com.example.partyapp

import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.bumptech.glide.load.engine.DiskCacheStrategy
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.RecyclerView.ViewHolder
import com.bumptech.glide.Glide
import com.google.firebase.storage.FirebaseStorage

private val storageRef = FirebaseStorage.getInstance().reference.child("partyImages")
class EntryPhotoAdapter(){}
/*(
    private val context: Context,
    private val entries: MutableList<AddPictureRVEntryModel>,
): RecyclerView.Adapter<EntryPhotoAdapter.EntryViewHolder>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): EntryViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.party_image_item, parent, false)
        return EntryViewHolder(view)
    }

    override fun getItemCount() = entries.size

    override fun onBindViewHolder(holder: EntryViewHolder, position: Int) {
        val entry = entries[position]
        if(position < itemCount-1) {
            holder.item = entry
            holder.plus.isEnabled = false
            Glide.with(holder.itemView)
                .load(storageRef.child(entry.imgPath!!))
                .diskCacheStrategy(DiskCacheStrategy.NONE)
                .skipMemoryCache(true)
                .error(R.drawable.profile_24)
                .into(holder.photo)
            holder.photo.setOnLongClickListener(){
                entries.removeAt(position)
                this.notifyDataSetChanged()
            }
        }
        holder.plus.setOnClickListener()
    }



    inner class EntryViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView), View.OnClickListener {
        var item: AddPictureRVEntryModel? = null
        val photo: ImageView = itemView.findViewById(R.id.photo)
        val plus: ImageView = itemView.findViewById(R.id.addPhotoIcon)

        init {
            itemView.setOnClickListener(this)
        }

        override fun onClick(p0: View?) {
        }
    }
}

*/