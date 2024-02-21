package com.example.partyapp

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.fragment.app.Fragment
import com.google.android.material.bottomnavigation.BottomNavigationView

class MainActivity : AppCompatActivity() {

    private fun replaceFragment(fragment: Fragment) {
        val fragmentManager = supportFragmentManager
        val fragmentTransaction = fragmentManager.beginTransaction()
        fragmentTransaction.replace(R.id.app_frame_layout, fragment)
        fragmentTransaction.commit()
    }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val bottomNavigationBar: BottomNavigationView = findViewById(R.id.bottom_navigation)
        bottomNavigationBar.selectedItemId = R.id.profileBN
        replaceFragment(ProfileFragment())
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
    }
}