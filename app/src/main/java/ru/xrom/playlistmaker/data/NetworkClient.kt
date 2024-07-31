package ru.xrom.playlistmaker.data

import ru.xrom.playlistmaker.data.dto.Response

interface NetworkClient {
    fun doRequest(dto: Any): Response
}