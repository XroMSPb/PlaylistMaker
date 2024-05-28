package ru.xrom.playlistmaker.recycleView

import ru.xrom.playlistmaker.Track

fun interface OnItemClickListener {
    fun onItemClick(item: Track)
}