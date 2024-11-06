package ru.xrom.playlistmaker.media.domain.api

import kotlinx.coroutines.flow.Flow
import ru.xrom.playlistmaker.media.ui.model.Playlist

interface PlaylistInteractor {
    fun getPlaylists(): Flow<List<Playlist>>
    fun createPlaylist(
        playlistName: String,
        playlistDescription: String,
        playlistImage: String?,
    ): Long
    fun addToPlaylist(trackId: String, playlistId: Int): Boolean
    fun removeFromPlaylist(trackId: String, playlistId: Int)
}