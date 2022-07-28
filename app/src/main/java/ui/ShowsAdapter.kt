package ui

import android.net.Uri
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.shows_lesdominik.databinding.ViewShowItemBinding
import com.shows_lesdominik.Show
import com.shows_lesdominik.ShowsFragment

class ShowsAdapter(
    private var items: List<Show>,
    private val onItemClickCallback: (Show) -> Unit
) : RecyclerView.Adapter<ShowsAdapter.ShowViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ShowViewHolder {
        val binding = ViewShowItemBinding.inflate(LayoutInflater.from(parent.context))
        return  ShowViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ShowViewHolder, position: Int) {
        holder.bind(items[position])
    }

    override fun getItemCount() = items.count()

    inner class ShowViewHolder(private val binding: ViewShowItemBinding) : RecyclerView.ViewHolder(binding.root) {

        fun bind(item: Show) {
            binding.showItemTitle.text = item.title
            Glide.with(binding.root).load(item.imageUrl).into(binding.showItemImage)
            binding.showItemDescription.text = item.description

            binding.cardContainer.setOnClickListener {
//                onItemClickCallback(item)
            }
        }
    }
}