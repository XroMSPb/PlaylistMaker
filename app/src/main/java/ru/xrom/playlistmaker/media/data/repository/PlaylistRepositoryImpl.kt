package ru.xrom.playlistmaker.media.data.repository

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import ru.xrom.playlistmaker.media.data.converter.PlaylistDBConverter
import ru.xrom.playlistmaker.media.data.db.AppDatabase
import ru.xrom.playlistmaker.media.data.db.entity.PlaylistEntity
import ru.xrom.playlistmaker.media.domain.api.PlaylistRepository
import ru.xrom.playlistmaker.media.ui.model.Playlist

class PlaylistRepositoryImpl(
    private val database: AppDatabase,
    private val playlistDBConverter: PlaylistDBConverter,
) : PlaylistRepository {
    override fun getPlaylists(): Flow<List<Playlist>> = flow {
        val playlists = database.playlistDao().getAllPlaylists()
        emit(convertFromPlaylistEntity(playlists))
    }

    override fun createPlaylist(
        playlistName: String,
        playlistDescription: String,
        playlistImage: String?,
    ): Long {
        return database.playlistDao().insertPlaylist(
            PlaylistEntity(
                name = playlistName,
                description = playlistDescription,
                imagePath = playlistImage,
                tracks = "",
                tracksCount = 0
            )
        )
    }

    override fun addToPlaylist(trackId: String, playlistId: Int) {
        val playlist = database.playlistDao().getPlaylistById(playlistId)
        val jsonTracks = playlist.tracks
        if (jsonTracks.isNotEmpty()) {
            val tracks = playlistDBConverter.createTracksFromJson(jsonTracks)
            val currentTrack = tracks.filter { track -> track == trackId }
            if (currentTrack.isEmpty()) {
                tracks.add(trackId)
                playlist.tracks = playlistDBConverter.createJsonFromTracks(tracks)
                database.playlistDao().updatePlaylist(
                    playlist
                )
            }
        }
    }

    override fun removeFromPlaylist(trackId: String, playlistId: Int) {
        val playlist = database.playlistDao().getPlaylistById(playlistId)
        val jsonTracks = playlist.tracks
        if (jsonTracks.isNotEmpty()) {
            val tracks = playlistDBConverter.createTracksFromJson(jsonTracks)
            val currentTrack = tracks.filter { track -> track == trackId }
            if (currentTrack.isNotEmpty()) {
                tracks.remove(trackId)
                playlist.tracks = playlistDBConverter.createJsonFromTracks(tracks)
                database.playlistDao().updatePlaylist(
                    playlist
                )
            }
        }
    }

    fun convertFromPlaylistEntity(playlists: List<PlaylistEntity>): List<Playlist> {
        return playlists.map { playlist -> playlistDBConverter.map(playlist) }
    }
}