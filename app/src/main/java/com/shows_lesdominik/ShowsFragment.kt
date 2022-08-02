package com.shows_lesdominik

import android.app.AlertDialog
import android.content.Context
import android.content.SharedPreferences
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
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
import android.os.Build
import androidx.core.content.ContextCompat.getSystemService
import androidx.core.content.FileProvider
import androidx.lifecycle.lifecycleScope
import com.bumptech.glide.Glide
import java.io.File

class ShowsFragment : Fragment() {

    private var _binding: FragmentShowsBinding? = null
    private val binding get() =_binding!!

    private lateinit var bottomSheetBinding: DialogUserDetailsBinding

    private lateinit var userEmail: String
    private val args by navArgs<ShowsFragmentArgs>()
    private val viewModel: ShowsViewModel by viewModels {
        ShowsViewModelFactory((activity?.application as ShowsApplication).database)
    }

    private lateinit var sharedPreferences: SharedPreferences

    private var imageUrl: String? = null
    private lateinit var uri: Uri
    private var latestTmpUri: Uri? = null
    private val takeImageResult = registerForActivityResult(ActivityResultContracts.TakePicture()) { isSuccess ->
        if (isSuccess) {
            latestTmpUri?.let { uri ->
                binding.userIcon.setImageURI(uri)
                bottomSheetBinding.userDetailsImage.setImageURI(uri)
                viewModel.setProfileImage(requireContext())
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

        userEmail = args.userEmail
        binding.userIcon.setImageResource(R.drawable.default_user)

        if (isConnected(requireContext())) {
            viewModel.getUserInfo()
            viewModel.userLiveData.observe(viewLifecycleOwner) { user ->
                if (user != null) {
                    imageUrl = user.imageUrl
                    if (!imageUrl.isNullOrEmpty()) {
                        Glide.with(binding.root).load(imageUrl).into(binding.userIcon)
                    }
                }
            }
        }

        initShowsRecycler()
        initListeners()
    }

    private fun isConnected(context: Context): Boolean {
        val connectivityManager = context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {

            val network = connectivityManager.activeNetwork ?: return false
            val activeNetwork = connectivityManager.getNetworkCapabilities(network) ?: return false

            return when {
                activeNetwork.hasTransport(NetworkCapabilities.TRANSPORT_WIFI) -> true
                activeNetwork.hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR) -> true
                else -> false
            }
        } else {
            // if the android version is below M
            @Suppress("DEPRECATION") val networkInfo =
                connectivityManager.activeNetworkInfo ?: return false
            @Suppress("DEPRECATION")
            return networkInfo.isConnected
        }
    }


    private fun initListeners() {
        binding.userIcon.setOnClickListener {
            showUserDetailsBottomSheet()
        }
    }

    private fun initShowsRecycler() {
        if (isConnected(requireContext())) {
            viewModel.getShows()
            viewModel.showsLiveData.observe(viewLifecycleOwner) {shows ->
                binding.loadingShows.isVisible = false
                if (shows.isEmpty()) {
                    binding.showsRecycler.isVisible = false
                    binding.noShowsView.isVisible = true
                } else {
                    binding.showsRecycler.isVisible = true
                    binding.noShowsView.isVisible = false

                    binding.showsRecycler.adapter = ShowsAdapter(shows) { show ->
                        val directions = ShowsFragmentDirections.toFragmentShowDetails(show.id, userEmail)
                        findNavController().navigate(directions)
                    }
                    binding.showsRecycler.layoutManager = LinearLayoutManager(requireContext())
                }
            }
        } else {
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
                        val directions = ShowsFragmentDirections.toFragmentShowDetails(show.id, userEmail)
                        findNavController().navigate(directions)
                    }
                    binding.showsRecycler.layoutManager = LinearLayoutManager(requireContext())
                }
            }
        }
    }

    private fun showUserDetailsBottomSheet() {
        val dialog = BottomSheetDialog(requireContext())

        bottomSheetBinding = DialogUserDetailsBinding.inflate(layoutInflater)
        dialog.setContentView(bottomSheetBinding.root)

        bottomSheetBinding.userEmail.text = userEmail

        when {
            latestTmpUri != null -> bottomSheetBinding.userDetailsImage.setImageURI(uri)
            imageUrl != null -> Glide.with(bottomSheetBinding.root).load(imageUrl).into(bottomSheetBinding.userDetailsImage)
            else -> bottomSheetBinding.userDetailsImage.setImageResource(R.drawable.default_user)
        }


        bottomSheetBinding.changeProfilePhoto.setOnClickListener {
           takeImage()
        }

        bottomSheetBinding.logoutButton.setOnClickListener {
            showAlertDialog()
            dialog.dismiss()
        }

        dialog.show()
    }

    private fun showAlertDialog() {
        val alertDialog = AlertDialog.Builder(requireContext())

        alertDialog.setMessage("Do you really want to log out?")
            .setPositiveButton("Yes") { dialog, _ ->
                sharedPreferences.edit {
                    putBoolean("REMEMBER_ME_CHECKED", false)
                }
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

    private fun getTmpFileUri(): Uri {
        val file = FileUtil.createImageFile(requireContext())

        file?.let {
            uri = FileProvider.getUriForFile(requireContext(), "${BuildConfig.APPLICATION_ID}.provider", it)
        }
        return uri
    }


    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
