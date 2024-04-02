package com.example.partyapp

import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

class MyEventsAdapter(private val EventsList: List<MyItem>) :
    RecyclerView.Adapter<MyEventsAdapter.MyEventsViewHolder>() {

    inner class MyEventsViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val eventNameHost: TextView = itemView.findViewById(R.id.LECH_host)
        val eventTitle: TextView = itemView.findViewById(R.id.LECHTitle)
        val eventNumberPhotos: TextView = itemView.findViewById(R.id.LECIPhotoCount)
        val eventAddressDistance: TextView = itemView.findViewById(R.id.text_event_address_distance)

        init {
            itemView.setOnClickListener {
                val context = itemView.context
                val currentItem = EventsList[adapterPosition]

//                Toast.makeText(context, "Hello World", Toast.LENGTH_LONG).show()
                val intent = Intent(context, LocalsExtra::class.java).apply {
                    putExtra("eventName", currentItem.eventName)
                    putExtra("host", currentItem.host)
                    putExtra("address", currentItem.address)
                    putExtra("time", currentItem.time)
                    putExtra("distance", currentItem.distance)
                    putExtra("dayOfWeek", currentItem.dayOfWeek)
                    putExtra("dayOfMonth", currentItem.dayOfMonth)
                    /*
                    i need the images
                    i need description
                     */
                    putExtra("description", currentItem.description)
                    putExtra("imagePaths", (currentItem.iP)?.toTypedArray())

                }
                context.startActivity(intent)
            }
        }
    }
        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyEventsViewHolder {
            //TODO("Not yet implemented")
            val itemView = LayoutInflater.from(parent.context)
//            .inflate(R.layout.local_party_item2, parent, false)
                .inflate(R.layout.local_party_item, parent, false)

            return MyEventsViewHolder(itemView)
        }

        override fun getItemCount() = EventsList.size


    override fun onBindViewHolder(holder: MyEventsViewHolder, position: Int) {
        //TODO("Not yet implemented")
        val currentItem = EventsList[position]
        holder.eventNameHost.text =  currentItem.host//currentItem.eventName + " (" + currentItem.distance + ")" // + " hosted by " + currentItem.host

//        holder.eventTime.text = currentItem.time
//        holder.eventAddressDistance.text = currentItem.address//  + " (" + currentItem.distance + ")"
        holder.eventAddressDistance.text = currentItem.distance//  + " (" + currentItem.distance + ")"
        holder.eventTitle.text = currentItem.eventName
        holder.eventNumberPhotos.text = (currentItem.iP?.size.toString() + " PHOTO(S)")
    }
}