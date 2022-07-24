package com.shows_lesdominik

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import model.Show

class ShowsViewModel : ViewModel() {

    private val shows = listOf(
        Show("theOffice","The Office", R.drawable.the_office),
        Show("strangerThings","Stranger Things", R.drawable.stranger_things),
        Show("krvNijeVoda","Krv nije voda", R.drawable.krv_nije_voda)
    )

    private val _showsLiveData = MutableLiveData<List<Show>>()
    val showsLiveData: LiveData<List<Show>> = _showsLiveData

    init {
        _showsLiveData.value = shows
    }
}