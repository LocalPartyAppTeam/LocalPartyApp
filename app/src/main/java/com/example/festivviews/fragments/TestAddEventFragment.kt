package com.example.festivviews.fragments

import android.content.Intent
import android.os.Build
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import androidx.annotation.RequiresApi
import com.example.festivviews.R
import com.example.festivviews.activities.AddPartyActivity

class TestAddEventFragment : Fragment() {

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
        fun newInstance() : TestAddEventFragment {
                return TestAddEventFragment()
            }
    }
}