package ru.xrom.playlistmaker.media.domain.api

import kotlinx.coroutines.flow.Flow
import ru.xrom.playlistmaker.media.ui.model.Playlist
import ru.xrom.playlistmaker.search.domain.model.Track

interface PlaylistInteractor {
    fun getPlaylists(): Flow<List<Playlist>>
    fun getPlaylistById(playlistId: Int): Flow<Playlist?>
    fun getAllTracks(playlistId: Int): Flow<List<Track>?>
    fun createPlaylist(
        playlistName: String,
        playlistDescription: String,
        playlistImage: String?,
    ): Long
    fun addToPlaylist(track: Track, playlistId: Int): Boolean
    fun removeFromPlaylist(trackId: String, playlistId: Int)
}