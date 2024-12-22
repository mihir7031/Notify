package com.example.notify

import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import com.bumptech.glide.Glide
import com.example.notify.databinding.ActivitySignUpBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import java.util.*

class signUp : AppCompatActivity() {
    private lateinit var binding: ActivitySignUpBinding
    private lateinit var firebaseAuth: FirebaseAuth
    private lateinit var firestore: FirebaseFirestore
    private val storage = FirebaseStorage.getInstance().reference

    private var profileImgUri: Uri? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        // Initialize Firebase Authentication and Firestore
        firebaseAuth = FirebaseAuth.getInstance()
        firestore = FirebaseFirestore.getInstance()
        binding = ActivitySignUpBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Click listener to select a profile image
        binding.profileImage.setOnClickListener {
            val intent = Intent(Intent.ACTION_GET_CONTENT)
            intent.type = "image/*"
            startActivityForResult(intent, 1)
        }

        // Set up the click listener for the sign-up button
        binding.signupbtn.setOnClickListener {
            val username = binding.name.text.toString().trim()
            val email = binding.EmailAddress.text.toString().trim()
            val password = binding.Password.text.toString().trim()
            val stdid = binding.studentId.text.toString().trim()

            if (validInput(username, email, password, stdid)) {
                performSignUp(username, email, password, stdid)
            }
        }
    }

    // Validate user input
    private fun validInput(username: String, email: String, password: String, stdid: String): Boolean {
        if (username.isEmpty()) {
            binding.name.error = "Enter Username"
            return false
        }
        if (email.isEmpty()) {
            binding.EmailAddress.error = "Enter Email"
            return false
        }
        if (!android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            binding.EmailAddress.error = "Enter a valid email address"
            return false
        }
        if (password.isEmpty() || password.length < 8) {
            binding.Password.error = "Password must be at least 8 characters"
            return false
        }
        if (stdid.isEmpty()) {
            binding.studentId.error = "Enter the Student ID"
            return false
        }
        if (profileImgUri == null) {
            Toast.makeText(this, "Please select a profile image", Toast.LENGTH_SHORT).show()
            return false
        }
        return true
    }

    // Perform sign-up with Firebase Authentication and store additional data in Firestore
    private fun performSignUp(username: String, email: String, password: String, stdid: String) {
        firebaseAuth.createUserWithEmailAndPassword(email, password)
            .addOnCompleteListener(this) { task ->
                if (task.isSuccessful) {
                    uploadProfileImage(username, email, stdid)
                } else {
                    Toast.makeText(this, "Signup failed: ${task.exception?.message}", Toast.LENGTH_LONG).show()
                }
            }
    }

    // Upload profile image to Firebase Storage
    private fun uploadProfileImage(username: String, email: String, stdid: String) {
        val userId = firebaseAuth.currentUser?.uid ?: return
        val profileRef = storage.child("profileImages/$userId.jpg")

        profileImgUri?.let { uri ->
            profileRef.putFile(uri)
                .addOnSuccessListener {
                    profileRef.downloadUrl.addOnSuccessListener { downloadUrl ->
                        saveUserToFirestore(username, email, stdid, downloadUrl.toString())
                    }
                }
                .addOnFailureListener {
                    Toast.makeText(this, "Failed to upload profile image", Toast.LENGTH_SHORT).show()
                }
        }
    }

    // Save user details to Firestore
    private fun saveUserToFirestore(username: String, email: String, stdid: String, profileImageUrl: String) {
        val userId = firebaseAuth.currentUser?.uid ?: return
        val userMap = hashMapOf(
            "name" to username,
            "studentId" to stdid,
            "email" to email,
            "profileImage" to profileImageUrl
        )

        firestore.collection("users").document(userId)
            .set(userMap)
            .addOnSuccessListener {
                Toast.makeText(this, "User registered successfully", Toast.LENGTH_SHORT).show()
                val intent = Intent(this, dashboard::class.java)
                intent.putExtra("LOGIN_NAME", username)
                intent.putExtra("ID", stdid)
                startActivity(intent)
                finish()
            }
            .addOnFailureListener { e ->
                Toast.makeText(this, "Failed to save user data: ${e.message}", Toast.LENGTH_LONG).show()
            }
    }

    // Handle the result from selecting a profile image
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == 1 && resultCode == Activity.RESULT_OK && data != null) {
            profileImgUri = data.data
            // Display the selected image in the ImageView
            Glide.with(this).load(profileImgUri).into(binding.profileImage)
        }
    }
}
