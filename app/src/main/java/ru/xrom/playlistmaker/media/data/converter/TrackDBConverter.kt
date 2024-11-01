package ru.xrom.playlistmaker.media.data.converter

import ru.xrom.playlistmaker.media.data.db.entity.TrackEntity
import ru.xrom.playlistmaker.search.domain.model.Track
import ru.xrom.playlistmaker.utils.getPreviewUrl

class TrackDBConverter {
    fun map(track: Track): TrackEntity {
        return TrackEntity(
            trackId = track.trackId,
            trackName = track.trackName,
            artistName = track.artistName,
            trackTimeMillis = track.trackTimeMillis,
            artworkUrl100 = track.artworkUrl100,
            previewUrl = getPreviewUrl(track.previewUrl),
            collectionName = track.collectionName,
            primaryGenreName = track.primaryGenreName ?: "unknown",
            releaseDate = track.releaseDate.orEmpty(),
            country = track.country,
            addedAt = System.currentTimeMillis().toString()
        )
    }

    fun map(trackEntity: TrackEntity): Track {
        return Track(
            trackId = trackEntity.trackId,
            trackName = trackEntity.trackName,
            artistName = trackEntity.artistName,
            trackTimeMillis = trackEntity.trackTimeMillis,
            artworkUrl100 = trackEntity.artworkUrl100,
            previewUrl = trackEntity.previewUrl,
            collectionName = trackEntity.collectionName,
            primaryGenreName = trackEntity.primaryGenreName,
            releaseDate = trackEntity.releaseDate,
            country = trackEntity.country,
            isFavorite = true
        )
    }
}