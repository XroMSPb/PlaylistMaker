package ru.xrom.playlistmaker.search.data.repository

import ru.xrom.playlistmaker.search.data.NetworkClient
import ru.xrom.playlistmaker.search.data.dto.ItunesResponse
import ru.xrom.playlistmaker.search.data.dto.TrackRequest
import ru.xrom.playlistmaker.search.domain.api.TrackRepository
import ru.xrom.playlistmaker.search.domain.model.Track

class TrackRepositoryImpl(private val networkClient: NetworkClient) : TrackRepository {
    override fun searchTracks(expression: String): List<Track> {
        val response = networkClient.doRequest(TrackRequest(expression))
        if (response.resultCode == 200) {
            return (response as ItunesResponse).results.map {
                Track(
                    it.trackId,
                    it.trackName,
                    it.artistName,
                    it.trackTimeMillis,
                    it.artworkUrl100,
                    it.collectionName,
                    it.primaryGenreName,
                    it.releaseDate,
                    it.country,
                    it.previewUrl
                )
            }
        } else {
            return emptyList()
        }
    }
}