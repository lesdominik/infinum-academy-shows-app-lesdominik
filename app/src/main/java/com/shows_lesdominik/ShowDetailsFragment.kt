package com.shows_lesdominik

import android.content.Context
import android.content.SharedPreferences
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.content.edit
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
    private val viewModel: ShowDetailsViewModel by viewModels {
        ShowDetailsViewModelFactory((activity?.application as ShowsApplication).database)
    }

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

        initShowDetails()
        initReviewsRecycler()
        initListeners()
    }

    private fun initShowDetails() {
        if (InternetConnectionUtil.isConnected(requireContext())) {
            fetchShowDetailsFromApi()
        } else {
            loadShowDetailsFromDatabase()
        }
    }

    private fun fetchShowDetailsFromApi() {
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
    }

    private fun loadShowDetailsFromDatabase() {
        viewModel.getShowDetailsFromDatabase(args.showId).observe(viewLifecycleOwner) { showEntity ->
            binding.showDetailsToolbar.title = showEntity.title
            Glide.with(requireContext()).load(showEntity.imageUrl).into(binding.detailsImage)
            binding.showDetails.text = showEntity.description

            if (showEntity.averageRating != null) {
                binding.reviewDetails.text = getString(R.string.reviewDetails, showEntity.noOfReviews, showEntity.averageRating)
                binding.reviewRatingBar.rating = showEntity.averageRating
            }

            binding.loadingShowDetails.isVisible = false
            binding.showDetailsGroup.isVisible = true
        }
    }

    private fun initReviewsRecycler() {
        if (InternetConnectionUtil.isConnected(requireContext())) {
           fetchReviewsFromApi()
        } else {
            loadReviewsFromDatabase()
        }

        viewModel.newReviewLiveData.observe(viewLifecycleOwner) { review ->
            if (review != null) {
                adapter.addItem(review)

                binding.reviewDetails.text = getString(R.string.reviewDetails, viewModel.getNoOfReviews(), viewModel.getShowAvgRating())
            }
        }
    }

    private fun fetchReviewsFromApi() {
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
    }

    private fun loadReviewsFromDatabase() {
        viewModel.getReviewsFromDatabase(args.showId).observe(viewLifecycleOwner) { reviewsEntity ->
            if (reviewsEntity.isEmpty()) {
                binding.noReviewsText.isVisible = true
                binding.reviewsGroup.isVisible = false
            } else {
                binding.noReviewsText.isVisible = false
                binding.reviewsGroup.isVisible = true

                adapter = ReviewsAdapter(reviewsEntity.map { reviewEntity ->
                    Review(
                        reviewEntity.id,
                        reviewEntity.comment,
                        reviewEntity.rating,
                        reviewEntity.showId,
                        User(reviewEntity.userId, reviewEntity.userEmail, reviewEntity.userImageUrl)
                    )
                })
                binding.reviewRecycle.adapter = adapter
                binding.reviewRecycle.layoutManager = LinearLayoutManager(requireContext())

                binding.reviewRecycle.addItemDecoration(DividerItemDecoration(requireContext(), DividerItemDecoration.VERTICAL))
            }
        }
    }

    private fun initListeners() {
        binding.reviewButton.setOnClickListener {
            if (InternetConnectionUtil.isConnected(requireContext())) {
                showAddReviewBottomSheet()
            } else {
                Toast.makeText(requireContext(), getString(R.string.no_internet), Toast.LENGTH_SHORT).show()
            }
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