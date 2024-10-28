package ru.xrom.playlistmaker.media.domain.api

import kotlinx.coroutines.flow.Flow
import ru.xrom.playlistmaker.search.domain.model.Track

interface FavoritesRepository {
    fun getTrackById(trackId: String): Flow<Track>
    fun getFavoriteTracks(): Flow<List<Track>>
    fun addToFavorite(track: Track)
    fun removeFromFavorite(trackId: String)
}