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
import java.io.File
import model.Show
import ui.ShowsAdapter

class ShowsFragment : Fragment() {

    private var _binding: FragmentShowsBinding? = null
    private val binding get() =_binding!!

    private lateinit var bottomSheetBinding: DialogUserDetailsBinding

    private lateinit var adapter: ShowsAdapter
    private val args by navArgs<ShowsFragmentArgs>()

    private val viewModel by viewModels<ShowsViewModel>()

    private lateinit var sharedPreferences: SharedPreferences

    private var latestTmpUri: Uri? = null
    private val takeImageResult = registerForActivityResult(ActivityResultContracts.TakePicture()) { isSuccess ->
        if (isSuccess) {
            latestTmpUri?.let { uri ->
                binding.userIcon.setImageURI(uri)
                bottomSheetBinding.userDetailsImage.setImageURI(uri)
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

        viewModel.getLatestTempUri(sharedPreferences)
        viewModel.latestTmpUri.observe(viewLifecycleOwner) { latestUri ->
            if (latestUri == null) {
                binding.userIcon.setImageResource(R.drawable.default_user)
            }
            else {
                binding.userIcon.setImageURI(latestUri)
            }
        }

        initShowsRecycler()
        initListeners()
    }

    private fun initListeners() {

        binding.toggleButton.addOnButtonCheckedListener { _, _, isChecked ->
            if (isChecked) {
                binding.showsRecycler.isVisible = false
                binding.noShowsView.isVisible = true
            } else {
                binding.showsRecycler.isVisible = true
                binding.noShowsView.isVisible = false
            }
        }

        binding.userIcon.setOnClickListener {
            showUserDetailsBottomSheet()
        }

    }

    private fun initShowsRecycler() {
        viewModel.showsLiveData.observe(viewLifecycleOwner) {shows ->
            adapter = ShowsAdapter(shows) { show ->
                val directions = ShowsFragmentDirections.toFragmentShowDetails(show.name, show.imageResourceId, show.description, args.userEmail)
                findNavController().navigate(directions)
            }
            binding.showsRecycler.layoutManager = LinearLayoutManager(requireContext())

            binding.showsRecycler.adapter = adapter
        }

    }

    private fun showUserDetailsBottomSheet() {
        val dialog = BottomSheetDialog(requireContext())

        bottomSheetBinding = DialogUserDetailsBinding.inflate(layoutInflater)
        dialog.setContentView(bottomSheetBinding.root)

        bottomSheetBinding.userEmail.text = args.userEmail

        if (latestTmpUri == null) {
            bottomSheetBinding.userDetailsImage.setImageResource(R.drawable.default_user)
        }
        else {
            bottomSheetBinding.userDetailsImage.setImageURI(latestTmpUri)
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
                viewModel.setRememberMeToFalse(sharedPreferences)

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
                viewModel.storeImageUri(sharedPreferences, uri)

                latestTmpUri = uri
                takeImageResult.launch(uri)
            }
        }
    }

    private fun getTmpFileUri(): Uri {
        val tmpFile = File.createTempFile("tmp_image_file", ".png").apply {
            createNewFile()
            deleteOnExit()
        }

        return FileProvider.getUriForFile(requireContext(), "${BuildConfig.APPLICATION_ID}.provider", tmpFile)
    }


    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
