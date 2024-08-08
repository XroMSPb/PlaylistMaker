package ru.xrom.playlistmaker.player.data

import android.media.MediaPlayer
import ru.xrom.playlistmaker.player.domain.api.TrackPlayerInteractor
import ru.xrom.playlistmaker.player.domain.model.State

class TrackPlayerImpl(
    private val mediaPlayer: MediaPlayer,
    private val trackUrl: String,
) : TrackPlayerInteractor {

    override var state = State.STATE_DEFAULT
    override fun prepare() {
        mediaPlayer.setDataSource(trackUrl)
        mediaPlayer.prepareAsync()
        mediaPlayer.setOnPreparedListener {
            state = State.STATE_PREPARED
        }
        mediaPlayer.setOnCompletionListener {
            state = State.STATE_PREPARED
        }
    }

    override fun start() {
        mediaPlayer.start()
        state = State.STATE_PLAYING
    }

    override fun pause() {
        mediaPlayer.pause()
        state = State.STATE_PAUSED
    }

    override fun release() {
        mediaPlayer.release()
    }


    override fun getCurrentPosition(): Int {
        return mediaPlayer.currentPosition
    }

}