package ru.xrom.playlistmaker.search.data.repository

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import ru.xrom.playlistmaker.media.data.converter.TrackDBConverter
import ru.xrom.playlistmaker.media.data.db.AppDatabase
import ru.xrom.playlistmaker.search.data.NetworkClient
import ru.xrom.playlistmaker.search.data.dto.ItunesResponse
import ru.xrom.playlistmaker.search.data.dto.TrackRequest
import ru.xrom.playlistmaker.search.domain.api.TrackRepository
import ru.xrom.playlistmaker.search.domain.model.Resource
import ru.xrom.playlistmaker.search.domain.model.Track

class TrackRepositoryImpl(
    private val networkClient: NetworkClient,
    private val trackDBConverter: TrackDBConverter,
    private val appDB: AppDatabase,
) : TrackRepository {
    override fun searchTracks(expression: String): Flow<Resource<List<Track>>> = flow {
        val response = networkClient.doRequest(TrackRequest(expression))
        when (response.resultCode) {
            200 -> {
                with(response as ItunesResponse) {
                    val trackList = response.results.map {
                        Track(
                            trackId = it.trackId,
                            trackName = it.trackName,
                            artistName = it.artistName,
                            trackTimeMillis = it.trackTimeMillis,
                            artworkUrl100 = it.artworkUrl100,
                            collectionName = it.collectionName,
                            primaryGenreName = it.primaryGenreName,
                            releaseDate = it.releaseDate,
                            country = it.country,
                            previewUrl = it.previewUrl,
                            isFavorite = appDB.trackDao().getTrackById(it.trackId).let { true }
                        )
                    }
                    emit(Resource.Success(trackList))
                }
            }

            -1 -> {
                emit(Resource.Error("Проверьте подключение к интернету"))
            }

            else -> {
                emit(Resource.Error("Ошибка сервера"))
            }
        }
    }
}