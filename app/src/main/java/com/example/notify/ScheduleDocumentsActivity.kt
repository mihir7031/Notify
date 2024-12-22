package com.example.notify

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference

class ScheduleDocumentsActivity : AppCompatActivity() {

    private lateinit var documentsRecyclerView: RecyclerView
    private lateinit var firebaseStorage: FirebaseStorage
    private lateinit var storageReference: StorageReference
    private val documentList = mutableListOf<String>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_schedule_documents)

        // Initialize Firebase Storage
        firebaseStorage = FirebaseStorage.getInstance()
        storageReference = firebaseStorage.reference

        documentsRecyclerView = findViewById(R.id.documentsRecyclerView)

        // Set up RecyclerView with adapter
        val layoutManager = LinearLayoutManager(this)
        documentsRecyclerView.layoutManager = layoutManager

        // Fetch documents from Firebase Storage
        fetchDocumentsFromFirebase()
    }

    // Fetch the list of documents from Firebase Storage
    private fun fetchDocumentsFromFirebase() {
        // Reference to the 'documents' folder in Firebase Storage
        val documentsReference = storageReference.child("documents")

        // List all the files in the folder
        documentsReference.listAll()
            .addOnSuccessListener { result ->
                // Loop through the files in Firebase Storage and get the file URLs
                for (item in result.items) {
                    val documentUrl = item.downloadUrl.toString()
                    documentList.add(documentUrl)
                }

                // Set the adapter to display documents
                val adapter = DocumentAdapter(this, documentList)
                documentsRecyclerView.adapter = adapter
            }
            .addOnFailureListener { e ->
                Toast.makeText(this, "Failed to fetch documents: ${e.message}", Toast.LENGTH_LONG).show()
            }
    }
}
