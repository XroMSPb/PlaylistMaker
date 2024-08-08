package ru.xrom.playlistmaker.player.domain.model

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class TrackPlayerModel(
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
    val position: Int = 0,
    var isPlaying: Boolean = false,
) : Parcelable