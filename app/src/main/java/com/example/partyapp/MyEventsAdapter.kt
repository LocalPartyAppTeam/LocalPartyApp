package com.example.partyapp

import android.content.Context
import android.content.Intent
import android.os.Build
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.annotation.RequiresApi
import androidx.core.os.persistableBundleOf
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.database.FirebaseDatabase
import java.time.LocalDateTime

@RequiresApi(Build.VERSION_CODES.O)
class MyEventsAdapter(private val context: Context,
                      private val userLocation: Array<Double>,
                      private val geo: GeoHelper,
                      private val eventList: List<EventModel>,
                      private val owner: Boolean ):
    RecyclerView.Adapter<MyEventsAdapter.ViewHolder>() {
    val databaseRef = FirebaseDatabase.getInstance().getReference("Users")
    inner class ViewHolder(itemView: View): RecyclerView.ViewHolder(itemView) {

        val eventNameHost: TextView = itemView.findViewById(R.id.MECH_host)
        val eventTitle: TextView = itemView.findViewById(R.id.MECHTitle)
        val eventDesc: TextView = itemView.findViewById(R.id.MECIDescription)
        val eventNumberPhotos: TextView = itemView.findViewById(R.id.MECIPhotoCount)
        val eventAddressDistance : TextView = itemView.findViewById(R.id.text_event_address_distance)
        val eventTagsRV : RecyclerView = itemView.findViewById(R.id.myEventItemTagsRV)
        init {
            itemView.setOnClickListener {
                val context = itemView.context
                val currentItem = eventList[adapterPosition]

//               Toast.makeText(context, "Hello World", Toast.LENGTH_LONG).show()
                if(owner){
                    val intent = Intent(context,OwnerEventExtraActivity::class.java).apply {
                        putExtra("event",currentItem)
                        putExtra("distance",geo.calculateDistance(userLocation[0], userLocation[1], currentItem.lat!!,currentItem.long!!).toString())
                        putExtra("dayOfWeek", LocalDateTime.parse(currentItem.start).dayOfWeek)
                        putExtra("dayOfMonth",LocalDateTime.parse(currentItem.start).dayOfMonth)
                    }
                    context.startActivity(intent)
                }else{
                    val intent = Intent(context,LocalsExtra::class.java).apply {
                        putExtra("event",currentItem)
                        putExtra("distance",geo.calculateDistance(userLocation[0], userLocation[1], currentItem.lat!!,currentItem.long!!).toString())
                        putExtra("dayOfWeek", LocalDateTime.parse(currentItem.start).dayOfWeek)
                        putExtra("dayOfMonth",LocalDateTime.parse(currentItem.start).dayOfMonth)

                    }
                    context.startActivity(intent)
                }

            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val itemView = LayoutInflater.from(parent.context)
//            .inflate(R.layout.local_party_item2, parent, false)
            .inflate(R.layout.my_event_item, parent, false)

        return ViewHolder(itemView)
    }

    override fun getItemCount(): Int {
        return eventList.size
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val currentEvent = eventList[position]
        val eventTagsList:MutableList<TagModel> = mutableListOf<TagModel>()
        holder.eventTagsRV.layoutManager = LinearLayoutManager(context, RecyclerView.HORIZONTAL, false)
        val eventTagsAdapter = TagsAdapter(false,eventTagsList)
        holder.eventTagsRV.adapter = eventTagsAdapter
        databaseRef.child(currentEvent.host!!).get().addOnSuccessListener {
            holder.eventNameHost.text = it.getValue(UserModel::class.java)?.username ?: "unknown"
        }
        if(currentEvent.tags != null){
            for(tag in currentEvent.tags!!){
                eventTagsList.add(TagModel(text=tag))
                eventTagsAdapter.notifyItemInserted(eventTagsAdapter.itemCount)
            }
        }
        holder.eventDesc.text =  currentEvent.desc
        holder.eventAddressDistance.text = geo.calculateDistance(userLocation[0], userLocation[1], currentEvent.lat!!,currentEvent.long!!).toString()+" mi"//  + " (" + currentItem.distance + ")"
        holder.eventTitle.text = currentEvent.name

        if(currentEvent.imgPaths.isNullOrEmpty()){
            holder.eventNumberPhotos.text = "0 PHOTO(S)"
        }else{
            holder.eventNumberPhotos.text = (currentEvent.imgPaths!!.size.toString() + " PHOTO(S)")
        }
    }
}