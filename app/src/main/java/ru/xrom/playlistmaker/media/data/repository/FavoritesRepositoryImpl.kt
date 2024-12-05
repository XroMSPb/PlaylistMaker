package ru.xrom.playlistmaker.media.data.repository

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import ru.xrom.playlistmaker.media.data.converter.TrackDBConverter
import ru.xrom.playlistmaker.media.data.db.AppDatabase
import ru.xrom.playlistmaker.media.data.db.entity.TrackEntity
import ru.xrom.playlistmaker.media.domain.api.FavoritesRepository
import ru.xrom.playlistmaker.search.domain.model.Track

class FavoritesRepositoryImpl(
    private val database: AppDatabase,
    private val trackDBConverter: TrackDBConverter,
) : FavoritesRepository {
    override suspend fun isFavorite(trackId: String): Boolean {
        return database.trackDao().isFavorite(trackId)
    }

    override fun getTrackById(trackId: String): Flow<Track> = flow {
        val track = database.trackDao().getTrackById(trackId)
        emit(trackDBConverter.map(track))
    }

    override fun getFavoriteTracks(): Flow<List<Track>> = flow {
        val tracks = database.trackDao().getAllTracks()
        emit(convertFromTrackEntity(tracks))
    }

    override fun addToFavorite(track: Track) {
        database.trackDao().insertTrack(trackDBConverter.map(track))
    }

    override fun removeFromFavorite(trackId: String) {
        database.trackDao().deleteTrack(trackId)
    }

    private fun convertFromTrackEntity(tracks: List<TrackEntity>): List<Track> {
        return tracks.map { track -> trackDBConverter.map(track) }
    }

}