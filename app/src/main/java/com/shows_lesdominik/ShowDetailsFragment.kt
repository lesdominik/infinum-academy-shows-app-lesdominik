package com.shows_lesdominik

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.shows_lesdominik.databinding.DialogAddReviewBinding
import com.shows_lesdominik.databinding.FragmentShowDetailsBinding
import com.shows_lesdominik.databinding.FragmentShowsBinding
import model.Review
import ui.ReviewsAdapter

class ShowDetailsFragment : Fragment() {

    private val reviews = emptyList<Review>()

    private var _binding: FragmentShowDetailsBinding? = null
    private val binding get() =_binding!!

    private lateinit var adapter: ReviewsAdapter
    private lateinit var username: String
    private val args by navArgs<ShowDetailsFragmentArgs>()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentShowDetailsBinding.inflate(inflater,container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.showTitle.text = args.showName
        binding.detailsImage.setImageResource(args.pictureId)
        binding.showDetails.text = args.details

        username = args.username

        initReviewsRecycler()
        initListeners()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }


    private fun initReviewsRecycler() {
        adapter = ReviewsAdapter(reviews)

        binding.reviewRecycle.layoutManager = LinearLayoutManager(context)
        binding.reviewRecycle.adapter = adapter

        binding.reviewRecycle.addItemDecoration(DividerItemDecoration(context, DividerItemDecoration.VERTICAL))
    }

    private fun initListeners() {
        binding.reviewButton.setOnClickListener {
            showAddReviewBottomSheet()
        }

        binding.backArrow.setOnClickListener {
            findNavController().popBackStack()
        }
    }

    private fun showAddReviewBottomSheet() {
        val dialog = BottomSheetDialog(context!!)

        val bottomSheetBinding = DialogAddReviewBinding.inflate(layoutInflater)
        dialog.setContentView(bottomSheetBinding.root)


        bottomSheetBinding.closeButton.setOnClickListener {
            dialog.dismiss()
        }

        bottomSheetBinding.showRatingBar.setOnRatingBarChangeListener { _, rating, _ ->
            bottomSheetBinding.submitButton.isEnabled = when {
                rating > 0 -> true
                else -> false
            }
        }

        bottomSheetBinding.submitButton.setOnClickListener {
            addReviewToList(bottomSheetBinding.showRatingBar.rating.toInt(), bottomSheetBinding.commentEdiText.text.toString())
            dialog.dismiss()
        }

        dialog.show()
    }

    private fun addReviewToList(rating: Int, comment: String?) {
        adapter.addItem(Review(username, R.drawable.ic_person, rating, comment))
        binding.noReviewsText.isVisible = false
        binding.reviewRecycle.isVisible = true
        binding.reviewDetails.isVisible = true
        binding.reviewRatingBar.isVisible = true

        binding.reviewDetails.text = "${adapter.itemCount} reviews, ${adapter.getAverageRating()} average"
        binding.reviewRatingBar.rating = adapter.getAverageRating().toFloat()
    }

}