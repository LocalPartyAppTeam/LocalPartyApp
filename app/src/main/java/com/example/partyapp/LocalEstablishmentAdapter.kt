package com.example.partyapp

import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView

class LocalEstablishmentAdapter(private val userLocation: Array<Double>,private val geo: GeoHelper, private val establishmentList: List<EstablishmentModel>) :
    RecyclerView.Adapter<LocalEstablishmentAdapter.LocalEstablishmentViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): LocalEstablishmentViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.local_establishment_item, parent, false)
        return LocalEstablishmentViewHolder(view)
    }

    override fun onBindViewHolder(holder: LocalEstablishmentViewHolder, position: Int) {
        val establishment = establishmentList[position]
        holder.titleTextView.text = establishment.name
        holder.distanceTextView.text = geo.calculateDistance(userLocation[0], userLocation[1], establishment.lat!!,establishment.long!!).toString()
        holder.descriptionTextView.text = establishment.desc
    }

    override fun getItemCount(): Int {
        return establishmentList.size
    }

    inner class LocalEstablishmentViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val titleTextView: TextView = itemView.findViewById(R.id.LECHTitle)
        val distanceTextView: TextView = itemView.findViewById(R.id.text_event_address_distance)
        val descriptionTextView: TextView = itemView.findViewById(R.id.LECIDescription)

        init {
            itemView.setOnClickListener {
                val context = itemView.context
                val currentItem = establishmentList[adapterPosition]
                val intent = Intent(context,EstablishmentExtra::class.java).apply {
                    putExtra("establishmentName",currentItem.name)
                    putExtra("owner",currentItem.ownerAccount)
                    putExtra("address",currentItem.address)
                    putExtra("description",currentItem.desc)
                    putExtra("tags", (currentItem.tags)?.toTypedArray())
                    putExtra("sanitizedTags", (currentItem.sanitizedTags)?.toTypedArray())
                    putExtra("imgPaths", (currentItem.imgPaths)?.toTypedArray())
                    putExtra("distance",geo.calculateDistance(userLocation[0], userLocation[1], currentItem.lat!!,currentItem.long!!).toString())
                }
                context.startActivity(intent)
            }
        }

    }
}
