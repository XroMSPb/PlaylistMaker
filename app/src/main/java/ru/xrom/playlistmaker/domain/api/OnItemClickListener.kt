package ru.xrom.playlistmaker.domain.api

import ru.xrom.playlistmaker.domain.model.Track

fun interface OnItemClickListener {
    fun onItemClick(item: Track)
}