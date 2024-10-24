package ru.xrom.playlistmaker.search.domain.api

import kotlinx.coroutines.flow.Flow
import ru.xrom.playlistmaker.search.domain.model.Track

interface SearchHistoryRepository {
    fun updateTracks(): Flow<List<Track>>
    fun addTrack(newTrack: Track)
    fun clearHistory()
}
