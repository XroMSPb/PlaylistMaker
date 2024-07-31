package ru.xrom.playlistmaker.domain.api

import ru.xrom.playlistmaker.domain.model.Track

interface TrackRepository {
    fun searchTracks(expression: String): List<Track>
}