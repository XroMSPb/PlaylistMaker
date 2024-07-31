package ru.xrom.playlistmaker.domain.impl

import ru.xrom.playlistmaker.domain.api.TrackInteractor
import ru.xrom.playlistmaker.domain.api.TrackRepository
import java.util.concurrent.Executors

class TrackInteractorImpl(private val repository: TrackRepository) : TrackInteractor {
    private val executor = Executors.newCachedThreadPool()

    override fun search(expression: String, consumer: TrackInteractor.TrackConsumer) {
        executor.execute {
            try {
                consumer.consume(repository.searchTracks(expression))
            } catch (throwable: Throwable) {
                consumer.onFailure(throwable)
            }

        }
    }
}