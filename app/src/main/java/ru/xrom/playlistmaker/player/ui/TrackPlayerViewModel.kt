package ru.xrom.playlistmaker.player.ui

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import ru.xrom.playlistmaker.media.domain.db.FavoritesInteractor
import ru.xrom.playlistmaker.player.domain.api.TrackPlayerInteractor
import ru.xrom.playlistmaker.player.ui.model.PlayerState
import ru.xrom.playlistmaker.search.domain.model.Track
import java.text.SimpleDateFormat
import java.util.Locale

class TrackPlayerViewModel(
    private val trackPlayerInteractor: TrackPlayerInteractor,
    private val favoritesInteractor: FavoritesInteractor,
) : ViewModel() {

    private val playerState = MutableLiveData<PlayerState>(PlayerState.Default())
    fun observePlayingState(): LiveData<PlayerState> = playerState

    private val favoriteState = MutableLiveData<Boolean>()
    fun observeFavoriteState(): LiveData<Boolean> = favoriteState

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

    override fun onCleared() {
        super.onCleared()
        playerState.value = PlayerState.Default()
        trackPlayerInteractor.release()
    }

    companion object {
        private const val TIMER_UPDATE_DELAY = 300L
    }
}

