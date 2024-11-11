package ru.xrom.playlistmaker.media.ui

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import ru.xrom.playlistmaker.media.domain.api.PlaylistInteractor
import ru.xrom.playlistmaker.media.ui.model.Playlist
import ru.xrom.playlistmaker.search.domain.model.Track

class PlaylistViewerViewModel(private val interactor: PlaylistInteractor) : ViewModel() {

    private val playlist = MutableLiveData<Playlist?>()
    fun observePlaylist(): LiveData<Playlist?> = playlist

    private val allTracks = MutableLiveData<List<Track>?>()
    fun observeAllTracks(): LiveData<List<Track>?> = allTracks


    fun loadPlaylist(playlistId: Int) {
        viewModelScope.launch(Dispatchers.IO) {
            interactor.getPlaylistById(playlistId).collect {
                if (it != null) {
                    interactor.getAllTracks(it.id).collect { tracks ->
                        allTracks.postValue(tracks)
                    }
                    playlist.postValue(it)
                }

            }
        }
    }


    fun removeTrackFromPlaylist(trackId: String) {
        viewModelScope.launch(Dispatchers.IO) {
            interactor.removeFromPlaylist(trackId, playlist.value!!.id)
            interactor.getAllTracks(playlist.value!!.id).collect { tracks ->
                allTracks.postValue(tracks)
            }
        }
    }
}