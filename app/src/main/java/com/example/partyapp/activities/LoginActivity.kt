package com.example.partyapp.activities

import android.content.Intent
import android.os.Bundle
import android.text.TextUtils
import android.util.Log
import android.widget.Button
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.partyapp.R
import com.example.partyapp.databinding.ActivityLoginBinding
import com.google.android.gms.common.SignInButton
import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.auth

private const val TAG = "EmailPassword"
class LoginActivity : AppCompatActivity() {
    private lateinit var auth: FirebaseAuth

    private lateinit var binding: ActivityLoginBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val emailSignInButton: Button = findViewById(R.id.loginButton)
        val signUpButton: Button = findViewById(R.id.signUpButton)
        val googleSignInButton: SignInButton = findViewById(R.id.signInButton)

        // Buttons
        emailSignInButton.setOnClickListener {
            val email = binding.fieldTextEmailAddress.text.toString()
            val password = binding.fieldTextPassword.text.toString()
            signIn(email, password)
        }

        signUpButton.setOnClickListener {
            startActivity(Intent(this@LoginActivity, SignUpActivity::class.java))
            finish()
        }

        auth = Firebase.auth
    }

    private fun signIn(email: String, password: String) {
        Log.d(TAG, "signIn:$email")
        if (!validateForm()) {
            return
        }
        auth.signInWithEmailAndPassword(email, password)
            .addOnCompleteListener(this) { task ->
                if (task.isSuccessful) {
                    // Sign in success, update UI with the signed-in user's information
                    Log.d(TAG, "signInWithEmail:success")
                    updateUI()
                } else {
                    // If sign in fails, display a message to the user.
                    Log.w(TAG, "signInWithEmail:failure", task.exception)
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

        val email = binding.fieldTextEmailAddress.text.toString()
        if (TextUtils.isEmpty(email)) {
            binding.fieldTextEmailAddress.error = "Required."
            valid = false
        } else {
            binding.fieldTextEmailAddress.error = null
        }

        val password = binding.fieldTextPassword.text.toString()
        if (TextUtils.isEmpty(password)) {
            binding.fieldTextPassword.error = "Required."
            valid = false
        } else {
            binding.fieldTextPassword.error = null
        }

        return valid
    }

    private fun updateUI() {
        val intent = Intent(this, MainActivity::class.java)
        intent.putExtra("finish", true)
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP) // To clean up all activities
        startActivity(intent)
        Toast.makeText(
            this,
            "Authentication Success.",
            Toast.LENGTH_SHORT,
        ).show()
        finish()
    }
}