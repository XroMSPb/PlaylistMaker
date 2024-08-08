package ru.xrom.playlistmaker.player.domain.api

import ru.xrom.playlistmaker.player.domain.model.State

interface TrackPlayerInteractor {

    var state: State
    fun prepare()
    fun start()
    fun pause()
    fun release()
    fun getCurrentPosition(): Int
}