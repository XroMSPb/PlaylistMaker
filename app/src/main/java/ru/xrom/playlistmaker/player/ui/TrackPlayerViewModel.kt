package ru.xrom.playlistmaker.player.ui

import android.os.Handler
import android.os.Looper
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import ru.xrom.playlistmaker.player.domain.api.TrackPlayerInteractor
import ru.xrom.playlistmaker.player.domain.model.PlayingState
import ru.xrom.playlistmaker.player.domain.model.State
import ru.xrom.playlistmaker.utils.Creator

class TrackPlayerViewModel(
    private val trackPlayerInteractor: TrackPlayerInteractor,
) : ViewModel() {

    private var playerState = State.STATE_DEFAULT
    private val playingState = MutableLiveData<PlayingState>(PlayingState.Default)
    private val positionState = MutableLiveData(0)
    fun observePlayingState(): LiveData<PlayingState> = playingState
    fun observePositionState(): LiveData<Int> = positionState

    companion object {
        private const val TIMER_UPDATE_DELAY = 250L
        fun getViewModelFactory(trackUrl: String): ViewModelProvider.Factory =
            viewModelFactory {
                initializer {
                    TrackPlayerViewModel(
                        trackPlayerInteractor = Creator.provideTrackPlayerInteractor(trackUrl),
                    )
                }
            }
    }

    fun onPrepare() {
        playingState.postValue(PlayingState.Prepared)
        trackPlayerInteractor.prepare()
        positionState.postValue(0)
    }

    private fun onPlay() {
        playingState.postValue(PlayingState.Playing)
        trackPlayerInteractor.start()
        startPlayer()
    }

    private fun onPause() {
        playingState.postValue(PlayingState.Paused)
        trackPlayerInteractor.pause()
        pausePlayer()
    }

    fun stateControl() {
        val intState = trackPlayerInteractor.state
        when (intState) {
            State.STATE_DEFAULT -> playingState.postValue(PlayingState.Default)
            State.STATE_PREPARED -> playingState.postValue(PlayingState.Prepared)
            State.STATE_PLAYING -> playingState.postValue(PlayingState.Playing)
            State.STATE_PAUSED -> playingState.postValue(PlayingState.Paused)
        }
    }

    fun playingControl() {
        if (playingState.value is PlayingState.Playing) onPause()
        else onPlay()
    }

    private val handler = Handler(Looper.getMainLooper())
    private val timerRunnable by lazy {
        object : Runnable {
            override fun run() {
                if (playerState == State.STATE_PLAYING) {
                    positionState.postValue(trackPlayerInteractor.getCurrentPosition())
                    handler.postDelayed(this, TIMER_UPDATE_DELAY)
                }
            }
        }
    }

    private fun startPlayer() {
        playerState = State.STATE_PLAYING
        handler.post(timerRunnable)
    }

    private fun pausePlayer() {
        playerState = State.STATE_PAUSED
        handler.removeCallbacks(timerRunnable)
    }

    override fun onCleared() {
        super.onCleared()
        trackPlayerInteractor.release()
    }

}

