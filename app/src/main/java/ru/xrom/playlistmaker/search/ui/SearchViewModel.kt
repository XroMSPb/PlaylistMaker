package ru.xrom.playlistmaker.search.ui

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import ru.xrom.playlistmaker.R
import ru.xrom.playlistmaker.search.domain.api.SearchHistoryInteractor
import ru.xrom.playlistmaker.search.domain.api.TrackInteractor
import ru.xrom.playlistmaker.search.domain.model.Resource
import ru.xrom.playlistmaker.search.domain.model.Track
import ru.xrom.playlistmaker.utils.debounce

class SearchViewModel(
    application: Application,
    private val trackInteractor: TrackInteractor,
    private val searchHistorySaver: SearchHistoryInteractor,
) :
    AndroidViewModel(application) {
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

    private val consumer = object : TrackInteractor.TrackConsumer {
        override fun consume(foundTracks: Resource<List<Track>>, request: String) {
            when (foundTracks) {
                is Resource.Error -> renderState(
                    SearchState.Error(
                        errorMessage = application.getString(
                            R.string.something_went_wrong
                        )
                    )
                )

                is Resource.Success -> {
                    if (foundTracks.data?.isNotEmpty() == true) {
                        if (request == latestSearchText) {
                            renderState(SearchState.ContentSearch(foundTracks.data))
                        }
                    } else {
                        renderState(SearchState.NothingFound)
                    }
                }
            }
        }

        override fun onFailure(t: Throwable) {
            renderState(
                SearchState.Error(
                    errorMessage = application.getString(
                        R.string.something_went_wrong
                    )
                )
            )
        }
    }

    init {
        val searchHistory = searchHistorySaver.getHistory()
        if (searchHistory.isEmpty())
            renderState(SearchState.EmptyHistory)
        else
            renderState(SearchState.ContentHistory(searchHistory))
    }

    fun searchRequest(newSearchText: String) {
        latestSearchText = newSearchText
        renderState(SearchState.Loading)
        trackInteractor
            .search(newSearchText, consumer)
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

