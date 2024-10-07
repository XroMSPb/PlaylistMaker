package ru.xrom.playlistmaker.player.data

import android.media.MediaPlayer
import ru.xrom.playlistmaker.player.domain.api.TrackPlayerInteractor
import ru.xrom.playlistmaker.player.domain.model.PlayerState

class TrackPlayerImpl(
    private val mediaPlayer: MediaPlayer,
    private val trackUrl: String,
) : TrackPlayerInteractor {

    private var state: PlayerState = PlayerState.Default()

    override fun prepare() {
        mediaPlayer.setDataSource(trackUrl)
        mediaPlayer.prepareAsync()
        mediaPlayer.setOnPreparedListener {
            state = PlayerState.Prepared()
        }
        mediaPlayer.setOnCompletionListener {
            state = PlayerState.Prepared()
        }
    }

    override fun start() {
        mediaPlayer.start()
        state = PlayerState.Playing(mediaPlayer.currentPosition.toString())
    }

    override fun pause() {
        mediaPlayer.pause()
        state = PlayerState.Paused(mediaPlayer.currentPosition.toString())
    }

    override fun release() {
        mediaPlayer.release()
    }

    override fun getCurrentPosition(): Int {
        return mediaPlayer.currentPosition
    }

    override fun isPlaying(): Boolean {
        return mediaPlayer.isPlaying
    }

}