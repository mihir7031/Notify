package com.example.notify

import android.app.ProgressDialog
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.notify.databinding.ActivityUpldNoticeBinding
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import java.util.*

class upldNotice : AppCompatActivity() {

    private lateinit var binding: ActivityUpldNoticeBinding
    private var selectImg: Uri? = null
    private lateinit var firestore: FirebaseFirestore
    private lateinit var storage: StorageReference
    private lateinit var progressDialog: ProgressDialog

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityUpldNoticeBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Initialize Firestore
        firestore = FirebaseFirestore.getInstance()

        // Initialize Firebase Storage reference
        storage = FirebaseStorage.getInstance().reference

        // Create the progress dialog
        progressDialog = ProgressDialog(this)
        progressDialog.setMessage("Uploading Image...")
        progressDialog.setCancelable(false)

        binding.addimg.setOnClickListener {
            val intent = Intent()
            intent.action = Intent.ACTION_GET_CONTENT
            intent.type = "image/*"
            startActivityForResult(intent, 1)
        }

        binding.upldnotice.setOnClickListener {
            uploadNotice()
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == 1 && resultCode == RESULT_OK && data?.data != null) {
            selectImg = data.data
            binding.upldimg.setImageURI(selectImg)
        }
    }

    private fun uploadNotice() {
        val title = binding.noticeTitle.text.toString().trim()
        val description = binding.noticeDescription.text.toString().trim()

        if (title.isEmpty() || description.isEmpty()) {
            Toast.makeText(this, "Please fill all fields", Toast.LENGTH_SHORT).show()
            return
        }

        // Show progress dialog while uploading
        progressDialog.show()

        if (selectImg != null) {
            // Upload image to Firebase Storage
            val imageRef = storage.child("images/${UUID.randomUUID()}.jpg")
            imageRef.putFile(selectImg!!)
                .addOnSuccessListener {
                    progressDialog.dismiss()
                    // Get the image URL after successful upload
                    imageRef.downloadUrl.addOnSuccessListener { uri ->
                        saveNoticeToFirestore(title, description, uri.toString())
                        clearInputFields()
                    }
                }
                .addOnFailureListener { e ->
                    progressDialog.dismiss()
                    Toast.makeText(this, "Failed to upload image: ${e.message}", Toast.LENGTH_LONG).show()
                    Log.e("FirebaseError", "Failed to upload image", e)
                }
        } else {
            // If no image is selected, just save text data
            saveNoticeToFirestore(title, description, null)
        }
    }

    private fun saveNoticeToFirestore(title: String, description: String, imageUrl: String?) {
        // Create a unique ID for the notice
        val noticeId = UUID.randomUUID().toString()

        // Create the Notice object
        val notice = Notice(noticeId, title, description, imageUrl)

        // Save the notice to Firestore under the "notices" collection
        firestore.collection("notices").document(noticeId).set(notice)
            .addOnCompleteListener { task ->
                progressDialog.dismiss()

                if (task.isSuccessful) {
                    Toast.makeText(this, "Notice uploaded successfully", Toast.LENGTH_SHORT).show()
                    clearInputFields()
                } else {
                    val exception = task.exception
                    Toast.makeText(this, "Failed to upload notice: ${exception?.message}", Toast.LENGTH_LONG).show()
                    Log.e("FirestoreError", "Failed to upload notice", exception)
                }
            }
    }

    // Clear the input fields after the upload is complete
    private fun clearInputFields() {
        binding.noticeTitle.text?.clear()
        binding.noticeDescription.text?.clear()
        binding.upldimg.setImageURI(null)
        selectImg = null
    }

}
