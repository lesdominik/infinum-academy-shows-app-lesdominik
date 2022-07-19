package com.shows_lesdominik

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
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
            when {
                isChecked -> {
                    binding.showsRecycler.visibility = android.view.View.GONE
                    binding.noShowsView.visibility = android.view.View.VISIBLE
                }
                else -> {
                    binding.showsRecycler.visibility = android.view.View.VISIBLE
                    binding.noShowsView.visibility = android.view.View.GONE
                }
            }
        }
    }

    private fun initShowsRecycler() {
        adapter = ShowsAdapter(shows) { show ->
            val intent = Intent(this, ShowDetailsActivity::class.java)
            intent.putExtra("USERNAME", username)
            intent.putExtra("NAME", show.name)
            intent.putExtra("PICTURE", R.drawable.the_office_full)
            intent.putExtra("DETAILS", show.description)
            startActivity(intent)
        }

        binding.showsRecycler.layoutManager = LinearLayoutManager(this)

        binding.showsRecycler.adapter = adapter
    }
}