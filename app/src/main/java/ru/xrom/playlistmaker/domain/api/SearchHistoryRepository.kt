package ru.xrom.playlistmaker.domain.api

import ru.xrom.playlistmaker.domain.model.Track

interface SearchHistoryRepository {
    fun updateTracks(): List<Track>
    fun addTrack(newTrack: Track)
    fun clearHistory()
}
