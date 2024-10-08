package ru.xrom.playlistmaker.search.ui

import android.app.Application
import androidx.core.content.ContextCompat.getString
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.Job
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
    private var searchJob: Job? = null

    private val searchState = MutableLiveData<SearchState>()
    fun observeSearchState(): LiveData<SearchState> = searchState

    fun addToHistory(track: Track) {
        searchHistorySaver.addToHistory(track)
        updateHistory()
    }

    fun clearHistory() {
        searchHistorySaver.clearHistory()
        renderState(SearchState.ContentHistory(searchHistorySaver.getHistory()))
    }

    fun updateHistory() {
        renderState(SearchState.ContentHistory(searchHistorySaver.getHistory()))
    }

    private fun renderState(state: SearchState) {
        searchState.postValue(state)
    }

    init {
        updateHistory()
    }

    fun stopSearch() {
        if (searchJob != null)
            searchJob?.cancel()
    }

    fun searchRequest(newSearchText: String) {
        latestSearchText = newSearchText
        renderState(SearchState.Loading)
        searchJob = viewModelScope.launch {
            trackInteractor.search(newSearchText)
                .collect { (foundTracks, error) ->
                    processResult(
                        foundTracks,
                        error,
                        newSearchText
                    )
                }
        }
    }

    private fun processResult(foundTracks: List<Track>?, error: String?, request: String) {
        if (foundTracks == null) {
            if (!error.isNullOrEmpty())
                renderState(SearchState.Error(error))
            else
                renderState(
                    SearchState.Error(
                        errorMessage = getString(
                            getApplication(), R.string.something_went_wrong
                        )
                    )
                )
        } else {
            if (foundTracks.isNotEmpty()) {
                if (request == latestSearchText)
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
        stopSearch()
        if (latestSearchText == changedText || changedText.isEmpty()) {
            return
        }
        trackSearchDebounce(changedText)
    }

    companion object {
        private const val SEARCH_DEBOUNCE_DELAY_MILLIS = 1000L
    }
}

