package com.example.notify

import android.app.Dialog
import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.notify.databinding.ItemNoticeBinding
import com.github.chrisbanes.photoview.PhotoView

class NoticeAdapter(
    private val context: Context,
    private var noticeList: List<Notice>
) : RecyclerView.Adapter<NoticeAdapter.NoticeViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): NoticeViewHolder {
        val binding = ItemNoticeBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return NoticeViewHolder(binding)
    }

    override fun onBindViewHolder(holder: NoticeViewHolder, position: Int) {
        val notice = noticeList[position]
        holder.bind(notice)
    }

    override fun getItemCount(): Int = noticeList.size

    // Function to update the notice list
    fun setNotices(notices: List<Notice>) {
        this.noticeList = notices
        notifyDataSetChanged()
    }

    // Function to show a full-screen popup of the image
    private fun showFullScreenImage(imageUrl: String) {
        val dialog = Dialog(context)
        dialog.setContentView(R.layout.popupimg)

        val fullScreenImageView = dialog.findViewById<PhotoView>(R.id.fullScreenImage)
        Glide.with(context)
            .load(imageUrl)
            .fitCenter()
            .placeholder(R.drawable.no_image)
            .into(fullScreenImageView)

        dialog.window?.setLayout(
            ViewGroup.LayoutParams.MATCH_PARENT,
            ViewGroup.LayoutParams.MATCH_PARENT
        )
        dialog.window?.setBackgroundDrawableResource(android.R.color.transparent)
        dialog.show()
    }

    inner class NoticeViewHolder(private val binding: ItemNoticeBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(notice: Notice) {
            binding.title.text = notice.title
            binding.description.text = notice.description

            // Load and display the image if available
            if (!notice.imageUrl.isNullOrEmpty()) {
                binding.noticeImage.visibility = View.VISIBLE
                Glide.with(context)
                    .load(notice.imageUrl)
                    .fitCenter()
                    .placeholder(R.drawable.no_image)
                    .error(R.drawable.no_image)
                    .into(binding.noticeImage)

                // Show full-screen image on long press
                binding.noticeImage.setOnLongClickListener {
                    showFullScreenImage(notice.imageUrl)
                    true
                }
            } else {
                binding.noticeImage.visibility = View.GONE
            }

            // Handle item click for editing the notice
            binding.root.setOnClickListener {
                val intent = Intent(context, EditNotice::class.java).apply {
                    putExtra("NOTICE_ID", notice.id)
                    putExtra("TITLE", notice.title)
                    putExtra("DESCRIPTION", notice.description)
                    putExtra("IMAGE_URL", notice.imageUrl)
                }
                context.startActivity(intent)
            }
        }
    }
}
