package ru.xrom.playlistmaker.settings.ui

import android.app.Application
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import ru.xrom.playlistmaker.R
import ru.xrom.playlistmaker.settings.domain.MainThemeInteractor
import ru.xrom.playlistmaker.sharing.domain.api.SharingRepository
import ru.xrom.playlistmaker.sharing.domain.model.MailData
import ru.xrom.playlistmaker.sharing.domain.model.ShareData
import ru.xrom.playlistmaker.sharing.domain.model.TermsData
import ru.xrom.playlistmaker.utils.SingleLiveEvent

class SettingsViewModel(
    private val application: Application,
    private val themeInteractor: MainThemeInteractor,
) : ViewModel(), SharingRepository {

    private val termsState = SingleLiveEvent<TermsData>()
    private val shareState = SingleLiveEvent<ShareData>()
    private val supportState = SingleLiveEvent<MailData>()

    init {
        termsState.postValue(getTermsData())
        shareState.postValue(getShareData())
        supportState.postValue(getMailData())
    }

    fun observeTermsState(): LiveData<TermsData> = termsState
    fun observeShareState(): LiveData<ShareData> = shareState
    fun observeSupportState(): LiveData<MailData> = supportState

    private val isNightThemeEnabled = MutableLiveData(themeInteractor.isNightTheme())
    fun updateThemeState(isNightTheme: Boolean) {
        themeInteractor.saveTheme(isNightTheme)
        isNightThemeEnabled.postValue(isNightTheme)
    }

    fun observeThemeState(): LiveData<Boolean> = isNightThemeEnabled

    override fun getShareData(): ShareData {
        return ShareData(
            url = application.getString(R.string.practikumLink),
            title = application.getString(R.string.practikumHeader)
        )
    }

    override fun getMailData(): MailData {
        return MailData(
            mail = application.getString(R.string.supportMail),
            subject = application.getString(R.string.suportSubject),
            text = application.getString(R.string.supportText),
            title = application.getString(R.string.practikumHeader)
        )
    }

    override fun getTermsData(): TermsData {
        return TermsData(
            link = application.getString(R.string.termsLink),
        )
    }
}