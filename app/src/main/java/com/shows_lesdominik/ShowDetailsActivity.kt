package com.shows_lesdominik

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.LayoutInflater
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.shows_lesdominik.databinding.ActivityShowDetailsBinding
import com.shows_lesdominik.databinding.ActivityShowsBinding
import com.shows_lesdominik.databinding.DialogAddReviewBinding
import model.Rating
import ui.RatingsAdapter
import ui.ShowsAdapter

class ShowDetailsActivity : AppCompatActivity() {

    private val reviews = emptyList<Rating>()

    private lateinit var binding: ActivityShowDetailsBinding
    private lateinit var adapter: RatingsAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityShowDetailsBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.showTitle.text = intent.extras?.getString("NAME")
        intent.extras?.getInt("PICTURE")?.let { binding.fullImage.setImageResource(it) }
        binding.showDetails.text = intent.extras?.getString("DETAILS")

        initReviewsRecycler()

        binding.backArrow.setOnClickListener {
            val intent = Intent(this, ShowsActivity::class.java)
            startActivity(intent)
        }

        binding.reviewButton.setOnClickListener {
            showAddReviewBottomSheet()
        }
    }

    private fun initReviewsRecycler() {
//        adapter = RatingsAdapter(rating) { rating ->
//        }

        binding.reviewRecycle.layoutManager = LinearLayoutManager(this)
//        binding.reviewRecycle.adapter = adapter
    }

    private fun showAddReviewBottomSheet() {
        val dialog = BottomSheetDialog(this)

        val bottomSheetBinding = DialogAddReviewBinding.inflate(layoutInflater)
        dialog.setContentView(bottomSheetBinding.root)

        bottomSheetBinding.closeButton.setOnClickListener {
            dialog.hide()
        }

        bottomSheetBinding.showRatingBar.setOnRatingBarChangeListener { _, rating, _ ->
            when {
                rating > 0 -> {
                    bottomSheetBinding.submitButton.isEnabled = true
                }
                else -> {
                    bottomSheetBinding.submitButton.isEnabled = false
                }
            }
        }

        dialog.show()
    }
}