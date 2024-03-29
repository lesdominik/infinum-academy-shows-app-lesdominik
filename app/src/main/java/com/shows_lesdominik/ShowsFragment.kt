package com.shows_lesdominik

import android.app.AlertDialog
import android.content.Context
import android.content.SharedPreferences
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.edit
import androidx.core.view.isVisible
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.shows_lesdominik.databinding.DialogUserDetailsBinding
import com.shows_lesdominik.databinding.FragmentShowsBinding
import android.net.Uri
import android.widget.Toast
import androidx.core.content.FileProvider
import androidx.lifecycle.lifecycleScope
import com.bumptech.glide.Glide
import java.io.File

class ShowsFragment : Fragment() {

    private var _binding: FragmentShowsBinding? = null
    private val binding get() =_binding!!

    private lateinit var bottomSheetBinding: DialogUserDetailsBinding

    private val args by navArgs<ShowsFragmentArgs>()
    private val viewModel: ShowsViewModel by viewModels {
        ShowsViewModelFactory((activity?.application as ShowsApplication).database)
    }

    private lateinit var sharedPreferences: SharedPreferences

    private var imageUrl: String? = null
    private var latestTmpUri: Uri? = null

    private val takeImageResult = registerForActivityResult(ActivityResultContracts.TakePicture()) { isSuccess ->
        if (isSuccess) {
            latestTmpUri?.let { uri ->
                binding.showsToolbar.setUserIconFromUri(uri)
                bottomSheetBinding.userDetailsImage.setImageURI(uri)

                FileUtil.getImageFile(requireContext())?.let {
                    viewModel.setProfileImage(it)
                }
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        sharedPreferences = requireContext().getSharedPreferences("MyPreferences", Context.MODE_PRIVATE)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentShowsBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        if (imageUrl != null) {
            binding.showsToolbar.setUserIconFromUrl(imageUrl)
        } else {
            binding.showsToolbar.setDefaultUserIcon()
        }

        if (InternetConnectionUtil.isConnected(requireContext())) {
            viewModel.getUserInfo()
            viewModel.userLiveData.observe(viewLifecycleOwner) { user ->
                if (user != null) {
                    imageUrl = user.imageUrl
                    if (!imageUrl.isNullOrEmpty()) {
                        binding.showsToolbar.setUserIconFromUrl(imageUrl)
                    }
                }
            }
        }

        initShowsRecycler()
        initListeners()
    }


    private fun initListeners() {
        binding.showsToolbar.onUserIconClick {
            showUserDetailsBottomSheet()
        }
    }

    private fun initShowsRecycler() {
        if (InternetConnectionUtil.isConnected(requireContext())) {
            fetchShowsFromApi()
        } else {
            loadShowsFromDatabase()
        }
    }

    private fun fetchShowsFromApi() {
        viewModel.getShows()
        viewModel.showsLiveData.observe(viewLifecycleOwner) { shows ->
            binding.loadingShows.isVisible = false
            if (shows.isEmpty()) {
                binding.showsRecycler.isVisible = false
                binding.noShowsView.isVisible = true
            } else {
                binding.showsRecycler.isVisible = true
                binding.noShowsView.isVisible = false

                binding.showsRecycler.adapter = ShowsAdapter(shows) { show ->
                    val directions = ShowsFragmentDirections.toFragmentShowDetails(show.id, args.userEmail)
                    findNavController().navigate(directions)
                }
                binding.showsRecycler.layoutManager = LinearLayoutManager(requireContext())
            }
        }
    }

    private fun loadShowsFromDatabase() {
        viewModel.getShowsFromDatabase().observe(viewLifecycleOwner) { shows ->
            binding.loadingShows.isVisible = false
            if (shows.isEmpty()) {
                binding.showsRecycler.isVisible = false
                binding.noShowsView.isVisible = true
            } else {
                binding.showsRecycler.isVisible = true
                binding.noShowsView.isVisible = false

                binding.showsRecycler.adapter = ShowsAdapter(shows.map { show ->
                    Show(show.id, show.averageRating, show.description, show.imageUrl, show.noOfReviews, show.title)
                }) { show ->
                    val directions = ShowsFragmentDirections.toFragmentShowDetails(show.id, args.userEmail)
                    findNavController().navigate(directions)
                }
                binding.showsRecycler.layoutManager = LinearLayoutManager(requireContext())
            }
        }
    }

    private fun showUserDetailsBottomSheet() {
        val dialog = BottomSheetDialog(requireContext())

        bottomSheetBinding = DialogUserDetailsBinding.inflate(layoutInflater)
        dialog.setContentView(bottomSheetBinding.root)

        bottomSheetBinding.userEmail.text = args.userEmail

        when {
            latestTmpUri != null -> bottomSheetBinding.userDetailsImage.setImageURI(latestTmpUri)
            imageUrl != null -> Glide.with(bottomSheetBinding.root).load(imageUrl).into(bottomSheetBinding.userDetailsImage)
            else -> bottomSheetBinding.userDetailsImage.setImageResource(R.drawable.default_user)
        }


        bottomSheetBinding.changeProfilePhoto.setOnClickListener {
            if (InternetConnectionUtil.isConnected(requireContext())) {
                takeImage()
            } else {
                Toast.makeText(requireContext(), getString(R.string.no_internet), Toast.LENGTH_SHORT).show()
            }
        }

        bottomSheetBinding.logoutButton.setOnClickListener {
            showAlertDialog()
            dialog.dismiss()
        }

        dialog.show()
    }

    private fun showAlertDialog() {
        val alertDialog = AlertDialog.Builder(requireContext())

        alertDialog.setMessage(getString(R.string.logout_alert_dialog_message))
            .setPositiveButton("Yes") { dialog, _ ->
                viewModel.setRememberMeChecked(sharedPreferences)
                findNavController().navigate(R.id.toLoginFragment)
                dialog.dismiss()
            }
            .setNegativeButton("No") { dialog, _ ->
                dialog.cancel()
            }

        alertDialog.show()
    }


    private fun takeImage() {
        lifecycleScope.launchWhenStarted {
            getTmpFileUri().let { uri ->
                latestTmpUri = uri
                takeImageResult.launch(uri)
            }
        }
    }

    private fun getTmpFileUri(): Uri? {
        val file = FileUtil.createImageFile(requireContext())

        return if (file != null) {
            FileProvider.getUriForFile(requireContext(), "${BuildConfig.APPLICATION_ID}.provider", file)
        } else {
            null
        }
    }


    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
