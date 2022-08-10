package com.shows_lesdominik

import android.content.Context
import android.content.SharedPreferences
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
import com.bumptech.glide.Glide
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.shows_lesdominik.databinding.DialogAddReviewBinding
import com.shows_lesdominik.databinding.FragmentShowDetailsBinding

class ShowDetailsFragment : Fragment() {

    private var _binding: FragmentShowDetailsBinding? = null
    private val binding get() =_binding!!

    private lateinit var adapter: ReviewsAdapter
    private val args by navArgs<ShowDetailsFragmentArgs>()
    private val viewModel by viewModels<ShowDetailsViewModel>()

    private lateinit var sharedPreferences: SharedPreferences


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

        viewModel.getShowDetails(args.showId)
        viewModel.showDetailsLiveData.observe(viewLifecycleOwner) { show ->
            binding.showDetailsToolbar.title = show.title
            Glide.with(requireContext()).load(show.imageUrl).into(binding.detailsImage)
            binding.showDetails.text = show.description

            if (show.averageRating != null) {
                binding.reviewDetails.text = getString(R.string.reviewDetails, show.noOfReviews, show.averageRating)
                binding.reviewRatingBar.rating = show.averageRating
            }

            binding.loadingShowDetails.isVisible = false
            binding.showDetailsGroup.isVisible = true
        }

        initReviewsRecycler()
        initListeners()
    }

    private fun initReviewsRecycler() {
        viewModel.getShowReviews(args.showId)
        viewModel.reviewsLiveData.observe(viewLifecycleOwner) { reviews ->
            if (reviews.isEmpty()) {
                binding.noReviewsText.isVisible = true
                binding.reviewsGroup.isVisible = false
            } else {
                binding.noReviewsText.isVisible = false
                binding.reviewsGroup.isVisible = true

                adapter = ReviewsAdapter(reviews)
                binding.reviewRecycle.adapter = adapter
                binding.reviewRecycle.layoutManager = LinearLayoutManager(requireContext())

                binding.reviewRecycle.addItemDecoration(DividerItemDecoration(requireContext(), DividerItemDecoration.VERTICAL))
            }
        }

        viewModel.newReviewLiveData.observe(viewLifecycleOwner) { review ->
            if (review != null) {
                adapter.addItem(review)

                binding.reviewDetails.text = getString(R.string.reviewDetails, viewModel.getNoOfReviews(), viewModel.getShowAvgRating())
            }
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
            viewModel.addReview(bottomSheetBinding.showRatingBar.rating.toInt(), bottomSheetBinding.commentEdiText.text.toString().trim(), args.showId)
            dialog.dismiss()
        }

        dialog.show()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}