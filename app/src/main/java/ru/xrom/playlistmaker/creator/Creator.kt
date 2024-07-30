package ru.xrom.playlistmaker.creator

import android.app.Application
import android.content.Context.MODE_PRIVATE
import android.content.SharedPreferences
import ru.xrom.playlistmaker.data.network.RetrofitNetworkClient
import ru.xrom.playlistmaker.data.repository.SearchHistoryRepositoryImpl
import ru.xrom.playlistmaker.data.repository.TrackRepositoryImpl
import ru.xrom.playlistmaker.domain.api.TrackInteractor
import ru.xrom.playlistmaker.domain.api.TrackRepository
import ru.xrom.playlistmaker.domain.impl.TrackInteractorImpl

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


    fun provideSearchHistoryRepository(): SearchHistoryRepositoryImpl {
        return SearchHistoryRepositoryImpl(provideSharedPreferences())
    }

    fun provideSharedPreferences(): SharedPreferences {
        return application.getSharedPreferences(PLAYLISTMAKER_PREFERENCES, MODE_PRIVATE)
    }

}