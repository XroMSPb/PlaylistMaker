package ru.xrom.playlistmaker.media.data.converter

import ru.xrom.playlistmaker.media.data.db.entity.TrackAtPlaylistEntity
import ru.xrom.playlistmaker.search.domain.model.Track
import ru.xrom.playlistmaker.utils.getPreviewUrl

class TrackAtPlaylistDBConverter {
    fun map(track: Track): TrackAtPlaylistEntity {
        return TrackAtPlaylistEntity(
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
            addedAt = System.currentTimeMillis().toString(),
            isFavorite = track.isFavorite

        )
    }

    fun map(trackEntity: TrackAtPlaylistEntity): Track {
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
            isFavorite = trackEntity.isFavorite
        )
    }
}