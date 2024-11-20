package ru.xrom.playlistmaker.search.ui

import ru.xrom.playlistmaker.search.domain.model.Track

fun interface OnItemClickListener {
    fun onItemClick(item: Track)
}

fun interface OnItemLongClickListener {
    fun onItemLongClick(item: Track)
}