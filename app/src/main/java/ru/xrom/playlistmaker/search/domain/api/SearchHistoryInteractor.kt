package ru.xrom.playlistmaker.search.domain.api

import ru.xrom.playlistmaker.search.domain.model.Track

interface SearchHistoryInteractor {
    fun getHistory(): List<Track>

    fun clearHistory()

    fun addToHistory(track: Track)
}