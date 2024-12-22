package com.example.notify

import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.notify.databinding.ActivityNotificationBinding
import com.google.firebase.firestore.FirebaseFirestore

class Notification : AppCompatActivity() {

    private lateinit var binding: ActivityNotificationBinding
    private lateinit var firestore: FirebaseFirestore
    private lateinit var noticeAdapter: NoticeAdapter
    private val noticeList = mutableListOf<Notice>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityNotificationBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Initialize Firestore
        firestore = FirebaseFirestore.getInstance()

        // Initialize RecyclerView and Adapter
        binding.recyclerView.layoutManager = LinearLayoutManager(this)
        noticeAdapter = NoticeAdapter(this, noticeList)
        binding.recyclerView.adapter = noticeAdapter

        // Fetch notices from Firestore
        fetchNotices()
    }

    private fun fetchNotices() {
        firestore.collection("notices")
            .get()
            .addOnSuccessListener { querySnapshot ->
                noticeList.clear()
                for (document in querySnapshot.documents) {
                    val notice = document.toObject(Notice::class.java)
                    if (notice != null) {
                        noticeList.add(notice)
                    }
                }
                noticeAdapter.setNotices(noticeList)
            }
            .addOnFailureListener { e ->
                Toast.makeText(this, "Failed to load notices: ${e.message}", Toast.LENGTH_SHORT).show()
                Log.e("FirestoreError", "Failed to load notices", e)
            }
    }
}