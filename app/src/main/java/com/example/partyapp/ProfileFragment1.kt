package com.example.partyapp

import android.content.Intent
import android.os.Build
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import androidx.annotation.RequiresApi

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 * Use the [ProfileFragment2.newInstance] factory method to
 * create an instance of this fragment.
 */
class ProfileFragment1 : Fragment() {

    @RequiresApi(Build.VERSION_CODES.P)
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_profile1, container, false)
        val launchActivity = view.findViewById<Button>(R.id.testAddActivity)
        launchActivity.setOnClickListener {
            startActivity(Intent(activity, AddPartyActivity::class.java))
        }
        return view
    }

    companion object {
        @JvmStatic
        fun newInstance() : ProfileFragment1 {
                return ProfileFragment1()
            }
    }
}