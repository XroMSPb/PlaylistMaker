package ru.xrom.playlistmaker.search.ui

import android.app.Application
import android.os.Handler
import android.os.Looper
import android.os.SystemClock
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelProvider.AndroidViewModelFactory.Companion.APPLICATION_KEY
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import ru.xrom.playlistmaker.R
import ru.xrom.playlistmaker.search.domain.api.SearchHistoryInteractor
import ru.xrom.playlistmaker.search.domain.api.TrackInteractor
import ru.xrom.playlistmaker.search.domain.model.Resource
import ru.xrom.playlistmaker.search.domain.model.Track
import ru.xrom.playlistmaker.utils.Creator
import ru.xrom.playlistmaker.utils.Creator.provideSearchHistoryGetHistoryInteractor

class SearchViewModel(application: Application) : AndroidViewModel(application) {
    private var latestSearchText: String? = null

    companion object {
        private const val SEARCH_DEBOUNCE_DELAY = 1000L
        private val SEARCH_REQUEST_TOKEN = Any()
        fun getViewModelFactory(): ViewModelProvider.Factory = viewModelFactory {
            initializer {
                SearchViewModel(this[APPLICATION_KEY] as Application)
            }
        }
    }

    override fun onCleared() {
        handler.removeCallbacksAndMessages(SEARCH_REQUEST_TOKEN)
    }

    private val searchHistorySaver: SearchHistoryInteractor by lazy {
        provideSearchHistoryGetHistoryInteractor()
    }
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

    private val handler = Handler(Looper.getMainLooper())

    private fun renderState(state: SearchState) {
        searchState.postValue(state)
    }

    init {
        val searchHistory = searchHistorySaver.getHistory()
        if (searchHistory.isEmpty())
            renderState(SearchState.EmptyHistory)
        else
            renderState(SearchState.ContentHistory(searchHistory))
    }

    fun searchRequest(newSearchText: String) {
        renderState(SearchState.Loading)
        Creator.provideTrackInteractor()
            .search(newSearchText, object : TrackInteractor.TrackConsumer {
                override fun consume(foundTracks: Resource<List<Track>>) {
                    when (foundTracks) {
                        is Resource.Error -> renderState(
                            SearchState.Error(
                                errorMessage = getApplication<Application>().getString(
                                    R.string.something_went_wrong
                                )
                            )
                        )

                        is Resource.Success -> {
                            if (foundTracks.data?.isNotEmpty() == true) {
                                renderState(SearchState.ContentSearch(foundTracks.data))
                            } else {
                                renderState(SearchState.NothingFound)
                            }
                        }
                    }
                }

                override fun onFailure(t: Throwable) {
                    renderState(
                        SearchState.Error(
                            errorMessage = getApplication<Application>().getString(
                                R.string.something_went_wrong
                            )
                        )
                    )
                }

            })
    }

    fun searchDebounce(changedText: String) {
        if (latestSearchText == changedText) {
            return
        }
        latestSearchText = changedText
        handler.removeCallbacksAndMessages(SEARCH_REQUEST_TOKEN)
        val searchRunnable = Runnable { searchRequest(changedText) }
        val postTime = SystemClock.uptimeMillis() + SEARCH_DEBOUNCE_DELAY
        handler.postAtTime(
            searchRunnable,
            SEARCH_REQUEST_TOKEN,
            postTime,
        )
    }
}

