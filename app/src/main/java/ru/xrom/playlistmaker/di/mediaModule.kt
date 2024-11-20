package ru.xrom.playlistmaker.di

import androidx.room.Room
import org.koin.android.ext.koin.androidContext
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.dsl.module
import ru.xrom.playlistmaker.media.data.converter.PlaylistDBConverter
import ru.xrom.playlistmaker.media.data.converter.TrackAtPlaylistDBConverter
import ru.xrom.playlistmaker.media.data.converter.TrackDBConverter
import ru.xrom.playlistmaker.media.data.db.AppDatabase
import ru.xrom.playlistmaker.media.data.repository.FavoritesRepositoryImpl
import ru.xrom.playlistmaker.media.data.repository.PlaylistRepositoryImpl
import ru.xrom.playlistmaker.media.domain.api.FavoritesInteractor
import ru.xrom.playlistmaker.media.domain.api.FavoritesRepository
import ru.xrom.playlistmaker.media.domain.api.PlaylistInteractor
import ru.xrom.playlistmaker.media.domain.api.PlaylistRepository
import ru.xrom.playlistmaker.media.domain.impl.FavoritesInteractorImpl
import ru.xrom.playlistmaker.media.domain.impl.PlaylistInteractorImpl
import ru.xrom.playlistmaker.media.ui.FavoritesViewModel
import ru.xrom.playlistmaker.media.ui.NewPlaylistViewModel
import ru.xrom.playlistmaker.media.ui.PlaylistViewModel
import ru.xrom.playlistmaker.media.ui.PlaylistViewerViewModel

val mediaModule = module {
    viewModel {
        FavoritesViewModel(get())
    }

    viewModel {
        PlaylistViewModel(get())
    }

    viewModel {
        NewPlaylistViewModel(get(), get())
    }

    viewModel {
        PlaylistViewerViewModel(get())
    }

    single {
        Room.databaseBuilder(androidContext(), AppDatabase::class.java, "database.db").build()
    }

    single {
        TrackDBConverter()
    }

    single {
        PlaylistDBConverter()
    }
    single {
        TrackAtPlaylistDBConverter()
    }

    single<FavoritesRepository> {
        FavoritesRepositoryImpl(get(), get())
    }

    single<FavoritesInteractor> {
        FavoritesInteractorImpl(get())
    }

    single<PlaylistRepository> {
        PlaylistRepositoryImpl(get(), get(), get())
    }

    single<PlaylistInteractor> {
        PlaylistInteractorImpl(get())
    }
}