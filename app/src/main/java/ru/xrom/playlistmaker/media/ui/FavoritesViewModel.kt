package ru.xrom.playlistmaker.media.ui

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch
import ru.xrom.playlistmaker.media.domain.api.FavoritesInteractor
import ru.xrom.playlistmaker.search.domain.model.Track

class FavoritesViewModel(private val favoritesInteractor: FavoritesInteractor) : ViewModel() {

    private val favoriteTracks = MutableLiveData<List<Track>>()
    fun getFavoriteTracks(): LiveData<List<Track>> = favoriteTracks

    fun refreshFavoriteTracks() {
        viewModelScope.launch {
            favoritesInteractor.getFavoriteTracks().collect { trackList ->
                favoriteTracks.postValue(trackList)
            }
        }
    }

    init {
        refreshFavoriteTracks()
    }


}