package ru.xrom.playlistmaker.search.domain.api

import ru.xrom.playlistmaker.search.domain.model.Resource
import ru.xrom.playlistmaker.search.domain.model.Track

interface TrackRepository {
    fun searchTracks(expression: String): Resource<List<Track>>
}