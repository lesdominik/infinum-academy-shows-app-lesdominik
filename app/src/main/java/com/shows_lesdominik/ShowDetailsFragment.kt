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

//        viewModel.setShowDetails(args.showName, args.pictureId, args.details)

        viewModel.showDetailsLiveData.observe(viewLifecycleOwner) { show ->
            binding.showTitle.text = show.title
            binding.detailsImage.setImageURI(Uri.parse(show.imageUrl))
            binding.showDetails.text = show.description
        }

        viewModel.getUsername(args.userEmail)

        initReviewsRecycler()
        initListeners()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }


    private fun initReviewsRecycler() {
        viewModel.reviewsLiveData.observe(viewLifecycleOwner) { reviews ->
            adapter = ReviewsAdapter(reviews)

            binding.reviewRecycle.layoutManager = LinearLayoutManager(requireContext())
            binding.reviewRecycle.adapter = adapter

            binding.reviewRecycle.addItemDecoration(DividerItemDecoration(requireContext(), DividerItemDecoration.VERTICAL))
        }
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
        val dialog = BottomSheetDialog(requireContext())

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
        var userImageUri = sharedPreferences.getString("URI", null)
        if (userImageUri.isNullOrEmpty()) {
            userImageUri = "android.resource://com.shows_lesdominik/" + R.drawable.default_user
        }
        viewModel.usernameLiveData.observe(viewLifecycleOwner) { username ->
            adapter.addItem(Review(username, userImageUri, rating, comment))
        }
        binding.noReviewsText.isVisible = false
        binding.reviewRecycle.isVisible = true
        binding.reviewDetails.isVisible = true
        binding.reviewRatingBar.isVisible = true

        binding.reviewDetails.text = "${adapter.itemCount} reviews, ${adapter.getAverageRating()} average"
        binding.reviewRatingBar.rating = adapter.getAverageRating().toFloat()
    }

}