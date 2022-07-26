package ui

import android.net.Uri
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.recyclerview.widget.RecyclerView
import com.shows_lesdominik.databinding.ItemReviewBinding
import model.Review
import java.text.DecimalFormat

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

    fun getAverageRating(): String {
        var sum: Int = 0
        for (item in items) {
            sum += item.rating
        }
        return "%.2f".format(sum.toDouble()/items.count().toDouble())
    }

    fun addItem(review: Review) {
        items = items + review
        notifyItemInserted(items.lastIndex)
    }

    inner class ReviewViewHolder(private val binding: ItemReviewBinding) : RecyclerView.ViewHolder(binding.root) {

        fun bind(item: Review) {
            binding.userName.text = item.user
            binding.userImage.setImageURI(Uri.parse(item.userImageUriString))
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