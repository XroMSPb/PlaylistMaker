package com.example.playlistmaker.settings.di

import android.content.Context
import android.content.SharedPreferences
import org.koin.android.ext.koin.androidContext
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.dsl.module
import ru.xrom.playlistmaker.settings.data.MainThemeInteractorImpl
import ru.xrom.playlistmaker.settings.domain.MainThemeInteractor
import ru.xrom.playlistmaker.settings.ui.SettingsViewModel
import ru.xrom.playlistmaker.utils.PLAYLISTMAKER_PREFERENCES


val settingsModule = module {
    factory<MainThemeInteractor> {
        MainThemeInteractorImpl(get())
    }

    single<SharedPreferences> {
        androidContext()
            .getSharedPreferences(PLAYLISTMAKER_PREFERENCES, Context.MODE_PRIVATE)
    }

    viewModel {
        SettingsViewModel(get(), get())
    }
}