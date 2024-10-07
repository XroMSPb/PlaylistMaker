package ru.xrom.playlistmaker.player.domain.api

interface TrackPlayerInteractor {

    fun prepare()
    fun start()
    fun pause()
    fun release()
    fun getCurrentPosition(): Int
    fun isPlaying(): Boolean
}