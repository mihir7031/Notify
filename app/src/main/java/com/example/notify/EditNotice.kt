package com.example.notify

import android.app.Activity
import android.app.Dialog
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.bumptech.glide.Glide
import com.example.notify.databinding.ActivityEditNoticeBinding
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage

class EditNotice : AppCompatActivity() {

    private lateinit var binding: ActivityEditNoticeBinding
    private lateinit var firestore: FirebaseFirestore
    private lateinit var storage: FirebaseStorage

    private var noticeId: String? = null
    private var currentImageUrl: String? = null
    private var selectedImageUri: Uri? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityEditNoticeBinding.inflate(layoutInflater)
        setContentView(binding.root)

        firestore = FirebaseFirestore.getInstance()
        storage = FirebaseStorage.getInstance()

        // Get notice ID from Intent
        noticeId = intent.getStringExtra("NOTICE_ID")
        loadNoticeDetails()

        // Set click listener to change image
        binding.changeImageText.setOnClickListener {
            selectImageFromGallery()
        }

        // Set click listener to save changes
        binding.saveButton.setOnClickListener {
            saveChanges()
        }

        // Set click listener to delete notice
        binding.deleteButton.setOnClickListener {
            deleteNotice()
        }
    }

    // Load notice details from Firestore
    private fun loadNoticeDetails() {
        noticeId?.let { id ->
            firestore.collection("notices").document(id).get()
                .addOnSuccessListener { document ->
                    val notice = document.toObject(Notice::class.java)
                    if (notice != null) {
                        binding.editTitle.setText(notice.title)
                        binding.editDescription.setText(notice.description)
                        currentImageUrl = notice.imageUrl

                        // Load the image
                        Glide.with(this)
                            .load(currentImageUrl)
                            .placeholder(R.drawable.no_image)
                            .into(binding.editImage)
                    }
                }
                .addOnFailureListener {
                    Toast.makeText(this, "Failed to load notice", Toast.LENGTH_SHORT).show()
                }
        }
    }

    // Select image from gallery
    private fun selectImageFromGallery() {
        val intent = Intent(Intent.ACTION_GET_CONTENT)
        intent.type = "image/*"
        startActivityForResult(intent, 101)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == 101 && resultCode == Activity.RESULT_OK) {
            selectedImageUri = data?.data
            binding.editImage.setImageURI(selectedImageUri)
        }
    }

    // Save changes to Firestore
    private fun saveChanges() {
        val updatedTitle = binding.editTitle.text.toString().trim()
        val updatedDescription = binding.editDescription.text.toString().trim()

        if (updatedTitle.isEmpty() || updatedDescription.isEmpty()) {
            Toast.makeText(this, "Please fill all fields", Toast.LENGTH_SHORT).show()
            return
        }

        val noticeMap = hashMapOf(
            "title" to updatedTitle,
            "description" to updatedDescription
        )

        // Update Firestore document
        noticeId?.let { id ->
            firestore.collection("notices").document(id).update(noticeMap as Map<String, Any>)
                .addOnSuccessListener {
                    Toast.makeText(this, "Notice updated", Toast.LENGTH_SHORT).show()
                    finish()
                }
                .addOnFailureListener {
                    Toast.makeText(this, "Failed to update notice", Toast.LENGTH_SHORT).show()
                }
        }
    }

    // Delete the notice
    private fun deleteNotice() {
        noticeId?.let { id ->
            firestore.collection("notices").document(id).delete()
                .addOnSuccessListener {
                    Toast.makeText(this, "Notice deleted", Toast.LENGTH_SHORT).show()
                    finish()
                }
                .addOnFailureListener {
                    Toast.makeText(this, "Failed to delete notice", Toast.LENGTH_SHORT).show()
                }
        }
    }
}
