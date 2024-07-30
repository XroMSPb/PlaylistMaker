package ru.xrom.playlistmaker.data.dto

class ItunesResponse(
    val searchType: String,
    val expression: String,
    val results: List<TrackDto>,
) : Response()
