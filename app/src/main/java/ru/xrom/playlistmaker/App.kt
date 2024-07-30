package ru.xrom.playlistmaker

import android.app.Application
import android.content.SharedPreferences
import androidx.appcompat.app.AppCompatDelegate
import ru.xrom.playlistmaker.creator.Creator.provideSharedPreferences


const val DARKTHEME_ENABLED = "darktheme_enabled"

class App : Application() {
    var darkTheme = false
        private set

    private lateinit var sharedPrefs: SharedPreferences

    override fun onCreate() {
        super.onCreate()
        sharedPrefs = provideSharedPreferences()
        switchTheme(sharedPrefs.getBoolean(DARKTHEME_ENABLED, darkTheme))
    }

    fun switchTheme(darkThemeEnabled: Boolean) {
        darkTheme = darkThemeEnabled
        sharedPrefs.edit().putBoolean(DARKTHEME_ENABLED, darkTheme).apply()
        AppCompatDelegate.setDefaultNightMode(
            if (darkThemeEnabled) {
                AppCompatDelegate.MODE_NIGHT_YES
            } else {
                AppCompatDelegate.MODE_NIGHT_NO
            }
        )
    }
}