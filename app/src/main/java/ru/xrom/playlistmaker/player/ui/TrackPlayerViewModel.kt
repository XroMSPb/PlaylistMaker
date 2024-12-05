package ru.xrom.playlistmaker.player.ui

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import ru.xrom.playlistmaker.media.domain.api.FavoritesInteractor
import ru.xrom.playlistmaker.media.domain.api.PlaylistInteractor
import ru.xrom.playlistmaker.media.ui.model.Playlist
import ru.xrom.playlistmaker.player.domain.api.TrackPlayerInteractor
import ru.xrom.playlistmaker.player.ui.model.AddToPlaylistState
import ru.xrom.playlistmaker.player.ui.model.PlayerState
import ru.xrom.playlistmaker.search.domain.model.Track
import java.text.SimpleDateFormat
import java.util.Locale

class TrackPlayerViewModel(
    private val trackPlayerInteractor: TrackPlayerInteractor,
    private val favoritesInteractor: FavoritesInteractor,
    private val playlistInteractor: PlaylistInteractor,
) : ViewModel() {

    private val playerState = MutableLiveData<PlayerState>(PlayerState.Default())
    fun observePlayingState(): LiveData<PlayerState> = playerState

    private val favoriteState = MutableLiveData<Boolean>()
    fun observeFavoriteState(): LiveData<Boolean> = favoriteState

    private val addedToPlaylistState = MutableLiveData<AddToPlaylistState>()
    fun observeAddingToPlaylistState(): LiveData<AddToPlaylistState> = addedToPlaylistState

    private val playlists = MutableLiveData<List<Playlist>>()
    fun observePlaylists(): LiveData<List<Playlist>> = playlists

    private var timerJob: Job? = null

    init {
        onPrepare()
    }

    private fun onPrepare() {
        trackPlayerInteractor.prepare(onCompletionListener = {
            timerJob?.cancel()
            playerState.postValue(PlayerState.Prepared())
        })
        playerState.postValue(PlayerState.Prepared())
    }

    private fun onPlay() {
        trackPlayerInteractor.start()
        playerState.postValue(PlayerState.Playing(getCurrentPlayerPosition()))
        startTimer()
    }

    private fun onPause() {
        trackPlayerInteractor.pause()
        timerJob?.cancel()
        playerState.postValue(PlayerState.Paused(getCurrentPlayerPosition()))
    }

    fun playingControl() {
        when (playerState.value) {
            is PlayerState.Playing -> onPause()
            else -> onPlay()
        }
    }

    private fun startTimer() {
        timerJob = viewModelScope.launch {
            while (trackPlayerInteractor.isPlaying()) {
                delay(TIMER_UPDATE_DELAY)
                playerState.postValue(PlayerState.Playing(getCurrentPlayerPosition()))
            }
        }
    }

    private fun getCurrentPlayerPosition(): String {
        return SimpleDateFormat(
            "mm:ss", Locale.getDefault()
        ).format(trackPlayerInteractor.getCurrentPosition()) ?: "00:00"
    }

    fun isTrackInFavorites(trackId: String) {
        viewModelScope.launch(Dispatchers.IO) {
            favoriteState.postValue(
                favoritesInteractor.isFavorite(trackId)
            )
        }
    }

    fun onFavoriteClick(track: Track) {
        when (track.isFavorite) {
            true -> {
                viewModelScope.launch(Dispatchers.IO) {
                    favoritesInteractor.removeFromFavorite(track)
                }
            }

            else -> {
                viewModelScope.launch(Dispatchers.IO) {
                    favoritesInteractor.addToFavorite(track)
                }
            }
        }
        favoriteState.postValue(!track.isFavorite)
    }

    fun onAddToPlaylistClick(track: Track, playlist: Playlist) {
        viewModelScope.launch(Dispatchers.IO) {
            addedToPlaylistState.postValue(
                AddToPlaylistState(
                    playlistInteractor.addToPlaylist(
                        track,
                        playlist.id
                    ), playlist
                )
            )
        }
    }

    fun updatePlaylists() {
        viewModelScope.launch(Dispatchers.IO) {
            playlistInteractor.getPlaylists().collect {
                playlists.postValue(it)
            }
        }
    }

    override fun onCleared() {
        super.onCleared()
        playerState.value = PlayerState.Default()
        trackPlayerInteractor.release()
    }

    companion object {
        private const val TIMER_UPDATE_DELAY = 300L
    }
}

