package ru.xrom.playlistmaker.utils

import android.app.Application
import androidx.appcompat.app.AppCompatDelegate
import com.example.playlistmaker.media_player.di.playerModule
import com.example.playlistmaker.settings.di.settingsModule
import org.koin.android.ext.android.inject
import org.koin.android.ext.koin.androidContext
import org.koin.android.ext.koin.androidLogger
import org.koin.core.context.startKoin
import ru.xrom.playlistmaker.di.mediaModule
import ru.xrom.playlistmaker.di.searchModule
import ru.xrom.playlistmaker.settings.domain.MainThemeInteractor


class App : Application() {
    var darkTheme = false
        private set

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
        darkTheme = darkThemeEnabled
        AppCompatDelegate.setDefaultNightMode(
            if (darkThemeEnabled) {
                AppCompatDelegate.MODE_NIGHT_YES
            } else {
                AppCompatDelegate.MODE_NIGHT_NO
            }
        )
    }

}