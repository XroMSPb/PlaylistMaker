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
import ru.xrom.playlistmaker.utils.Creator

class TrackPlayerViewModel(
    private val trackPlayerInteractor: TrackPlayerInteractor,
) : ViewModel() {

    private val playingState = MutableLiveData(PlayingState.Default)
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

    init {
        onPrepare()
    }

    private fun onPrepare() {
        trackPlayerInteractor.prepare()
        playingState.postValue(PlayingState.Prepared)
        positionState.postValue(0)
    }

    private fun onPlay() {
        trackPlayerInteractor.start()
        playingState.postValue(PlayingState.Playing)
        startTimer()
    }

    private fun onPause() {
        trackPlayerInteractor.pause()
        playingState.postValue(PlayingState.Paused)
        pauseTimer()
    }

    fun stateControl() {
        playingState.postValue(trackPlayerInteractor.getState())
    }

    fun playingControl() {
        if (playingState.value == PlayingState.Playing) onPause()
        else onPlay()
    }

    private val handler = Handler(Looper.getMainLooper())
    private val timerRunnable by lazy {
        object : Runnable {
            override fun run() {
                if (playingState.value == PlayingState.Playing) {
                    positionState.postValue(trackPlayerInteractor.getCurrentPosition())
                    handler.postDelayed(this, TIMER_UPDATE_DELAY)
                }
            }
        }
    }

    private fun startTimer() {
        handler.post(timerRunnable)
    }

    private fun pauseTimer() {
        handler.removeCallbacks(timerRunnable)
    }

    override fun onCleared() {
        super.onCleared()
        pauseTimer()
        trackPlayerInteractor.release()
    }

}

