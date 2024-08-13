package ru.xrom.playlistmaker.utils

import android.app.Application
import android.content.SharedPreferences
import androidx.appcompat.app.AppCompatDelegate
import ru.xrom.playlistmaker.utils.Creator.initApplication
import ru.xrom.playlistmaker.utils.Creator.provideSharedPreferences


const val DARKTHEME_ENABLED = "darktheme_enabled"

class App : Application() {
    var darkTheme = false
        private set

    private lateinit var sharedPrefs: SharedPreferences

    override fun onCreate() {
        super.onCreate()
        initApplication(this)
        sharedPrefs = provideSharedPreferences()
        switchTheme(sharedPrefs.getBoolean(DARKTHEME_ENABLED, darkTheme))
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