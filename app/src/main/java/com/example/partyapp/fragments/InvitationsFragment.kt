package com.example.partyapp.fragments

import android.content.Intent
import android.os.Build
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.annotation.RequiresApi
import com.example.partyapp.R
import com.example.partyapp.activities.AddEstablishmentActivity
import com.example.partyapp.activities.AddPartyActivity


@RequiresApi(Build.VERSION_CODES.P)
class InvitationsFragment : Fragment() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_invitations, container, false)

        val launchAddEventActivity = view.findViewById<ImageView>(R.id.eventsFragmentAddEventButton)
        val launchAddEstActivity = view.findViewById<ImageView>(R.id.eventsFragmentAddEstablishmentButton)
        launchAddEventActivity.setOnClickListener {
            startActivity(Intent(activity, AddPartyActivity::class.java))
        }
        launchAddEstActivity.setOnClickListener {
            startActivity(Intent(activity, AddEstablishmentActivity::class.java))
        }
        return view
    }

}