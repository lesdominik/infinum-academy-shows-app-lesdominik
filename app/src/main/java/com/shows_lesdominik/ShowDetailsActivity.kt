package com.shows_lesdominik

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import androidx.core.view.isVisible
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.shows_lesdominik.databinding.ActivityShowDetailsBinding
import com.shows_lesdominik.databinding.DialogAddReviewBinding
import model.Review
import ui.ReviewsAdapter

class ShowDetailsActivity : AppCompatActivity() {

    private val reviews = emptyList<Review>()

    private lateinit var binding: ActivityShowDetailsBinding
    private lateinit var adapter: ReviewsAdapter
    private lateinit var username: String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityShowDetailsBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.showTitle.text = intent.extras?.getString("NAME")
        intent.extras?.getInt("PICTURE")?.let { binding.detailsImage.setImageResource(it) }
        binding.showDetails.text = intent.extras?.getString("DETAILS")
        username = intent.extras?.getString("USERNAME").toString()

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
        adapter = ReviewsAdapter(reviews)

        binding.reviewRecycle.layoutManager = LinearLayoutManager(this)
        binding.reviewRecycle.adapter = adapter

        binding.reviewRecycle.addItemDecoration(DividerItemDecoration(this, DividerItemDecoration.VERTICAL))
    }

    private fun showAddReviewBottomSheet() {
        val dialog = BottomSheetDialog(this)

        val bottomSheetBinding = DialogAddReviewBinding.inflate(layoutInflater)
        dialog.setContentView(bottomSheetBinding.root)


        bottomSheetBinding.closeButton.setOnClickListener {
            dialog.dismiss()
        }

        bottomSheetBinding.showRatingBar.setOnRatingBarChangeListener { _, rating, _ ->
            bottomSheetBinding.submitButton.isEnabled = rating > 0
        }

        bottomSheetBinding.submitButton.setOnClickListener {
            addReviewToList(bottomSheetBinding.showRatingBar.rating.toInt(), bottomSheetBinding.commentEdiText.text.toString())
            dialog.dismiss()
        }


        dialog.show()
    }

    private fun addReviewToList(rating: Int, comment: String?) = with(binding) {
        adapter.addItem(Review(username, R.drawable.ic_person, rating, comment))
        noReviewsText.isVisible = false
        reviewVisibilityGroup.isVisible = true
        
        reviewDetails.text = getString(R.string.reviewDetails, adapter.itemCount, adapter.getAverageRating())
        reviewRatingBar.rating = adapter.getAverageRating().toFloat()
    }
}