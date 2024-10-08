package ru.xrom.playlistmaker.player.data

import android.media.MediaPlayer
import ru.xrom.playlistmaker.player.domain.api.TrackPlayerInteractor

class TrackPlayerImpl(
    private val mediaPlayer: MediaPlayer,
    private val trackUrl: String,
) : TrackPlayerInteractor {

    override fun prepare(onCompletionListener: () -> Unit) {
        mediaPlayer.setDataSource(trackUrl)
        mediaPlayer.prepareAsync()
        mediaPlayer.setOnCompletionListener {
            onCompletionListener()
        }
    }

    override fun start() {
        mediaPlayer.start()
    }

    override fun pause() {
        mediaPlayer.pause()
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