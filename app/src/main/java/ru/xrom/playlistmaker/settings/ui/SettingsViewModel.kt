package ru.xrom.playlistmaker.settings.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import ru.xrom.playlistmaker.sharing.domain.api.SharingRepository
import ru.xrom.playlistmaker.utils.Creator


class SettingsViewModel(sharingRepository: SharingRepository) : ViewModel() {
    companion object {
        fun getViewModelFactory(): ViewModelProvider.Factory = viewModelFactory {
            initializer {
                SettingsViewModel(Creator.provideSharingRepositoryInteractor())
            }
        }
    }

}