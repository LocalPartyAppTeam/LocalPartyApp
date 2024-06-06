package com.example.partyapp.activities

import android.os.Build
import android.os.Bundle
import android.view.View
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import com.example.partyapp.fragments.InvitationsFragment
import com.example.partyapp.fragments.LocalsFragment
import com.example.partyapp.fragments.LoginFragment
import com.example.partyapp.fragments.MyEventsFragment
import com.example.partyapp.fragments.ProfileFragment
import com.example.partyapp.R
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase


@RequiresApi(Build.VERSION_CODES.TIRAMISU)
class MainActivity : AppCompatActivity() {

    private fun replaceFragment(fragment: Fragment) {
        val fragmentManager = supportFragmentManager
        val fragmentTransaction = fragmentManager.beginTransaction()
        fragmentTransaction.replace(R.id.app_frame_layout, fragment)
        fragmentTransaction.commit()
    }

    private lateinit var auth: FirebaseAuth
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        auth = Firebase.auth

        when (auth.currentUser) {
            null -> {
                this.findViewById<BottomNavigationView?>(R.id.bottom_navigation).visibility =
                    View.GONE
                val bottomNavigationBar: BottomNavigationView =
                    findViewById(R.id.bottom_navigation_logged_out)
                bottomNavigationBar.setOnItemSelectedListener { item ->
                    lateinit var fragment: Fragment
                    when (item.itemId) {
                        R.id.profileBN -> fragment = LoginFragment()
                    }
                    replaceFragment(fragment)
                    true
                }
                replaceFragment(LoginFragment())
            }
            else -> {
                this.findViewById<BottomNavigationView?>(R.id.bottom_navigation_logged_out).visibility = View.GONE
                val bottomNavigationBar:BottomNavigationView = findViewById(R.id.bottom_navigation)
                bottomNavigationBar.visibility = View.VISIBLE
                bottomNavigationBar.setOnItemSelectedListener { item ->
                    lateinit var fragment: Fragment
                    when (item.itemId) {
                        R.id.invitationsBN -> fragment = InvitationsFragment()
                        R.id.eventsBN -> fragment = MyEventsFragment()
                        R.id.localsBN -> fragment = LocalsFragment()
                        R.id.profileBN -> fragment = ProfileFragment()
                    }
                    replaceFragment(fragment)
                    true
                }
                bottomNavigationBar.selectedItemId = R.id.profileBN
                replaceFragment(ProfileFragment())
            }
        }
    }
}