package ru.xrom.playlistmaker.utils

import android.app.Application
import androidx.appcompat.app.AppCompatDelegate
import org.koin.android.ext.android.inject
import org.koin.android.ext.koin.androidContext
import org.koin.android.ext.koin.androidLogger
import org.koin.core.context.startKoin
import ru.xrom.playlistmaker.di.mediaModule
import ru.xrom.playlistmaker.di.playerModule
import ru.xrom.playlistmaker.di.searchModule
import ru.xrom.playlistmaker.di.settingsModule
import ru.xrom.playlistmaker.settings.domain.MainThemeInteractor


class App : Application() {

    override fun onCreate() {
        super.onCreate()
        startKoin {
            androidLogger()
            androidContext(this@App)
            modules(
                searchModule,
                playerModule,
                settingsModule,
                mediaModule,
            )
        }
        val mainThemeInt: MainThemeInteractor by inject()
        switchTheme(mainThemeInt.isNightTheme())
    }

    fun switchTheme(darkThemeEnabled: Boolean) {
        AppCompatDelegate.setDefaultNightMode(
            if (darkThemeEnabled) {
                AppCompatDelegate.MODE_NIGHT_YES
            } else {
                AppCompatDelegate.MODE_NIGHT_NO
            }
        )
    }

}