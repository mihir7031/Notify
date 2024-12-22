package com.example.notify

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.notify.databinding.ActivityLoginBinding
import com.google.firebase.auth.FirebaseAuth

class login : AppCompatActivity() {

    private lateinit var firebaseAuth: FirebaseAuth
    private val binding: ActivityLoginBinding by lazy {
        ActivityLoginBinding.inflate(layoutInflater)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)

        firebaseAuth = FirebaseAuth.getInstance()

        // Login button click listener
        binding.loginbtn.setOnClickListener {
            val email = binding.username.text.toString().trim()
            val password = binding.password.text.toString().trim()

            if (validInput(email, password)) {
                performLogin(email, password)
            }
        }

        // "Don't have an account?" button click listener
        binding.donthavebtn.setOnClickListener {
            val intent = Intent(this, signUp::class.java)
            startActivity(intent)
        }
    }

    // Validate input fields
    private fun validInput(email: String, password: String): Boolean {
        if (email.isEmpty()) {
            binding.username.error = "Email cannot be empty"
            return false
        }

        if (!android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            binding.username.error = "Please enter a valid email address"
            return false
        }

        if (password.isEmpty()) {
            binding.password.error = "Password cannot be empty"
            return false
        }

        if (password.length < 8) {
            binding.password.error = "Password must be at least 8 characters"
            return false
        }

        return true
    }

    // Perform login with Firebase Authentication
    private fun performLogin(email: String, password: String) {
        firebaseAuth.signInWithEmailAndPassword(email, password)
            .addOnCompleteListener(this) { task ->
                if (task.isSuccessful) {
                    // Login successful
                    Toast.makeText(this, "Login successful", Toast.LENGTH_SHORT).show()
                    val intent = Intent(this, dashboard::class.java)
                    startActivity(intent)
                    finish()  // To prevent the user from returning to the login screen on back press
                } else {
                    // Login failed
                    Toast.makeText(this, "Login failed: ${task.exception?.message}", Toast.LENGTH_LONG).show()
                }
            }
    }
}
