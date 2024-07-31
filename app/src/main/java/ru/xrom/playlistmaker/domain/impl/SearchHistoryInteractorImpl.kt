package ru.xrom.playlistmaker.domain.impl

import ru.xrom.playlistmaker.domain.api.SearchHistoryInteractor
import ru.xrom.playlistmaker.domain.api.SearchHistoryRepository
import ru.xrom.playlistmaker.domain.model.Track

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