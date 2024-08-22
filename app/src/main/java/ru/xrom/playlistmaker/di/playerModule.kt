package com.example.playlistmaker.media_player.di

import android.media.MediaPlayer
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.core.parameter.parametersOf
import org.koin.dsl.module
import ru.xrom.playlistmaker.player.data.TrackPlayerImpl
import ru.xrom.playlistmaker.player.domain.api.TrackPlayerInteractor
import ru.xrom.playlistmaker.player.ui.TrackPlayerViewModel

val playerModule = module {
    factory<MediaPlayer> { MediaPlayer() }

    factory<TrackPlayerInteractor> {
        TrackPlayerImpl(get<MediaPlayer>(), get<String>())
    }

    viewModel { (url: String) ->
        TrackPlayerViewModel(
            trackPlayerInteractor = get<TrackPlayerInteractor>(parameters = { parametersOf(url) }),
        )
    }
}