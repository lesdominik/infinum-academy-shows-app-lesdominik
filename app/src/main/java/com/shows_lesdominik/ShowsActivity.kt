package com.shows_lesdominik

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import com.shows_lesdominik.databinding.ActivityShowsBinding
import model.Show
import ui.ShowsAdapter

class ShowsActivity : AppCompatActivity() {

    private val shows = listOf(
        Show("The Office", R.drawable.the_office),
        Show("Stranger Things", R.drawable.stranger_things),
        Show("Krv nije voda", R.drawable.krv_nije_voda)
    )

    private lateinit var binding: ActivityShowsBinding
    private lateinit var adapter: ShowsAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityShowsBinding.inflate(layoutInflater)
        setContentView(binding.root)

        initShowsRecycler()
    }

    private fun initShowsRecycler() {
        adapter = ShowsAdapter(shows) { show ->
            Toast.makeText(this, show.name, Toast.LENGTH_SHORT).show()
        }

        binding.showsRecycler.layoutManager = LinearLayoutManager(this)

        binding.showsRecycler.adapter = adapter
    }
}