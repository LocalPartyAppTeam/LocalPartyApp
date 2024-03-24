package com.example.partyapp

import android.content.Intent
import android.os.Bundle
import android.text.TextUtils
import android.util.Log
import android.widget.Button
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.partyapp.databinding.ActivitySignupBinding
import com.google.android.gms.common.SignInButton
import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.auth

private const val TAG = "EmailPassword"

class SignUpActivity : AppCompatActivity() {
    private lateinit var auth: FirebaseAuth

    private lateinit var binding: ActivitySignupBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySignupBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val signUpButton: Button = findViewById(R.id.signUpButton)
//        val googleSignUpButton: SignInButton = findViewById(R.id.signInButton)

        signUpButton.setOnClickListener {
            val email = binding.fieldTextEmailAddress.text.toString()
            val password = binding.fieldTextPassword.text.toString()
            createAccount(email, password)
        }

        auth = Firebase.auth
    }

    private fun createAccount(email: String, password: String) {
        Log.d(TAG, "createAccount:$email")
        if (!validateForm()) {
            return
        }

        auth.createUserWithEmailAndPassword(email, password)
            .addOnCompleteListener(this) { task ->
                if (task.isSuccessful) {
                    // Sign in success, update UI with the signed-in user's information
                    Log.d(TAG, "createUserWithEmail:success")
                    updateUI()
                } else {
                    // If sign in fails, display a message to the user.
                    Log.w(TAG, "createUserWithEmail:failure", task.exception)
                    Toast.makeText(
                        this,
                        "Authentication failed.",
                        Toast.LENGTH_SHORT,
                    ).show()
                }
            }
    }

    private fun validateForm(): Boolean {
        var valid = true

        val name = binding.fieldTextName.text.toString()
        if (TextUtils.isEmpty(name)) {
            binding.fieldTextName.error = "Required."
            valid = false
        } else {
            binding.fieldTextName.error = null
        }

        val email = binding.fieldTextEmailAddress.text.toString()
        if (TextUtils.isEmpty(email)) {
            binding.fieldTextEmailAddress.error = "Required."
            valid = false
        } else {
            binding.fieldTextEmailAddress.error = null
        }

        val password = binding.fieldTextPassword.text.toString()
        val confirmPassword = binding.fieldTextConfirmPassword.text.toString()
        if (TextUtils.isEmpty(password) || TextUtils.isEmpty(confirmPassword)) {
            binding.fieldTextPassword.error = "Required."
            binding.fieldTextConfirmPassword.error = "Required."
            valid = false
        } else {
            binding.fieldTextPassword.error = null
            binding.fieldTextConfirmPassword.error = null
        }

        if (!TextUtils.equals(password, confirmPassword)) {
            binding.fieldTextConfirmPassword.error = "Must match password."
            valid = false
        } else {
            binding.fieldTextConfirmPassword.error = null
        }

        return valid
    }

    private fun updateUI() {
        startActivity(Intent(this, MainActivity::class.java))
        Toast.makeText(
            this,
            "Account Creation Success.",
            Toast.LENGTH_SHORT,
        ).show()
        finish()
    }
}