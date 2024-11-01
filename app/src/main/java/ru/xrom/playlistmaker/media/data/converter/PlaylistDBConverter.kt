package ru.xrom.playlistmaker.media.data.converter

import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import ru.xrom.playlistmaker.media.data.db.entity.PlaylistEntity
import ru.xrom.playlistmaker.media.ui.model.Playlist

class PlaylistDBConverter {
    fun map(playlist: Playlist): PlaylistEntity {
        return PlaylistEntity(
            id = playlist.id,
            name = playlist.name,
            description = playlist.description,
            imagePath = playlist.imagePath,
            tracks = createJsonFromTracks(playlist.tracks),
            tracksCount = playlist.tracks.size,
        )
    }

    fun map(playlist: PlaylistEntity): Playlist {
        return Playlist(
            id = playlist.id,
            name = playlist.name,
            description = playlist.description,
            imagePath = playlist.imagePath,
            tracks = createTracksFromJson(playlist.tracks),
        )
    }

    fun createJsonFromTracks(tracks: ArrayList<String>): String {
        return Gson().toJson(tracks)
    }

    fun createTracksFromJson(json: String): ArrayList<String> {
        val trackListType = object : TypeToken<List<String>>() {}.type
        return Gson().fromJson(json, trackListType)
    }
}