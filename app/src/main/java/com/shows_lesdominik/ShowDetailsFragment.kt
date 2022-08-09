package com.shows_lesdominik

import android.content.Context
import android.content.SharedPreferences
import android.net.Uri
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.fragment.app.viewModels
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

    private var _binding: FragmentShowDetailsBinding? = null
    private val binding get() =_binding!!

    private lateinit var adapter: ReviewsAdapter
    private val args by navArgs<ShowDetailsFragmentArgs>()

    private lateinit var sharedPreferences: SharedPreferences

    private val viewModel by viewModels<ShowDetailsViewModel>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        sharedPreferences = requireContext().getSharedPreferences("MyPreferences", Context.MODE_PRIVATE)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentShowDetailsBinding.inflate(inflater,container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        viewModel.setShowDetails(args.showName, args.pictureId, args.details)

        viewModel.showDetailsLiveData.observe(viewLifecycleOwner) { show ->
            binding.showDetailsToolbar.title = show.name
            binding.detailsImage.setImageResource(show.imageResourceId)
            binding.showDetails.text = show.description
        }

        initReviewsRecycler()
        initListeners()
    }

    private fun initReviewsRecycler() {
        viewModel.reviewsLiveData.observe(viewLifecycleOwner) { reviews ->
            adapter = ReviewsAdapter(reviews)

            binding.reviewRecycle.layoutManager = LinearLayoutManager(requireContext())
            binding.reviewRecycle.adapter = adapter

            binding.reviewRecycle.addItemDecoration(DividerItemDecoration(requireContext(), DividerItemDecoration.VERTICAL))
        }

        viewModel.createdReviewLiveData.observe(viewLifecycleOwner) { review ->
            adapter.addItem(review)
        }
    }

    private fun initListeners() {
        binding.reviewButton.setOnClickListener {
            showAddReviewBottomSheet()
        }

        binding.showDetailsToolbar.setNavigationOnClickListener {
            findNavController().popBackStack()
        }
    }

    private fun showAddReviewBottomSheet() {
        val dialog = BottomSheetDialog(requireContext())

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
        viewModel.createReview(sharedPreferences, args.userEmail, rating, comment)

        noReviewsText.isVisible = false
        reviewVisibilityGroup.isVisible = true

        reviewDetails.text = "${adapter.itemCount} reviews, ${adapter.getAverageRating()} average"
        reviewRatingBar.rating = adapter.getAverageRating().toFloat()
    }


    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}