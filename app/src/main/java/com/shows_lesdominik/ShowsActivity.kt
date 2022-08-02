package com.shows_lesdominik

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.core.view.isVisible
import androidx.recyclerview.widget.LinearLayoutManager
import com.shows_lesdominik.databinding.ActivityShowsBinding
import model.Show
import ui.ShowsAdapter

class ShowsActivity : AppCompatActivity() {

    private val shows = listOf(
        Show("theOffice","The Office", R.drawable.the_office),
        Show("strangerThings","Stranger Things", R.drawable.stranger_things),
        Show("krvNijeVoda","Krv nije voda", R.drawable.krv_nije_voda)
    )

    private lateinit var binding: ActivityShowsBinding
    private lateinit var adapter: ShowsAdapter
    private lateinit var username: String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityShowsBinding.inflate(layoutInflater)
        setContentView(binding.root)

        username = intent.extras?.getString("USERNAME").toString()
        initShowsRecycler()

        binding.toggleButton.addOnButtonCheckedListener { _, _, isChecked ->
            if (isChecked) {
                binding.showsRecycler.isVisible = false
                binding.noShowsView.isVisible = true
            } else {
                binding.showsRecycler.isVisible = true
                binding.noShowsView.isVisible = false
            }
        }
    }

    private fun initShowsRecycler() {
        adapter = ShowsAdapter(shows) { show ->
            val intent = Intent(this, ShowDetailsActivity::class.java).apply {
                putExtra("USERNAME", username)
                putExtra("NAME", show.name)
                putExtra("PICTURE", show.imageResourceId)
                putExtra("DETAILS", show.description)
            }
            startActivity(intent)
        }

        binding.showsRecycler.layoutManager = LinearLayoutManager(this)

        binding.showsRecycler.adapter = adapter
    }
}