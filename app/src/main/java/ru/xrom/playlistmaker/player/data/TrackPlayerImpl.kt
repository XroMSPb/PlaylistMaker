package ru.xrom.playlistmaker.player.data

import android.media.MediaPlayer
import ru.xrom.playlistmaker.player.domain.api.TrackPlayerInteractor
import ru.xrom.playlistmaker.player.domain.model.PlayingState

class TrackPlayerImpl(
    private val mediaPlayer: MediaPlayer,
    private val trackUrl: String,
) : TrackPlayerInteractor {

    override var state: PlayingState = PlayingState.Default
    override fun prepare() {
        mediaPlayer.setDataSource(trackUrl)
        mediaPlayer.prepareAsync()
        mediaPlayer.setOnPreparedListener {
            state = PlayingState.Prepared
        }
        mediaPlayer.setOnCompletionListener {
            state = PlayingState.Prepared
        }
    }

    override fun start() {
        mediaPlayer.start()
        state = PlayingState.Playing
    }

    override fun pause() {
        mediaPlayer.pause()
        state = PlayingState.Paused
    }

    override fun release() {
        mediaPlayer.release()
    }

    override fun getCurrentPosition(): Int {
        return mediaPlayer.currentPosition
    }

}