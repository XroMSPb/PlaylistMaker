package ru.xrom.playlistmaker.recycleView

import ru.xrom.playlistmaker.utils.Track

fun interface OnItemClickListener {
    fun onItemClick(item: Track)
}