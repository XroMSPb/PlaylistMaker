package ru.xrom.playlistmaker.search.data

import ru.xrom.playlistmaker.search.data.dto.Response

interface NetworkClient {
    fun doRequest(dto: Any): Response
}