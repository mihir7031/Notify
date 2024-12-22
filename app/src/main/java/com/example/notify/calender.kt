package com.example.notify

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.widget.Button
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference

class calender : AppCompatActivity() {

    private lateinit var findDocumentsButton: Button
    private lateinit var uploadDocumentsButton: Button
    private lateinit var firebaseStorage: FirebaseStorage
    private lateinit var storageReference: StorageReference
    private var selectedFileUri: Uri? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_calender)

        // Initialize Firebase Storage
        firebaseStorage = FirebaseStorage.getInstance()
        storageReference = firebaseStorage.reference

        // Find views by ID
        findDocumentsButton = findViewById(R.id.findDocumentsButton)
        uploadDocumentsButton = findViewById(R.id.uploadDocumentsButton)

        // Set click listeners for the buttons
        findDocumentsButton.setOnClickListener {
            findDocuments()
        }

        uploadDocumentsButton.setOnClickListener {
            uploadDocument()
        }
    }

    // Handle finding documents (e.g., open file explorer to choose a document)
    private fun findDocuments() {
        val intent = Intent(Intent.ACTION_GET_CONTENT)
        intent.type = "application/pdf" // You can specify a specific type (e.g., PDF)
        startActivityForResult(intent, FILE_SELECT_CODE)
    }

    // Handle uploading a selected document
    private fun uploadDocument() {
        selectedFileUri?.let {
            uploadFileToFirebase(it)
        } ?: Toast.makeText(this, "No document selected", Toast.LENGTH_SHORT).show()
    }

    // Start file selection activity
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == FILE_SELECT_CODE && resultCode == RESULT_OK) {
            // Get the URI of the selected file
            selectedFileUri = data?.data

            // Show a message confirming the selection
            selectedFileUri?.let {
                Toast.makeText(this, "Document selected: ${getFileName(it)}", Toast.LENGTH_SHORT).show()
            }
        }
    }

    // Get the file name from URI
    private fun getFileName(uri: Uri): String {
        val filePath = uri.path
        return filePath?.substring(filePath.lastIndexOf("/") + 1) ?: "Unknown file"
    }

    // Upload the selected file to Firebase Storage
    private fun uploadFileToFirebase(fileUri: Uri) {
        val fileName = getFileName(fileUri)
        val fileReference = storageReference.child("documents/$fileName")

        fileReference.putFile(fileUri)
            .addOnSuccessListener {
                Toast.makeText(this, "File uploaded successfully", Toast.LENGTH_SHORT).show()
            }
            .addOnFailureListener { e ->
                Toast.makeText(this, "File upload failed: ${e.message}", Toast.LENGTH_LONG).show()
            }
    }

    companion object {
        const val FILE_SELECT_CODE = 1
    }
}
