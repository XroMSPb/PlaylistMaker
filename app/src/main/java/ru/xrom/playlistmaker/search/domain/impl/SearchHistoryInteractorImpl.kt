package ru.xrom.playlistmaker.search.domain.impl

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import ru.xrom.playlistmaker.search.domain.api.SearchHistoryInteractor
import ru.xrom.playlistmaker.search.domain.api.SearchHistoryRepository
import ru.xrom.playlistmaker.search.domain.model.Track

class SearchHistoryInteractorImpl(
    private val searchHistoryRepository: SearchHistoryRepository,
) : SearchHistoryInteractor {
    override fun getHistory(): Flow<List<Track>> = flow {
        searchHistoryRepository.updateTracks().collect { tracks ->
            emit(tracks)
        }
    }

    override fun clearHistory() {
        searchHistoryRepository.clearHistory()
    }

    override fun addToHistory(track: Track) {
        searchHistoryRepository.addTrack(track)
    }
}