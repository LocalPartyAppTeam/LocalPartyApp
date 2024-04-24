package com.example.partyapp

import android.content.Context
import android.content.Intent
import android.os.Build
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.core.os.persistableBundleOf
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import java.time.LocalDateTime

@RequiresApi(Build.VERSION_CODES.O)
class MyEventsAdapter(private val context: Context,
                      private val userLocation: Array<Double>,
                      private val geo: GeoHelper,
                      private val eventList: List<EventModel>,
                      private val owner: Boolean ):
    RecyclerView.Adapter<MyEventsAdapter.ViewHolder>() {
    val databaseRef = FirebaseDatabase.getInstance().getReference("Users")
    val usersAttendingRef: DatabaseReference = FirebaseDatabase.getInstance().reference.child("UsersAttending")
    val taggedEvents: DatabaseReference = FirebaseDatabase.getInstance().reference.child("TaggedEvents")
    val eventAttendees: DatabaseReference = FirebaseDatabase.getInstance().reference.child("EventAttendees")
    private lateinit var auth: FirebaseAuth

    init {
        auth = FirebaseAuth.getInstance()
    }

    private val currUser = auth.currentUser!!.uid



    inner class ViewHolder(itemView: View): RecyclerView.ViewHolder(itemView) {

        val eventNameHost: TextView = itemView.findViewById(R.id.MECH_host)
        val eventTitle: TextView = itemView.findViewById(R.id.MECHTitle)
        val eventDesc: TextView = itemView.findViewById(R.id.MECIDescription)
        val eventNumberPhotos: TextView = itemView.findViewById(R.id.MECIPhotoCount)
        val eventAddressDistance : TextView = itemView.findViewById(R.id.text_event_address_distance)
        val eventTagsRV : RecyclerView = itemView.findViewById(R.id.myEventItemTagsRV)
//        private lateinit var auth: FirebaseAuth
//        val currUserID = auth.currentUser!!.uid

        init {
            itemView.setOnClickListener {
                val context = itemView.context
                val currentItem = eventList[adapterPosition]

//               Toast.makeText(context, currentItem.pushId, Toast.LENGTH_LONG).show()
//                /*
//                currentItem.pushId is the code i need for the event
//                1. iterate through Users attending and find current user id
//                2. iterate through each event id and find the currentItem.pushId
//                3. once found then delete it
//                 */
                if(owner){
                    val intent = Intent(context,OwnerEventExtraActivity::class.java).apply {
                        putExtra("event",currentItem)
                        putExtra("distance",geo.calculateDistance(userLocation[0], userLocation[1], currentItem.lat!!,currentItem.long!!).toString())
                        putExtra("dayOfWeek", LocalDateTime.parse(currentItem.start).dayOfWeek)
                        putExtra("dayOfMonth",LocalDateTime.parse(currentItem.start).dayOfMonth)


                        taggedEvents.addListenerForSingleValueEvent(object:
                            ValueEventListener{
                            override fun onDataChange(snapshot: DataSnapshot) {
                                for(childSnapshot in snapshot.children){
                                    val userID: String = childSnapshot.key!!
                                    if(userID == currUser){
                                        Toast.makeText(context,"delete this one (TaggedEvents) " + userID,Toast.LENGTH_LONG).show()

//                                        taggedEvents.child(userID).removeValue()
//                                            .addOnSuccessListener {
////                                                Toast.makeText(context, "User deleted: $userID", Toast.LENGTH_SHORT).show()
//                                            }
//                                            .addOnFailureListener { e ->
////                                                Toast.makeText(context, "Failed to delete user: $userID", Toast.LENGTH_SHORT).show()
//                                                Log.e("Firebase", "Failed to delete user: $userID", e)
//                                            }
//                                        break
                                    }

                                }

                            }

                            override fun onCancelled(error: DatabaseError) {
                                TODO("Not yet implemented")
                            }
                        })
                    }
                    context.startActivity(intent)
                }else{
                    val intent = Intent(context,LocalsExtra::class.java).apply {
                        putExtra("event",currentItem)
                        putExtra("distance",geo.calculateDistance(userLocation[0], userLocation[1], currentItem.lat!!,currentItem.long!!).toString())
                        putExtra("dayOfWeek", LocalDateTime.parse(currentItem.start).dayOfWeek)
                        putExtra("dayOfMonth",LocalDateTime.parse(currentItem.start).dayOfMonth)

//                        Toast.makeText(context, currentItem.pushId, Toast.LENGTH_LONG).show()
//                        Toast.makeText(context, currentItem.pushId, Toast.LENGTH_LONG).show()
                        usersAttendingRef.addListenerForSingleValueEvent(object :
                            ValueEventListener {
                            override fun onDataChange(dataSnapshot: DataSnapshot) {
                                for (childSnapshot in dataSnapshot.children) {
                                    val userID: String = childSnapshot.key!!
//                                    Toast.makeText(context, userID, Toast.LENGTH_SHORT).show()
                                    /*
                                    if userID == currentUser then delete that node under UsersAttending
                                     */
                                    if(currUser == userID){
                                        Toast.makeText(context,"delete this one (Attending Events) " + userID,Toast.LENGTH_LONG).show()

                                        val usersInner = currentItem.pushId?.let { it1 ->
                                            eventAttendees.child(
                                                it1
                                            )
                                        }
                                        if (usersInner != null) {
                                            usersInner.addListenerForSingleValueEvent(object  : ValueEventListener {
                                                override fun onDataChange(snapshot: DataSnapshot) {
                                                    for(snap in snapshot.children){
                                                        val userIDinner: String = childSnapshot.key!!
                                                        if(userIDinner == currUser){
                                                            if (usersInner != null) {
//                                                                usersInner.child(userIDinner).removeValue()
                                                            }
                                                        }

                                                    }
                                                }

                                                override fun onCancelled(error: DatabaseError) {
                                                    TODO("Not yet implemented")
                                                }
                                            })
                                        }

//                                        usersAttendingRef.child(userID).removeValue()
//                                            .addOnSuccessListener {
//                                                Toast.makeText(context, "User deleted: $userID", Toast.LENGTH_SHORT).show()
//                                            }
//                                            .addOnFailureListener { e ->
//                                                Toast.makeText(context, "Failed to delete user: $userID", Toast.LENGTH_SHORT).show()
//                                                Log.e("Firebase", "Failed to delete user: $userID", e)
//                                            }
//                                        break


                                    }
                                }
                            }

                            override fun onCancelled(databaseError: DatabaseError) {
                                // Handle potential errors
                            }
                        })
                        /*
                        currentItem.pushId is the code i need for the event
                        1. iterate through Users attending and find current user id
                        2. iterate through each event id and find the currentItem.pushId
                        3. once found then delete it
                         */
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
//        val currUser = auth.currentUser!!.uid


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
//            Toast.makeText(context,it.getValue(UserModel::class.java)?.username,Toast.LENGTH_SHORT).show()
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
//        Toast.makeText(context,holder.toString(),Toast.LENGTH_LONG).show()

        if(currentEvent.imgPaths.isNullOrEmpty()){
            holder.eventNumberPhotos.text = "0 PHOTO(S)"
        }else{
            holder.eventNumberPhotos.text = (currentEvent.imgPaths!!.size.toString() + " PHOTO(S)")
        }
    }
}