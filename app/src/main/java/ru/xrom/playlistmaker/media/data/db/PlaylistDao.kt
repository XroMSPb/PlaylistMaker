package ru.xrom.playlistmaker.media.data.db

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import ru.xrom.playlistmaker.media.data.db.entity.PlaylistEntity
import ru.xrom.playlistmaker.media.data.db.entity.TrackAtPlaylistEntity

@Dao
interface PlaylistDao {

    @Query("SELECT * FROM playlist_table")
    suspend fun getAllPlaylists(): List<PlaylistEntity>

    @Query("SELECT * FROM playlist_table WHERE id = :id")
    fun getPlaylistById(id: Int): PlaylistEntity

    @Insert(entity = PlaylistEntity::class, onConflict = OnConflictStrategy.IGNORE)
    fun insertPlaylist(playlist: PlaylistEntity): Long

    @Update(entity = PlaylistEntity::class, onConflict = OnConflictStrategy.REPLACE)
    fun updatePlaylist(playlist: PlaylistEntity)

    @Query("SELECT tracks FROM playlist_table WHERE id = :playlistId")
    suspend fun getAllTracksFromPlaylist(playlistId: Int): String

    @Query("SELECT * FROM all_tracks")
    suspend fun getAllTracks(): List<TrackAtPlaylistEntity>

    @Query("SELECT * FROM all_tracks WHERE trackId IN (:trackIds)")
    suspend fun getTrackByIds(trackIds: List<String>): List<TrackAtPlaylistEntity>

    @Insert(entity = TrackAtPlaylistEntity::class, onConflict = OnConflictStrategy.IGNORE)
    fun insertTrack(track: TrackAtPlaylistEntity)

    @Query("DELETE FROM all_tracks WHERE trackId = :trackId")
    suspend fun deleteTrack(trackId: String)
}