package com.shows_lesdominik

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.LayoutInflater
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.shows_lesdominik.databinding.ActivityShowDetailsBinding
import com.shows_lesdominik.databinding.ActivityShowsBinding
import com.shows_lesdominik.databinding.DialogAddReviewBinding

class ShowDetailsActivity : AppCompatActivity() {

    lateinit var binding: ActivityShowDetailsBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityShowDetailsBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.showTitle.text = intent.extras?.getString("NAME")
        intent.extras?.getInt("PICTURE")?.let { binding.fullImage.setImageResource(it) }

        binding.backArrow.setOnClickListener {
            val intent = Intent(this, ShowsActivity::class.java)
            startActivity(intent)
        }

        binding.reviewButton.setOnClickListener {
            showAddReviewBottomSheet()
        }
    }

    private fun showAddReviewBottomSheet() {
        val dialog = BottomSheetDialog(this)

        val bottomSheetBinding = DialogAddReviewBinding.inflate(layoutInflater)
        dialog.setContentView(bottomSheetBinding.root)

        bottomSheetBinding.closeButton.setOnClickListener {
            dialog.hide()
        }

        bottomSheetBinding.showRatingBar.setOnRatingBarChangeListener { ratingBar, rating, b ->
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