package ui

import android.opengl.Visibility
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.shows_lesdominik.databinding.ItemReviewBinding
import model.Review

class ReviewsAdapter(
    private var items: List<Review>,

    ) : RecyclerView.Adapter<ReviewsAdapter.ReviewViewHolder>(){

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ReviewViewHolder {
        val binding = ItemReviewBinding.inflate(LayoutInflater.from(parent.context))
        return ReviewViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ReviewViewHolder, position: Int) {
        holder.bind(items[position])
    }

    override fun getItemCount() = items.count()

    fun addItem(review: Review) {
        items = items + review
        notifyItemInserted(items.lastIndex)
    }

    inner class ReviewViewHolder(private val binding: ItemReviewBinding) : RecyclerView.ViewHolder(binding.root) {

        fun bind(item: Review) {
            binding.userName.text = item.user
            binding.userImage.setImageResource(item.profileImageResourceId)
            binding.showRating.text = item.rating.toString()

            if (item.comment.isNullOrEmpty()) {
                binding.commentText.visibility = android.view.View.GONE
            } else {
                binding.commentText.visibility = android.view.View.VISIBLE
                binding.commentText.text = item.comment
            }
        }
    }
}