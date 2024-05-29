package ru.xrom.playlistmaker

import android.app.Application
import android.content.SharedPreferences
import androidx.appcompat.app.AppCompatDelegate

const val PLAYLISTMAKER_PREFERENCES = "_preferences"
const val DARKTHEME_ENABLED = "darktheme_enabled"

class App : Application() {
    var darkTheme = false
        private set(value) {
            field = value
        }

    private lateinit var sharedPrefs: SharedPreferences

    override fun onCreate() {
        super.onCreate()
        sharedPrefs = getSharedPreferences(
            PLAYLISTMAKER_PREFERENCES, MODE_PRIVATE
        )
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