package com.example.partyapp

import android.content.Intent
import android.os.Bundle
import android.text.TextUtils
import android.util.Log
import android.widget.Button
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.partyapp.databinding.ActivityLoginBinding
import com.google.android.gms.common.SignInButton
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser

class SignUpActivity : AppCompatActivity() {
//    private lateinit var auth: FirebaseAuth
//
//    private lateinit var binding: ActivityLoginBinding
//
//    override fun onCreate(savedInstanceState: Bundle?) {
//        super.onCreate(savedInstanceState)
//        binding = ActivityLoginBinding.inflate(layoutInflater)
//        setContentView(binding.root)
//
//        val emailSignInButton: Button = findViewById(R.id.loginButton)
//        val signUpButton: Button = findViewById(R.id.signUpButton)
//        val googleSignInButton: SignInButton = findViewById(R.id.signInButton)
//
//        // Buttons
//        emailSignInButton.setOnClickListener {
//            val email = binding.fieldTextEmailAddress.text.toString()
//            val password = binding.fieldTextPassword.text.toString()
//            signIn(email, password)
//        }
//
//        signUpButton.setOnClickListener {
//            startActivity(Intent(this, SignUpActivity::class.java))
//            finish()
//        }
//    }
//
//    private fun signIn(email: String, password: String) {
//        Log.d(TAG, "signIn:$email")
//        if (!validateForm()) {
//            return
//        }
//        auth.signInWithEmailAndPassword(email, password)
//            .addOnCompleteListener(this) { task ->
//                if (task.isSuccessful) {
//                    // Sign in success, update UI with the signed-in user's information
//                    Log.d(TAG, "signInWithEmail:success")
//                    val user = auth.currentUser
//                    updateUI(user)
//                } else {
//                    // If sign in fails, display a message to the user.
//                    Log.w(TAG, "signInWithEmail:failure", task.exception)
//                    Toast.makeText(
//                        this,
//                        "Authentication failed.",
//                        Toast.LENGTH_SHORT,
//                    ).show()
//                }
//            }
//    }
//
//    private fun validateForm(): Boolean {
//        var valid = true
//
//        val email = binding.fieldTextEmailAddress.text.toString()
//        if (TextUtils.isEmpty(email)) {
//            binding.fieldTextEmailAddress.error = "Required."
//            valid = false
//        } else {
//            binding.fieldTextEmailAddress.error = null
//        }
//
//        val password = binding.fieldTextPassword.text.toString()
//        if (TextUtils.isEmpty(password)) {
//            binding.fieldTextPassword.error = "Required."
//            valid = false
//        } else {
//            binding.fieldTextPassword.error = null
//        }
//
//        return valid
//    }
//
//    private fun updateUI(user: FirebaseUser?) {
//        startActivity(Intent(this, MainActivity::class.java))
//        finish()
//    }
}