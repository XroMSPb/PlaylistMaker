package ru.xrom.playlistmaker.settings.data

import android.content.SharedPreferences
import androidx.core.content.edit
import ru.xrom.playlistmaker.settings.domain.MainThemeInteractor

class MainThemeInteractorImpl(private val sharedPrefs: SharedPreferences) : MainThemeInteractor {
    private var darkTheme = false

    override fun isNightTheme(): Boolean {
        return sharedPrefs.getBoolean(DARK_THEME_ENABLED, darkTheme)
    }

    override fun saveTheme(isNightTheme: Boolean) {
        darkTheme = isNightTheme
        sharedPrefs.edit { putBoolean(DARK_THEME_ENABLED, darkTheme) }
    }

    companion object {
        private const val DARK_THEME_ENABLED = "darktheme_enabled"
    }
}