package ru.xrom.playlistmaker.utils

import android.app.Application
import android.content.Context.MODE_PRIVATE
import android.content.SharedPreferences
import android.media.MediaPlayer
import ru.xrom.playlistmaker.player.data.TrackPlayerImpl
import ru.xrom.playlistmaker.player.domain.api.TrackPlayerInteractor
import ru.xrom.playlistmaker.search.data.network.RetrofitNetworkClient
import ru.xrom.playlistmaker.search.data.repository.SearchHistoryRepositoryImpl
import ru.xrom.playlistmaker.search.data.repository.TrackRepositoryImpl
import ru.xrom.playlistmaker.search.domain.api.SearchHistoryInteractor
import ru.xrom.playlistmaker.search.domain.api.SearchHistoryRepository
import ru.xrom.playlistmaker.search.domain.api.TrackInteractor
import ru.xrom.playlistmaker.search.domain.api.TrackRepository
import ru.xrom.playlistmaker.search.domain.impl.SearchHistoryInteractorImpl
import ru.xrom.playlistmaker.search.domain.impl.TrackInteractorImpl
import ru.xrom.playlistmaker.settings.data.MainThemeInteractorImpl
import ru.xrom.playlistmaker.settings.domain.MainThemeInteractor
import ru.xrom.playlistmaker.sharing.data.SharingRepositoryImpl
import ru.xrom.playlistmaker.sharing.domain.api.SharingRepository

object Creator {
    private const val PLAYLISTMAKER_PREFERENCES = "_preferences"

    private lateinit var application: Application

    fun initApplication(application: Application) {
        this.application = application
    }

    private fun getTrackRepository(): TrackRepository {
        return TrackRepositoryImpl(RetrofitNetworkClient())
    }

    fun provideTrackInteractor(): TrackInteractor {
        return TrackInteractorImpl(getTrackRepository())
    }

    fun provideSearchHistoryGetHistoryInteractor(): SearchHistoryInteractor {
        return SearchHistoryInteractorImpl(provideSearchHistoryRepository())
    }

    private fun provideSearchHistoryRepository(): SearchHistoryRepository {
        return SearchHistoryRepositoryImpl(provideSharedPreferences())
    }

    fun provideSharedPreferences(): SharedPreferences {
        return application.getSharedPreferences(PLAYLISTMAKER_PREFERENCES, MODE_PRIVATE)
    }

    private fun provideMediaPlayer(): MediaPlayer {
        return MediaPlayer()
    }

    fun provideTrackPlayerInteractor(trackUrl: String): TrackPlayerInteractor {
        return TrackPlayerImpl(
            provideMediaPlayer(),
            trackUrl
        )
    }

    fun provideSharingRepositoryInteractor(): SharingRepository {
        return SharingRepositoryImpl(application.applicationContext)
    }

    fun provideMainThemeInteractor(): MainThemeInteractor {
        return MainThemeInteractorImpl(provideSharedPreferences())
    }
}
