package ru.xrom.playlistmaker.di

import android.content.Context
import com.google.gson.Gson
import org.koin.android.ext.koin.androidContext
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.dsl.module
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import ru.xrom.playlistmaker.search.data.NetworkClient
import ru.xrom.playlistmaker.search.data.network.ItunesApi
import ru.xrom.playlistmaker.search.data.network.RetrofitNetworkClient
import ru.xrom.playlistmaker.search.data.repository.SearchHistoryRepositoryImpl
import ru.xrom.playlistmaker.search.data.repository.TrackRepositoryImpl
import ru.xrom.playlistmaker.search.domain.api.SearchHistoryInteractor
import ru.xrom.playlistmaker.search.domain.api.SearchHistoryRepository
import ru.xrom.playlistmaker.search.domain.api.TrackInteractor
import ru.xrom.playlistmaker.search.domain.api.TrackRepository
import ru.xrom.playlistmaker.search.domain.impl.SearchHistoryInteractorImpl
import ru.xrom.playlistmaker.search.domain.impl.TrackInteractorImpl
import ru.xrom.playlistmaker.search.ui.SearchViewModel
import ru.xrom.playlistmaker.utils.PLAYLISTMAKER_PREFERENCES

val searchModule = module {

    single {
        androidContext().getSharedPreferences(PLAYLISTMAKER_PREFERENCES, Context.MODE_PRIVATE)
    }

    factory { Gson() }
    single<ItunesApi> {
        Retrofit.Builder().baseUrl("https://itunes.apple.com/")
            .addConverterFactory(GsonConverterFactory.create()).build()
            .create(ItunesApi::class.java)
    }

    single<NetworkClient> {
        RetrofitNetworkClient()
    }

    single<SearchHistoryRepository> {
        SearchHistoryRepositoryImpl(get())
    }

    single<SearchHistoryInteractor> {
        SearchHistoryInteractorImpl(get())
    }

    single<TrackInteractor> {
        TrackInteractorImpl(get())
    }

    single<TrackRepository> {
        TrackRepositoryImpl(get())
    }

    viewModel {
        SearchViewModel(get(), get(), get())
    }
}