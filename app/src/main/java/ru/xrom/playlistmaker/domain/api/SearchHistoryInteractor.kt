package ru.xrom.playlistmaker.domain.api

import ru.xrom.playlistmaker.domain.model.Track

interface SearchHistoryInteractor {
    fun getHistory(): List<Track>

    fun clearHistory()

    fun addToHistory(track: Track)
}