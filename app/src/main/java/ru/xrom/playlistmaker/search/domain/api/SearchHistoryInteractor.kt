package ru.xrom.playlistmaker.search.domain.api

import kotlinx.coroutines.flow.Flow
import ru.xrom.playlistmaker.search.domain.model.Track

interface SearchHistoryInteractor {
    fun getHistory(): Flow<List<Track>>

    fun clearHistory()

    fun addToHistory(track: Track)
}