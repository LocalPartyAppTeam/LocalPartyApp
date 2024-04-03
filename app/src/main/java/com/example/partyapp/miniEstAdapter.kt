package com.example.partyapp

import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.card.MaterialCardView

class miniEstAdapter(private val miniEstArray: MutableList<EstablishmentModel>, private val context: Context): RecyclerView.Adapter<miniEstAdapter.ViewHolder>() {

    class ViewHolder(itemView: View): RecyclerView.ViewHolder(itemView){
        val nameTextView: TextView
        val locationTextView: TextView
        val descTextView: TextView
        val miniEstCard: MaterialCardView
        init {
            nameTextView = itemView.findViewById(R.id.tagText)
            locationTextView = itemView.findViewById(R.id.miniEstCardDist)
            descTextView = itemView.findViewById(R.id.miniEstCardDesc)
            miniEstCard = itemView.findViewById(R.id.mini_est_card)
        }
    }
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): miniEstAdapter.ViewHolder {
        val context = parent.context
        val inflater = LayoutInflater.from(context)
        val view = LayoutInflater.from(parent.context).inflate(R.layout.mini_est_item, parent, false)
        return ViewHolder(view)
    }
    override fun onBindViewHolder(holder: miniEstAdapter.ViewHolder, position: Int) {
        val miniEst = miniEstArray[position]
        holder.nameTextView.text = miniEst.name
        holder.locationTextView.text = miniEst.name
        holder.descTextView.text = miniEst.name
        holder.miniEstCard.setOnClickListener{
            val intent = Intent(context,EstablishmentExtra::class.java).apply {
                putExtra("name",miniEst.name)
                putExtra("owner",miniEst.ownerAccount)
                putExtra("lat",miniEst.lat)
                putExtra("long",miniEst.long)
                putExtra("desc",miniEst.desc)
                putExtra("address",miniEst.address)
                putExtra("tags",(miniEst.tags)?.toTypedArray())
                putExtra("imgPaths",(miniEst.imgPaths)?.toTypedArray())
            }
            context.startActivity(intent)
        }

    }
    override fun getItemCount(): Int {
        TODO("Not yet implemented")
    }
}