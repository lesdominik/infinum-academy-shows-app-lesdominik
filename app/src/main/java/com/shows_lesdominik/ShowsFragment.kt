package com.shows_lesdominik

import android.content.Context
import android.content.SharedPreferences
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.edit
import androidx.core.view.isVisible
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.shows_lesdominik.databinding.DialogUserDetailsBinding
import com.shows_lesdominik.databinding.FragmentShowsBinding
import model.Show
import ui.ShowsAdapter

private const val REMEMBER_ME_CHECKED = "REMEMBER_ME_CHECKED"

class ShowsFragment : Fragment() {

    private val shows = listOf(
        Show("theOffice","The Office", R.drawable.the_office),
        Show("strangerThings","Stranger Things", R.drawable.stranger_things),
        Show("krvNijeVoda","Krv nije voda", R.drawable.krv_nije_voda)
    )

    private var _binding: FragmentShowsBinding? = null
    private val binding get() =_binding!!

    private lateinit var adapter: ShowsAdapter
    private lateinit var userEmail: String
    private val args by navArgs<ShowsFragmentArgs>()

    private lateinit var sharedPreferences: SharedPreferences

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

        initShowsRecycler()
        initListeners()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }


    private fun initListeners() {

        binding.toggleButton.addOnButtonCheckedListener { _, _, isChecked ->
            when {
                isChecked -> {
                    binding.showsRecycler.isVisible = false
                    binding.noShowsView.isVisible = true
                }
                else -> {
                    binding.showsRecycler.isVisible = true
                    binding.noShowsView.isVisible = false
                }
            }
        }

        binding.userIcon.setOnClickListener {
            showUserDetailsBottomSheet()
        }

    }

    private fun initShowsRecycler() {
        adapter = ShowsAdapter(shows) { show ->
            val directions = ShowsFragmentDirections.toFragmentShowDetails(show.name, show.imageResourceId, show.description, userEmail)
            findNavController().navigate(directions)
        }

        binding.showsRecycler.layoutManager = LinearLayoutManager(requireContext())

        binding.showsRecycler.adapter = adapter
    }

    private fun showUserDetailsBottomSheet() {
        val dialog = BottomSheetDialog(requireContext())

        val bottomSheetBinding = DialogUserDetailsBinding.inflate(layoutInflater)
        dialog.setContentView(bottomSheetBinding.root)

        bottomSheetBinding.userEmail.text = userEmail
        bottomSheetBinding.userDetailsImage.setImageResource(R.drawable.default_user)

        bottomSheetBinding.logoutButton.setOnClickListener {
            sharedPreferences.edit {
                putBoolean(REMEMBER_ME_CHECKED, false)
            }
            findNavController().popBackStack()

            dialog.dismiss()
        }

        dialog.show()
    }
}
