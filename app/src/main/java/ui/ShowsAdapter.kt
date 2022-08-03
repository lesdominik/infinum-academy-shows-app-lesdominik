package ui

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.shows_lesdominik.databinding.ViewShowItemBinding
import model.Show

class ShowsAdapter(
    private var items: List<Show>,
    private val onItemClickCallback: (Show) -> Unit
) : RecyclerView.Adapter<ShowsAdapter.ShowViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ShowViewHolder {
        val binding = ViewShowItemBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return  ShowViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ShowViewHolder, position: Int) {
        holder.bind(items[position])
    }

    override fun getItemCount() = items.count()

    inner class ShowViewHolder(private val binding: ViewShowItemBinding) : RecyclerView.ViewHolder(binding.root) {

        fun bind(item: Show) = with(binding) {
            showItemTitle.text = item.name
            showItemImage.setImageResource(item.imageResourceId)
            showItemDescription.text = item.description

            cardContainer.setOnClickListener {
                onItemClickCallback(item)
            }
        }
    }
}