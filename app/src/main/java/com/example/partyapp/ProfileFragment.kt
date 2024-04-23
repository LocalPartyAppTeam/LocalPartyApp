package com.example.partyapp

import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.RequiresApi
import androidx.fragment.app.Fragment
import com.example.partyapp.databinding.FragmentProfileBinding
import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.auth
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.getValue

@RequiresApi(Build.VERSION_CODES.TIRAMISU)
class ProfileFragment : Fragment(){
    private lateinit var auth: FirebaseAuth

    private var _binding: FragmentProfileBinding? = null
    private val binding: FragmentProfileBinding
        get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        _binding = FragmentProfileBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        auth = Firebase.auth
        val databaseRef = FirebaseDatabase.getInstance().getReference("Users")

        with(binding) {
            logoutButton.setOnClickListener {
               signOut()
            }
            databaseRef.child(auth.currentUser!!.uid).get().addOnSuccessListener {
                val user = it.getValue(UserModel::class.java)
                if(user != null){
                    nameTV.text = "Name: "+ user.firstName + " " + user.lastName
                    emailTV.text = "Email: "+ user.email
                }
            }
        }
    }

    private fun signOut() {
        auth.signOut()
        val intent = Intent(context, MainActivity::class.java)
        intent.putExtra("finish", true)
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP) // To clean up all activities
        startActivity(intent)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}