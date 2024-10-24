package ru.xrom.playlistmaker.media.domain.impl

import kotlinx.coroutines.flow.Flow
import ru.xrom.playlistmaker.media.domain.db.FavoritesInteractor
import ru.xrom.playlistmaker.media.domain.db.FavoritesRepository
import ru.xrom.playlistmaker.search.domain.model.Track

class FavoritesInteractorImpl(
    private val repository: FavoritesRepository,
) : FavoritesInteractor {
    override fun isFavorite(trackId: String): Boolean {
        return repository.getTrackById(trackId).let { true }
    }

    override fun getTrackById(trackId: String): Flow<Track?> = repository.getTrackById(trackId)

    override fun getFavoriteTracks(): Flow<List<Track>> = repository.getFavoriteTracks()

    override fun addToFavorite(track: Track) {
        repository.addToFavorite(track)
        track.isFavorite = true
    }

    override fun removeFromFavorite(track: Track) {
        repository.removeFromFavorite(track.trackId)
        track.isFavorite = false
    }
}