package com.example.notify

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.notify.databinding.ItemDoucumentBinding

class DocumentAdapter(
    private val context: Context,
    private var documentList: List<String> // List of file URLs (PDF links)
) : RecyclerView.Adapter<DocumentAdapter.DocumentViewHolder>() {

    // Called when a new ViewHolder is created
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): DocumentViewHolder {
        // Inflate the custom item layout for each document using ViewBinding
        val binding = ItemDoucumentBinding.inflate(LayoutInflater.from(context), parent, false)
        return DocumentViewHolder(binding)
    }

    // Called to bind data to each item in the RecyclerView
    override fun onBindViewHolder(holder: DocumentViewHolder, position: Int) {
        val documentUrl = documentList[position]
        val fileName = documentUrl.substring(documentUrl.lastIndexOf("/") + 1)

        // Set the document name in the TextView
        holder.binding.documentName.text = fileName

        // Set an onClickListener to open the document when clicked
        holder.itemView.setOnClickListener {
            val fileUri = Uri.parse(documentUrl)
            openDocument(fileUri)
        }
    }

    // Return the total number of items in the document list
    override fun getItemCount(): Int = documentList.size

    // Opens the PDF document with the appropriate intent
    private fun openDocument(uri: Uri) {
        val intent = Intent(Intent.ACTION_VIEW)
        intent.setDataAndType(uri, "application/pdf")
        intent.flags = Intent.FLAG_ACTIVITY_NO_HISTORY
        context.startActivity(Intent.createChooser(intent, "Open PDF with"))
    }

    // This method updates the document list and notifies the adapter to refresh the view
    fun setDocuments(documents: List<String>) {
        this.documentList = documents
        notifyDataSetChanged()
    }

    // ViewHolder for each item in the RecyclerView
    class DocumentViewHolder(val binding: ItemDoucumentBinding) : RecyclerView.ViewHolder(binding.root)
}
