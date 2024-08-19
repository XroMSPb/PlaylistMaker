package ru.xrom.playlistmaker.search.domain.impl

import ru.xrom.playlistmaker.search.domain.api.SearchHistoryInteractor
import ru.xrom.playlistmaker.search.domain.api.SearchHistoryRepository
import ru.xrom.playlistmaker.search.domain.model.Track

class SearchHistoryInteractorImpl(
    private val searchHistoryRepository: SearchHistoryRepository,
) : SearchHistoryInteractor {
    override fun getHistory(): List<Track> {
        return searchHistoryRepository.updateTracks()
    }

    override fun clearHistory() {
        searchHistoryRepository.clearHistory()
    }

    override fun addToHistory(track: Track) {
        searchHistoryRepository.addTrack(track)
    }
}