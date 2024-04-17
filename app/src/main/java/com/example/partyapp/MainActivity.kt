package com.example.partyapp

import android.os.Build
import android.os.Bundle
import android.view.View
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase


@RequiresApi(Build.VERSION_CODES.P)
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
                        R.id.eventsBN -> fragment = LoginFragment()
                        R.id.localsBN -> fragment = LoginFragment()
                        R.id.profileBN -> fragment = LoginFragment()
                    }
                    replaceFragment(fragment)
                    true
                }
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
                        R.id.localsBN -> fragment = LocalsFragment2()
                        R.id.profileBN -> fragment = ProfileFragment()
                    }
                    replaceFragment(fragment)
                    true
                }
            }
        }
        replaceFragment(ProfileFragment())
    }
}