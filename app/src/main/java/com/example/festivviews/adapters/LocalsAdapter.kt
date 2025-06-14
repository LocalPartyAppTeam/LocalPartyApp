package com.example.festivviews.adapters

// LocalsAdapter.kt

import android.content.Context
import android.content.Intent
import android.os.Build
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.annotation.RequiresApi
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.festivviews.GeoHelper
import com.example.festivviews.activities.LocalsExtraActivity
import com.example.festivviews.R
import com.example.festivviews.models.EventModel
import com.example.festivviews.models.TagModel
import com.example.festivviews.models.UserModel
import com.google.firebase.database.FirebaseDatabase
import java.time.LocalDateTime

@RequiresApi(Build.VERSION_CODES.TIRAMISU)
class LocalsAdapter(private val context: Context, private val userLocation: Array<Double>, private val geo: GeoHelper, private val localsList: List<EventModel>) :
    RecyclerView.Adapter<LocalsAdapter.LocalViewHolder>() {
        val databaseRef = FirebaseDatabase.getInstance().getReference("Users")

    inner class LocalViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
//        val eventNameHost: TextView = itemView.findViewById(R.id.text_event_name_host)
//        val eventTime: TextView = itemView.findViewById(R.id.text_event_time)
//        val eventAddressDistance: TextView = itemView.findViewById(R.id.text_event_address_distance)
//        val dayOfWeek: TextView = itemView.findViewById(R.id.text_day_of_week)
//        val dayOfMonth: TextView = itemView.findViewById(R.id.text_day_of_month)
        val eventNameHost: TextView = itemView.findViewById(R.id.LECH_host)
        val eventTitle: TextView = itemView.findViewById(R.id.LECHTitle)
        val eventDesc: TextView = itemView.findViewById(R.id.LECIDescription)
        val eventNumberPhotos:TextView = itemView.findViewById(R.id.LECIPhotoCount)
        val eventAddressDistance : TextView = itemView.findViewById(R.id.text_event_address_distance)
        val eventTagsRV : RecyclerView = itemView.findViewById(R.id.eventItemTagsRV)
        init {
            itemView.setOnClickListener {
                val context = itemView.context
                val currentItem = localsList[adapterPosition]

//                Toast.makeText(context, "Hello World", Toast.LENGTH_LONG).show()
                val intent = Intent(context, LocalsExtraActivity::class.java).apply {
                    putExtra("event",currentItem)
                    putExtra("distance",geo.calculateDistance(userLocation[0], userLocation[1], currentItem.lat!!,currentItem.long!!).toString())
                    putExtra("dayOfWeek", LocalDateTime.parse(currentItem.start).dayOfWeek)
                    putExtra("dayOfMonth",LocalDateTime.parse(currentItem.start).dayOfMonth)
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
        val eventTagsList:MutableList<TagModel> = mutableListOf<TagModel>()
        holder.eventTagsRV.layoutManager = LinearLayoutManager(context, RecyclerView.HORIZONTAL, false)
        val eventTagsAdapter = TagsAdapter(false,eventTagsList)
        holder.eventTagsRV.adapter = eventTagsAdapter
        databaseRef.child(currentItem.host!!).get().addOnSuccessListener {
            holder.eventNameHost.text = it.getValue(UserModel::class.java)?.username ?: "unknown"
        }
        //currentItem.eventName + " (" + currentItem.distance + ")" // + " hosted by " + currentItem.host
        if(currentItem.tags != null){
            for(tag in currentItem.tags!!){
                eventTagsList.add(TagModel(text=tag))
                eventTagsAdapter.notifyItemInserted(eventTagsAdapter.itemCount)
            }
        }

//        holder.eventTime.text = currentItem.time
//        holder.eventAddressDistance.text = currentItem.address//  + " (" + currentItem.distance + ")"
        holder.eventAddressDistance.text = geo.calculateDistance(userLocation[0], userLocation[1], currentItem.lat!!,currentItem.long!!).toString()+" mi"//  + " (" + currentItem.distance + ")"
        holder.eventTitle.text = currentItem.name
        holder.eventDesc.text = currentItem.desc
        if(currentItem.imgPaths.isNullOrEmpty()){
            holder.eventNumberPhotos.text = "0 PHOTO(S)"
        }else{
            holder.eventNumberPhotos.text = (currentItem.imgPaths!!.size.toString() + " PHOTO(S)")
        }
//        holder.dayOfWeek.text = currentItem.dayOfWeek
//        holder.dayOfMonth.text = currentItem.dayOfMonth


    }

    override fun getItemCount() = localsList.size
}
