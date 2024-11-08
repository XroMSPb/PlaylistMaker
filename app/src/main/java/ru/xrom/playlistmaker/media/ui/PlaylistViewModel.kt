package ru.xrom.playlistmaker.media.ui

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import ru.xrom.playlistmaker.media.domain.api.PlaylistInteractor
import ru.xrom.playlistmaker.media.ui.model.Playlist

class PlaylistViewModel(
    private val interactor: PlaylistInteractor,
) : ViewModel() {

    private val playlists = MutableLiveData<List<Playlist>>()
    fun observePlaylists(): LiveData<List<Playlist>> = playlists


    fun updatePlaylists() {
        viewModelScope.launch(Dispatchers.IO) {
            interactor.getPlaylists().collect {
                playlists.postValue(it)
            }
        }
    }
}