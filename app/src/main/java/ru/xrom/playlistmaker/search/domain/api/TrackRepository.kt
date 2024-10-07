package ru.xrom.playlistmaker.search.domain.api

import kotlinx.coroutines.flow.Flow
import ru.xrom.playlistmaker.search.domain.model.Resource
import ru.xrom.playlistmaker.search.domain.model.Track

interface TrackRepository {
    fun searchTracks(expression: String): Flow<Resource<List<Track>>>
}