package com.example.festivviews.adapters

import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.festivviews.activities.EstablishmentExtraActivity
import com.example.festivviews.GeoHelper
import com.example.festivviews.R
import com.example.festivviews.models.EstablishmentModel
import com.example.festivviews.models.TagModel

class LocalEstablishmentAdapter(private val context: Context, private val userLocation: Array<Double>, private val geo: GeoHelper, private val establishmentList: List<EstablishmentModel>) :
    RecyclerView.Adapter<LocalEstablishmentAdapter.LocalEstablishmentViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): LocalEstablishmentViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.local_establishment_item, parent, false)
        return LocalEstablishmentViewHolder(view)
    }

    override fun onBindViewHolder(holder: LocalEstablishmentViewHolder, position: Int) {
        val establishment = establishmentList[position]
        holder.estTagsRV.layoutManager = LinearLayoutManager(context, RecyclerView.HORIZONTAL, false)
        val estTagsList = mutableListOf<TagModel>()
        val eventTagsAdapter = TagsAdapter(false,estTagsList)
        holder.estTagsRV.adapter = eventTagsAdapter
        if(establishment.tags != null){
            for(tag in establishment.tags!!){
                estTagsList.add(TagModel(text=tag))
                eventTagsAdapter.notifyItemInserted(eventTagsAdapter.itemCount)
            }
        }
        holder.titleTextView.text = establishment.name
        holder.distanceTextView.text = geo.calculateDistance(userLocation[0], userLocation[1], establishment.lat!!,establishment.long!!).toString()+" mi"
        holder.descriptionTextView.text = establishment.desc
    }

    override fun getItemCount(): Int {
        return establishmentList.size
    }

    inner class LocalEstablishmentViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val titleTextView: TextView = itemView.findViewById(R.id.LECHTitle)
        val distanceTextView: TextView = itemView.findViewById(R.id.text_event_address_distance)
        val descriptionTextView: TextView = itemView.findViewById(R.id.LECIDescription)
        val estTagsRV : RecyclerView = itemView.findViewById(R.id.estItemTagsRV)

        init {
            itemView.setOnClickListener {
                val context = itemView.context
                val currentItem = establishmentList[adapterPosition]
                val intent = Intent(context, EstablishmentExtraActivity::class.java).apply {
                    putExtra("establishment",currentItem)
                    putExtra("distance",geo.calculateDistance(userLocation[0], userLocation[1], currentItem.lat!!,currentItem.long!!).toString())
                }
                context.startActivity(intent)
            }
        }

    }
}
