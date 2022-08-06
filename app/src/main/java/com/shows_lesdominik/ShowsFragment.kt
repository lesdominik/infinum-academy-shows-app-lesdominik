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
import androidx.core.content.FileProvider
import androidx.lifecycle.lifecycleScope
import com.bumptech.glide.Glide
import java.io.File

class ShowsFragment : Fragment() {

    private var _binding: FragmentShowsBinding? = null
    private val binding get() =_binding!!

    private lateinit var bottomSheetBinding: DialogUserDetailsBinding

    private val args by navArgs<ShowsFragmentArgs>()
    private val viewModel by viewModels<ShowsViewModel>()

    private lateinit var sharedPreferences: SharedPreferences

    private var imageUrl: String? = null
    private var latestTmpUri: Uri? = null

    private val takeImageResult = registerForActivityResult(ActivityResultContracts.TakePicture()) { isSuccess ->
        if (isSuccess) {
            latestTmpUri?.let { uri ->
                binding.userIcon.setImageURI(uri)
                bottomSheetBinding.userDetailsImage.setImageURI(uri)

                FileUtil.getImageFile(requireContext())?.let {
                    viewModel.setProfileImage(sharedPreferences, it)
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

        binding.userIcon.setImageResource(R.drawable.default_user)

        viewModel.getUserInfo()
        viewModel.userLiveData.observe(viewLifecycleOwner) { user ->
            if (user != null) {
                imageUrl = user.imageUrl
                if (!imageUrl.isNullOrEmpty()) {
                    Glide.with(binding.root).load(imageUrl).into(binding.userIcon)
                }
            }
        }

        initShowsRecycler()
        initListeners()
    }


    private fun initListeners() {
        binding.userIcon.setOnClickListener {
            showUserDetailsBottomSheet()
        }
    }

    private fun initShowsRecycler() {
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
