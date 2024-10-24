package ru.xrom.playlistmaker.di

import androidx.room.Room
import org.koin.android.ext.koin.androidContext
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.dsl.module
import ru.xrom.playlistmaker.media.data.converter.TrackDBConverter
import ru.xrom.playlistmaker.media.data.db.AppDatabase
import ru.xrom.playlistmaker.media.data.repository.FavoritesRepositoryImpl
import ru.xrom.playlistmaker.media.domain.db.FavoritesInteractor
import ru.xrom.playlistmaker.media.domain.db.FavoritesRepository
import ru.xrom.playlistmaker.media.domain.impl.FavoritesInteractorImpl
import ru.xrom.playlistmaker.media.ui.FavoritesViewModel
import ru.xrom.playlistmaker.media.ui.PlaylistViewModel

val mediaModule = module {
    viewModel {
        FavoritesViewModel(get())
    }

    viewModel {
        PlaylistViewModel()
    }

    single {
        Room.databaseBuilder(androidContext(), AppDatabase::class.java, "database.db").build()
    }

    single {
        TrackDBConverter()
    }

    single<FavoritesRepository> {
        FavoritesRepositoryImpl(get(), get())
    }

    single<FavoritesInteractor> {
        FavoritesInteractorImpl(get())
    }
}