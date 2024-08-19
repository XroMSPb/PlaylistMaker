package ru.xrom.playlistmaker.settings.data

import android.content.SharedPreferences
import androidx.core.content.edit
import ru.xrom.playlistmaker.settings.domain.MainThemeInteractor
import ru.xrom.playlistmaker.utils.DARKTHEME_ENABLED

class MainThemeInteractorImpl(val sharedPrefs: SharedPreferences) : MainThemeInteractor {
    private var darkTheme = false


    override fun isNightTheme(): Boolean {
        return sharedPrefs.getBoolean(DARKTHEME_ENABLED, darkTheme)
    }

    override fun saveTheme(isNightTheme: Boolean) {
        darkTheme = isNightTheme
        sharedPrefs.edit { putBoolean(DARKTHEME_ENABLED, darkTheme) }
    }
}