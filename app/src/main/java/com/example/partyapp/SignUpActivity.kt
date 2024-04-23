package com.example.partyapp

import android.app.DatePickerDialog
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.text.InputType
import android.text.TextUtils
import android.util.Log
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import com.example.partyapp.databinding.ActivitySignupBinding
import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.auth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.database
import java.time.LocalDateTime
import java.time.Period
import java.util.Calendar

private const val TAG = "EmailPassword"

@RequiresApi(Build.VERSION_CODES.TIRAMISU)
class SignUpActivity : AppCompatActivity() {
    private lateinit var auth: FirebaseAuth
    private lateinit var database: DatabaseReference

    private lateinit var binding: ActivitySignupBinding
    var y = 0
    var m = 0
    var d = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySignupBinding.inflate(layoutInflater)
        setContentView(binding.root)

        database = Firebase.database.reference
//        val googleSignUpButton: SignInButton = findViewById(R.id.signInButton)

        binding.fieldTextDob.inputType = InputType.TYPE_NULL;
        binding.fieldTextDob.setOnClickListener {
            calendar()
        }
        binding.fieldTextDob.setOnFocusChangeListener { _, _ ->
            calendar()
        }

        val signUpButton = binding.signUpButton
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
                    val firstName = binding.fieldTextName.text.toString()
                    val lastName = binding.fieldTextLastName.text.toString()
                    val dateOfBirth = binding.fieldTextDob.text.toString()
                    auth.currentUser?.let { writeAccount(it.uid, firstName, lastName, dateOfBirth) }
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

        val dob = binding.fieldTextDob.text.toString()
        if (TextUtils.isEmpty(dob)) {
            binding.fieldTextDob.error = "Required."
            valid = false
        } else {
            binding.fieldTextDob.error = null
        }

        return valid
    }

    private fun calendar() {
        val c = Calendar.getInstance()

        val year = c.get(Calendar.YEAR)
        val month = c.get(Calendar.MONTH)
        val day = c.get(Calendar.DAY_OF_MONTH)

        val datePickerDialog = DatePickerDialog(
            this,
            { _, year, monthOfYear, dayOfMonth ->
                y = year
                m = monthOfYear
                d = dayOfMonth
                binding.fieldTextDob.setText(dayOfMonth.toString() + "/" + (monthOfYear + 1) + "/" + year)
            },
            year,
            month,
            day
        )
        datePickerDialog.show()
    }

    private fun writeAccount(uid: String, firstName: String, lastName: String, dob: String, ) {
        val age = Period.between(LocalDateTime.of(y,m+1,d,0,0).toLocalDate(), LocalDateTime.now().toLocalDate()).years
        val user = UserModel(firstName,lastName,dob,auth.currentUser!!.email,"@"+firstName+lastName+uid.substring(0,3),uid,age)
        database.child("Users").child(uid).setValue(user)
    }

    private fun updateUI() {
        val intent = Intent(this, MainActivity::class.java)
        intent.putExtra("finish", true)
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP) // To clean up all activities
        startActivity(intent)
        Toast.makeText(
            this,
            "Account Creation Success.",
            Toast.LENGTH_SHORT,
        ).show()
        finish()
    }
}