package ru.xrom.playlistmaker.presentation.ui.search

import ru.xrom.playlistmaker.domain.model.Track

fun interface OnItemClickListener {
    fun onItemClick(item: Track)
}