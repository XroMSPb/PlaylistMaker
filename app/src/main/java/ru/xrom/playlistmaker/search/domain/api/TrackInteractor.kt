package ru.xrom.playlistmaker.search.domain.api

import kotlinx.coroutines.flow.Flow
import ru.xrom.playlistmaker.search.domain.model.Track

interface TrackInteractor {
    fun search(expression: String): Flow<Pair<List<Track>?, String?>>
}