package ru.xrom.playlistmaker.search.ui

import android.app.Application
import androidx.core.content.ContextCompat.getString
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch
import ru.xrom.playlistmaker.R
import ru.xrom.playlistmaker.search.domain.api.SearchHistoryInteractor
import ru.xrom.playlistmaker.search.domain.api.TrackInteractor
import ru.xrom.playlistmaker.search.domain.model.Track
import ru.xrom.playlistmaker.utils.debounce

class SearchViewModel(
    application: Application,
    private val trackInteractor: TrackInteractor,
    private val searchHistorySaver: SearchHistoryInteractor,
) : AndroidViewModel(application) {
    private var latestSearchText: String? = null

    private val searchState = MutableLiveData<SearchState>()
    fun observeSearchState(): LiveData<SearchState> = searchState

    fun addToHistory(track: Track) {
        searchHistorySaver.addToHistory(track)
        renderState(SearchState.ContentHistory(searchHistorySaver.getHistory()))
    }

    fun clearHistory() {
        searchHistorySaver.clearHistory()
        renderState(SearchState.EmptyHistory)
    }

    private fun renderState(state: SearchState) {
        searchState.postValue(state)
    }

    init {
        val searchHistory = searchHistorySaver.getHistory()
        if (searchHistory.isEmpty()) renderState(SearchState.EmptyHistory)
        else renderState(SearchState.ContentHistory(searchHistory))
    }

    fun searchRequest(newSearchText: String) {
        latestSearchText = newSearchText
        renderState(SearchState.Loading)
        viewModelScope.launch {
            trackInteractor.search(newSearchText)
                .collect { pair -> processResult(pair.first, pair.second) }
        }
    }

    private fun processResult(foundTracks: List<Track>?, error: String?) {
        if (foundTracks == null) {
            renderState(
                SearchState.Error(
                    errorMessage = getString(
                        getApplication(), R.string.something_went_wrong
                    )
                )
            )
        } else {
            if (foundTracks.isNotEmpty()) {
                renderState(SearchState.ContentSearch(foundTracks))
            } else {
                renderState(SearchState.NothingFound)
            }

        }
    }

    private val trackSearchDebounce =
        debounce<String>(SEARCH_DEBOUNCE_DELAY_MILLIS, viewModelScope, true) { changedText ->
            searchRequest(changedText)
        }

    fun searchDebounce(changedText: String) {
        if (latestSearchText == changedText) {
            return
        }
        trackSearchDebounce(changedText)
    }

    companion object {
        private const val SEARCH_DEBOUNCE_DELAY_MILLIS = 1000L
    }
}

