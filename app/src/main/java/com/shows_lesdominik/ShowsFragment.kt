package com.shows_lesdominik

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.LinearLayoutManager
import com.shows_lesdominik.databinding.FragmentShowsBinding
import model.Show
import ui.ShowsAdapter

class ShowsFragment : Fragment() {

    private val shows = listOf(
        Show("theOffice","The Office", R.drawable.the_office),
        Show("strangerThings","Stranger Things", R.drawable.stranger_things),
        Show("krvNijeVoda","Krv nije voda", R.drawable.krv_nije_voda)
    )

    private var _binding: FragmentShowsBinding? = null
    private val binding get() =_binding!!

    private lateinit var adapter: ShowsAdapter
    private lateinit var username: String
    private val args by navArgs<ShowsFragmentArgs>()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentShowsBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        username = args.username

        initShowsRecycler()
        initListeners()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }


    private fun initListeners() {
        binding.logoutButton.setOnClickListener {
            findNavController().popBackStack()
        }

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

    }

    private fun initShowsRecycler() {
        adapter = ShowsAdapter(shows) { show ->
            val directions = ShowsFragmentDirections.toFragmentShowDetails(show.name, show.imageResourceId, show.description, username)
            findNavController().navigate(directions)
        }

        binding.showsRecycler.layoutManager = LinearLayoutManager(context)

        binding.showsRecycler.adapter = adapter
    }
}
