package ui

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.shows_lesdominik.databinding.ItemReviewBinding
import model.Rating

class RatingsAdapter(
    private var items: List<Rating>,

) : RecyclerView.Adapter<RatingsAdapter.ReviewViewHolder>(){

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ReviewViewHolder {
        val binding = ItemReviewBinding.inflate(LayoutInflater.from(parent.context))
        return ReviewViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ReviewViewHolder, position: Int) {
        holder.bind(items[position])
    }

    override fun getItemCount() = items.count()

    inner class ReviewViewHolder(private val binding: ItemReviewBinding) : RecyclerView.ViewHolder(binding.root) {

        fun bind(item: Rating) {
            binding.userName.text = item.user
            binding.userImage.setImageResource(item.profileImageResourceId)
            binding.commentText.text = item.comment
        }
    }
}