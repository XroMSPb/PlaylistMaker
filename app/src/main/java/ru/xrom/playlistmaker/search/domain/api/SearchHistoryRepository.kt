package ru.xrom.playlistmaker.search.domain.api

import ru.xrom.playlistmaker.search.domain.model.Track

interface SearchHistoryRepository {
    fun updateTracks(): List<Track>
    fun addTrack(newTrack: Track)
    fun clearHistory()
}
