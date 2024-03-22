
package com.example.partyapp

import android.content.ContentValues
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.ktx.Firebase
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.ktx.database
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
        val currentUser = auth.currentUser
        auth.signInWithEmailAndPassword("okb3@njit.edu",
            "aaaaaaaa"
        )
            .addOnCompleteListener(this) { task ->
                if (task.isSuccessful) {
                    Log.d(ContentValues.TAG, "createUserWithEmail:success")
                    val user = auth.currentUser
                } else {
                    Log.w(ContentValues.TAG, "createUserWithEmail:failure", task.exception)
                }
            }

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