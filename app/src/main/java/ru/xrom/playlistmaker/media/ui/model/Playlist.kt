package ru.xrom.playlistmaker.media.ui.model

data class Playlist(
    val id: Int,
    val name: String,
    val description: String,
    val imagePath: String?,
    val tracks: ArrayList<String>,
)