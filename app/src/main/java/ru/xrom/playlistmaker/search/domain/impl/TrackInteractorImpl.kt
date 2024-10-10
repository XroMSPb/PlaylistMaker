package ru.xrom.playlistmaker.search.domain.impl

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import ru.xrom.playlistmaker.search.domain.api.TrackInteractor
import ru.xrom.playlistmaker.search.domain.api.TrackRepository
import ru.xrom.playlistmaker.search.domain.model.Resource
import ru.xrom.playlistmaker.search.domain.model.SearchResult

class TrackInteractorImpl(private val repository: TrackRepository) : TrackInteractor {

    override fun search(expression: String): Flow<SearchResult> {
        return repository.searchTracks(expression).map { result ->
            when (result) {
                is Resource.Success -> SearchResult.Success(result.data, expression)
                is Resource.Error -> SearchResult.Error(result.message)
            }
        }
    }
}