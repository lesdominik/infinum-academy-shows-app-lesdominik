package com.shows_lesdominik

import android.net.Uri
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.shows_lesdominik.databinding.ItemReviewBinding

class ReviewsAdapter(
    private var items: List<Review>
) : RecyclerView.Adapter<ReviewsAdapter.ReviewViewHolder>(){

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ReviewViewHolder {
        val binding = ItemReviewBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ReviewViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ReviewViewHolder, position: Int) {
        holder.bind(items[position])
    }

    override fun getItemCount() = items.count()

    fun addItem(review: Review) {
        items = listOf<Review>(review) + items
        notifyItemInserted(0)
    }

    inner class ReviewViewHolder(private val binding: ItemReviewBinding) : RecyclerView.ViewHolder(binding.root) {

        fun bind(item: Review) {
            if (item.user.imageUrl.isNullOrEmpty()) {
                binding.userImage.setImageResource(R.drawable.default_user)
            } else {
                Glide.with(binding.root).load(item.user.imageUrl).into(binding.userImage)
            }

            val splitEmail = item.user.email.split("@")
            binding.userName.text = splitEmail[0]
            binding.showRating.text = item.rating.toString()

            if (item.comment.isNullOrEmpty()) {
                binding.commentText.isVisible = false
            } else {
                binding.commentText.isVisible = true
                binding.commentText.text = item.comment
            }
        }
    }
}