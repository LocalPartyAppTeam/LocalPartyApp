package com.example.partyapp

// LocalsAdapter.kt

import android.content.Intent
import android.os.Build
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.annotation.RequiresApi
import androidx.recyclerview.widget.RecyclerView
import java.time.LocalDateTime

@RequiresApi(Build.VERSION_CODES.O)
class LocalsAdapter(private val userLocation: Array<Double>,private val geo: GeoHelper, private val localsList: List<EventModel>) :
    RecyclerView.Adapter<LocalsAdapter.LocalViewHolder>() {

    inner class LocalViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
//        val eventNameHost: TextView = itemView.findViewById(R.id.text_event_name_host)
//        val eventTime: TextView = itemView.findViewById(R.id.text_event_time)
//        val eventAddressDistance: TextView = itemView.findViewById(R.id.text_event_address_distance)
//        val dayOfWeek: TextView = itemView.findViewById(R.id.text_day_of_week)
//        val dayOfMonth: TextView = itemView.findViewById(R.id.text_day_of_month)
        val eventNameHost: TextView = itemView.findViewById(R.id.LECH_host)
        val eventTitle: TextView = itemView.findViewById(R.id.LECHTitle)
        val eventNumberPhotos:TextView = itemView.findViewById(R.id.LECIPhotoCount)
        val eventAddressDistance : TextView = itemView.findViewById(R.id.text_event_address_distance)
        init {
            itemView.setOnClickListener {
                val context = itemView.context
                val currentItem = localsList[adapterPosition]

//                Toast.makeText(context, "Hello World", Toast.LENGTH_LONG).show()
                val intent = Intent(context,LocalsExtra::class.java).apply {
                    putExtra("name",currentItem.name)
                    putExtra("host",currentItem.host)
                    putExtra("address",currentItem.address)
                    putExtra("start",currentItem.start)
                    putExtra("end",currentItem.end)
                    putExtra("distance",geo.calculateDistance(userLocation[0], userLocation[1], currentItem.lat!!,currentItem.long!!).toString())
                    putExtra("dayOfWeek", LocalDateTime.parse(currentItem.start).dayOfWeek)
                    putExtra("dayOfMonth",LocalDateTime.parse(currentItem.start).dayOfMonth)
                    /*
                    i need the images
                    i need description
                     */
                    putExtra("desc",currentItem.desc)
                    putExtra("tags", (currentItem.tags)?.toTypedArray())
                    putExtra("sanitizedTags", (currentItem.sanitizedTags)?.toTypedArray())
                    putExtra("imgPaths", (currentItem.imgPaths)?.toTypedArray())
                    Log.i("TagsAdapter","tags ${currentItem.tags!!.size}")

                }
                context.startActivity(intent)
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): LocalViewHolder {
        val itemView = LayoutInflater.from(parent.context)
//            .inflate(R.layout.local_party_item2, parent, false)
            .inflate(R.layout.local_party_item, parent, false)

        return LocalViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: LocalViewHolder, position: Int) {
        val currentItem = localsList[position]
        holder.eventNameHost.text =  currentItem.host//currentItem.eventName + " (" + currentItem.distance + ")" // + " hosted by " + currentItem.host

//        holder.eventTime.text = currentItem.time
//        holder.eventAddressDistance.text = currentItem.address//  + " (" + currentItem.distance + ")"
        holder.eventAddressDistance.text = geo.calculateDistance(userLocation[0], userLocation[1], currentItem.lat!!,currentItem.long!!).toString()//  + " (" + currentItem.distance + ")"
        holder.eventTitle.text = currentItem.name
        holder.eventNumberPhotos.text = (currentItem.imgPaths?.size.toString() + " PHOTO(S)")
//        holder.dayOfWeek.text = currentItem.dayOfWeek
//        holder.dayOfMonth.text = currentItem.dayOfMonth


    }

    override fun getItemCount() = localsList.size
}
