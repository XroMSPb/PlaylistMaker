package ru.xrom.playlistmaker.media.data.db

import androidx.room.Database
import androidx.room.RoomDatabase
import ru.xrom.playlistmaker.media.data.db.entity.PlaylistEntity
import ru.xrom.playlistmaker.media.data.db.entity.TrackAtPlaylistEntity
import ru.xrom.playlistmaker.media.data.db.entity.TrackEntity

@Database(
    version = 1,
    entities = [TrackEntity::class, PlaylistEntity::class, TrackAtPlaylistEntity::class],
    exportSchema = false
)
abstract class AppDatabase : RoomDatabase() {
    abstract fun trackDao(): TrackDao
    abstract fun playlistDao(): PlaylistDao
}