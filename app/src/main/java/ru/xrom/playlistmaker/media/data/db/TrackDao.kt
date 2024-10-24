package ru.xrom.playlistmaker.media.data.db

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import ru.xrom.playlistmaker.media.data.db.entity.TrackEntity

@Dao
interface TrackDao {

    @Query("SELECT * FROM favorite_table ORDER BY addedAt DESC")
    suspend fun getAllTracks(): List<TrackEntity>

    @Query("SELECT * FROM favorite_table WHERE trackId = :id")
    suspend fun getTrackById(id: String): TrackEntity

    @Query("SELECT trackId FROM favorite_table")
    suspend fun getTracksIds(): List<String>

    @Insert(entity = TrackEntity::class, onConflict = OnConflictStrategy.REPLACE)
    fun insertTrack(track: TrackEntity)

    @Query("SELECT COUNT(*) > 0 FROM favorite_table WHERE trackId = :trackId")
    suspend fun isFavorite(trackId: String): Boolean

    @Query("DELETE FROM favorite_table WHERE trackId = :trackId")
    fun deleteTrack(trackId: String)

}


