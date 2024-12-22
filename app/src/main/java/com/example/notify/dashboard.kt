package com.example.notify

import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import com.bumptech.glide.Glide
import com.example.notify.databinding.ActivityDashboardBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import java.util.*

class dashboard : AppCompatActivity() {

    private lateinit var profileImgUri: Uri
    private lateinit var auth: FirebaseAuth
    private val firestore = FirebaseFirestore.getInstance()
    private val storage = FirebaseStorage.getInstance().reference

    private val binding: ActivityDashboardBinding by lazy {
        ActivityDashboardBinding.inflate(layoutInflater)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(binding.root)

        auth = FirebaseAuth.getInstance()

        // Fetch and display student name, ID, and profile image
        loadUserProfile()

        // Set up click listeners for navigation buttons
        binding.notice.setOnClickListener {
            startActivity(Intent(this, upldNotice::class.java))
        }

        binding.logout.setOnClickListener {
            auth.signOut()
            startActivity(Intent(this, login::class.java))
            finish()
        }

        binding.Notifications.setOnClickListener {
            startActivity(Intent(this, Notification::class.java))
        }

        binding.updatenotice.setOnClickListener {
            startActivity(Intent(this, EditNotice::class.java))
        }


        binding.semesterSchedule.setOnClickListener {
            startActivity(Intent(this, ScheduleDocumentsActivity::class.java))
        }

        binding.calender.setOnClickListener {
            startActivity(Intent(this, calender::class.java))
        }
        // Allow user to select a new profile image
        binding.profileImage.setOnClickListener {
            val intent = Intent(Intent.ACTION_GET_CONTENT)
            intent.type = "image/*"
            startActivityForResult(intent, 1)
        }
    }

    // Load student name, ID, and profile image from Firestore
    private fun loadUserProfile() {
        val userId = auth.currentUser?.uid

        if (userId != null) {
            firestore.collection("users").document(userId).get()
                .addOnSuccessListener { document ->
                    if (document.exists()) {
                        val name = document.getString("name") ?: "User"
                        val studentId = document.getString("studentId") ?: "Unknown"
                        val profileUrl = document.getString("profileImage")

                        // Set name and student ID in TextViews
                        binding.profilename.text = name
                        binding.studentid.text = studentId

                        // Load profile image using Glide
                        if (profileUrl != null) {
                            Glide.with(this)
                                .load(profileUrl)
                                .placeholder(R.drawable.no_image)
                                .into(binding.profileImage)
                        } else {
                            binding.profileImage.setImageResource(R.drawable.logo)
                        }
                    } else {
                        Toast.makeText(this, "User profile not found", Toast.LENGTH_SHORT).show()
                    }
                }
                .addOnFailureListener {
                    Toast.makeText(this, "Failed to load user details", Toast.LENGTH_SHORT).show()
                }
        }
    }

    // Handle the result from selecting a profile image
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == 1 && resultCode == Activity.RESULT_OK && data != null) {
            profileImgUri = data.data!!

            // Update profile image in ImageView
            binding.profileImage.setImageURI(profileImgUri)

            // Upload the selected image to Firebase Storage and update Firestore
            uploadProfileImage()
        }
    }

    // Upload the selected profile image to Firebase Storage
    private fun uploadProfileImage() {
        val userId = auth.currentUser?.uid ?: return
        val profileRef = storage.child("profileImages/$userId.jpg")

        profileRef.putFile(profileImgUri)
            .addOnSuccessListener { taskSnapshot ->
                // Get the download URL of the uploaded image
                profileRef.downloadUrl.addOnSuccessListener { uri ->
                    updateProfileImageInFirestore(uri.toString())
                }
            }
            .addOnFailureListener {
                Toast.makeText(this, "Failed to upload profile image", Toast.LENGTH_SHORT).show()
            }
    }

    // Update the profile image URL in Firestore
    private fun updateProfileImageInFirestore(profileImageUrl: String) {
        val userId = auth.currentUser?.uid ?: return

        firestore.collection("users").document(userId)
            .update("profileImage", profileImageUrl)
            .addOnSuccessListener {
                Toast.makeText(this, "Profile image updated", Toast.LENGTH_SHORT).show()
            }
            .addOnFailureListener {
                Toast.makeText(this, "Failed to update profile image in Firestore", Toast.LENGTH_SHORT).show()
            }
    }
}
