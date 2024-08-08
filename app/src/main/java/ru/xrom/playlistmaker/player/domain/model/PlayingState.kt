package ru.xrom.playlistmaker.player.domain.model

sealed class PlayingState {
    data object Default : PlayingState()
    data object Prepared : PlayingState()
    data object Playing : PlayingState()
    data object Paused : PlayingState()
    data object Complete : PlayingState()
}