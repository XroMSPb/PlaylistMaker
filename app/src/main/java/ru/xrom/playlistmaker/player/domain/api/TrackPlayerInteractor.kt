package ru.xrom.playlistmaker.player.domain.api

import ru.xrom.playlistmaker.player.domain.model.PlayingState

interface TrackPlayerInteractor {

    var state: PlayingState
    fun prepare()
    fun start()
    fun pause()
    fun release()
    fun getCurrentPosition(): Int
}