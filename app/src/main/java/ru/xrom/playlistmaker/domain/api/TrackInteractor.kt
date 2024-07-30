package ru.xrom.playlistmaker.domain.api

import ru.xrom.playlistmaker.domain.model.Track

interface TrackInteractor {
    fun search(expression: String, consumer: TrackConsumer)

    interface TrackConsumer {
        fun consume(foundTracks: List<Track>)
        fun onFailure(t: Throwable)
    }
}