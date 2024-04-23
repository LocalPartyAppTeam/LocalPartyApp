package com.example.partyapp

import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.card.MaterialCardView

class MiniEstAdapter(private val miniEstArray: MutableList<EstablishmentModel>, private val context: Context, private val location: Array<Double>): RecyclerView.Adapter<MiniEstAdapter.ViewHolder>() {

    class ViewHolder(itemView: View): RecyclerView.ViewHolder(itemView){
        val nameTextView: TextView
        val locationTextView: TextView
        val descTextView: TextView
        val miniEstCard: MaterialCardView
        init {
            nameTextView = itemView.findViewById(R.id.miniEstCardName)
            locationTextView = itemView.findViewById(R.id.miniEstCardDist)
            descTextView = itemView.findViewById(R.id.miniEstCardDesc)
            miniEstCard = itemView.findViewById(R.id.mini_est_card)
        }
    }
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MiniEstAdapter.ViewHolder {
        val context = parent.context
        val inflater = LayoutInflater.from(context)
        val view = LayoutInflater.from(parent.context).inflate(R.layout.mini_est_item, parent, false)
        return ViewHolder(view)
    }
    override fun onBindViewHolder(holder: MiniEstAdapter.ViewHolder, position: Int) {
        val miniEst = miniEstArray[position]
        val geo = GeoHelper(context)
        holder.nameTextView.text = miniEst.name
        holder.locationTextView.text = geo.calculateDistance(location[0],location[1],miniEst.lat!!,miniEst.long!!).toString() + " mi"
        holder.descTextView.text = miniEst.desc
        holder.miniEstCard.setOnClickListener{
            val intent = Intent(context,EstablishmentExtra::class.java).apply {
                putExtra("establishment",miniEst)
            }
            context.startActivity(intent)
        }

    }
    override fun getItemCount(): Int {
        return miniEstArray.size
    }
}