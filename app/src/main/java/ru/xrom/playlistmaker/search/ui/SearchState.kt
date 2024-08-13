package ru.xrom.playlistmaker.search.ui

import ru.xrom.playlistmaker.search.domain.model.Track

sealed interface SearchState {
    data class ContentHistory(val data: List<Track>) : SearchState
    object EmptyHistory : SearchState
    object Loading : SearchState
    data class ContentSearch(val tracks: List<Track>) : SearchState
    data class Error(val errorMessage: String) : SearchState
    object NothingFound : SearchState

}