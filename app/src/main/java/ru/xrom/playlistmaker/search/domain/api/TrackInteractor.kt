package ru.xrom.playlistmaker.search.domain.api

import kotlinx.coroutines.flow.Flow
import ru.xrom.playlistmaker.search.domain.model.SearchResult

interface TrackInteractor {
    fun search(expression: String): Flow<SearchResult>
}