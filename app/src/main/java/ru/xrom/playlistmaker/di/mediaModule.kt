package ru.xrom.playlistmaker.di

import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.dsl.module
import ru.xrom.playlistmaker.media.ui.FavoritesViewModel
import ru.xrom.playlistmaker.media.ui.PlaylistViewModel

val mediaModule = module {
    viewModel {
        FavoritesViewModel()
    }

    viewModel {
        PlaylistViewModel()
    }
}