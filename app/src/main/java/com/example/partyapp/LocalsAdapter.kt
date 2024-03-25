package com.example.partyapp

// LocalsAdapter.kt

import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView

class LocalsAdapter(private val localsList: List<LocalItem>) :
    RecyclerView.Adapter<LocalsAdapter.LocalViewHolder>() {

    inner class LocalViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val eventNameHost: TextView = itemView.findViewById(R.id.text_event_name_host)
        val eventTime: TextView = itemView.findViewById(R.id.text_event_time)
        val eventAddressDistance: TextView = itemView.findViewById(R.id.text_event_address_distance)
        val dayOfWeek: TextView = itemView.findViewById(R.id.text_day_of_week)
        val dayOfMonth: TextView = itemView.findViewById(R.id.text_day_of_month)
        init {
            itemView.setOnClickListener {
                val context = itemView.context
                val currentItem = localsList[adapterPosition]

//                Toast.makeText(context, "Hello World", Toast.LENGTH_LONG).show()
                val intent = Intent(context,LocalsExtra::class.java).apply {
                    putExtra("eventName",currentItem.eventName)
                    putExtra("host",currentItem.host)
                    putExtra("address",currentItem.address)
                    putExtra("time",currentItem.time)
                    putExtra("distance",currentItem.distance)
                    putExtra("dayOfWeek",currentItem.dayOfWeek)
                    putExtra("dayOfMonth",currentItem.dayOfMonth)
                    /*
                    i need the images
                    i need description
                     */
                    putExtra("description",currentItem.description)
                    putExtra("imagePaths", (currentItem.iP)?.toTypedArray())

                }
                context.startActivity(intent)
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): LocalViewHolder {
        val itemView = LayoutInflater.from(parent.context)
            .inflate(R.layout.local_party_item2, parent, false)
        return LocalViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: LocalViewHolder, position: Int) {
        val currentItem = localsList[position]
        holder.eventNameHost.text = currentItem.eventName + " (" + currentItem.distance + ")" // + " hosted by " + currentItem.host
        holder.eventTime.text = currentItem.time
        holder.eventAddressDistance.text = currentItem.address//  + " (" + currentItem.distance + ")"
        holder.dayOfWeek.text = currentItem.dayOfWeek
        holder.dayOfMonth.text = currentItem.dayOfMonth


    }

    override fun getItemCount() = localsList.size
}
