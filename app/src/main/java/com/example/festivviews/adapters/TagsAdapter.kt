package com.example.festivviews.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.festivviews.R
import com.example.festivviews.models.TagModel

class TagsAdapter(private val removable: Boolean, private val tagItems: MutableList<TagModel>): RecyclerView.Adapter<TagsAdapter.ViewHolder>() {
    class ViewHolder(itemView: View): RecyclerView.ViewHolder(itemView){
        val tagTextView: TextView
        init {
            tagTextView = itemView.findViewById(R.id.tagText)
        }
    }
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val context = parent.context
        val inflater = LayoutInflater.from(context)
        val contactView = inflater.inflate(R.layout.tag_item, parent, false)
        return ViewHolder(contactView)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val tagListItem = tagItems[position]
        holder.tagTextView.text = tagListItem.text
        if(removable){
            holder.tagTextView.setOnLongClickListener{
                val pos = holder.adapterPosition
                tagItems.removeAt(pos)
                this.notifyItemRemoved(pos)
                return@setOnLongClickListener true
            }
        }
    }

    override fun getItemCount(): Int {
        return tagItems.size
    }
}