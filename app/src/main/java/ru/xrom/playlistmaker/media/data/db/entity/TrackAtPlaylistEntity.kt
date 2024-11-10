package ru.xrom.playlistmaker.media.data.db.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "all_tracks")
data class TrackAtPlaylistEntity(
    @PrimaryKey
    val trackId: String,
    val trackName: String,
    val artistName: String,
    val trackTimeMillis: Long,
    val artworkUrl100: String,
    val collectionName: String,
    val primaryGenreName: String,
    val releaseDate: String,
    val country: String,
    val previewUrl: String,
    val addedAt: String,
    val isFavorite: Boolean
)