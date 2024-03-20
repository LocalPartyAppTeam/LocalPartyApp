package com.example.partyapp
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.Firebase
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
//import com.google.firebase.database.core.view.View
import com.google.firebase.database.database

class LocalsFragment2 : Fragment() {
    private lateinit var recyclerView: RecyclerView
    private lateinit var adapter: LocalsAdapter
    private lateinit var databaseReference: DatabaseReference


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_locals, container, false)
        recyclerView = view.findViewById(R.id.recycler_locals)
        recyclerView.layoutManager = LinearLayoutManager(requireContext())

        ///random events will add from firebase later
        val firebaseDatabase  = Firebase.database
        val myRef = firebaseDatabase.getReference("Events")

        // Read from the database
        databaseReference.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                // Database connection successful
//                toast("Connected to the Firebase Realtime Database")

            }

            override fun onCancelled(databaseError: DatabaseError) {
                // Database connection failed
//                toast("Failed to connect to the Firebase Realtime Database")
            }
        })

        val localsList = mutableListOf<LocalItem>()
        localsList.add(LocalItem("Event 1", "Host 1", "Address 1", "Time 1", "Date 1", "Mon","20"))
        localsList.add(LocalItem("Event 2", "Host 2", "Address 2", "Time 2", "Date 2","Mon","20"))
        adapter = LocalsAdapter(localsList)
        recyclerView.adapter = adapter
        return view

    }
}
