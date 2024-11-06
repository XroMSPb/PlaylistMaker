package ru.xrom.playlistmaker.media.domain.impl

import kotlinx.coroutines.flow.Flow
import ru.xrom.playlistmaker.media.domain.api.PlaylistInteractor
import ru.xrom.playlistmaker.media.domain.api.PlaylistRepository
import ru.xrom.playlistmaker.media.ui.model.Playlist

class PlaylistInteractorImpl(
    private val repository: PlaylistRepository,
) : PlaylistInteractor {
    override fun getPlaylists(): Flow<List<Playlist>> = repository.getPlaylists()

    override fun createPlaylist(
        playlistName: String,
        playlistDescription: String,
        playlistImage: String?,
    ): Long {
        return repository.createPlaylist(playlistName, playlistDescription, playlistImage)
    }

    override fun addToPlaylist(trackId: String, playlistId: Int): Boolean {
        return repository.addToPlaylist(trackId, playlistId)
    }

    override fun removeFromPlaylist(trackId: String, playlistName: Int) {
        repository.removeFromPlaylist(trackId, playlistName)
    }
}