package ru.xrom.playlistmaker.sharing.data

import android.content.Context
import androidx.core.content.ContextCompat.getString
import ru.xrom.playlistmaker.R
import ru.xrom.playlistmaker.sharing.domain.api.SharingRepository
import ru.xrom.playlistmaker.sharing.domain.model.MailData
import ru.xrom.playlistmaker.sharing.domain.model.ShareData
import ru.xrom.playlistmaker.sharing.domain.model.TermsData

class SharingRepositoryImpl(private val context: Context) : SharingRepository {
    override fun getShareData(): ShareData {
        return ShareData(
            getString(context, R.string.practikumLink),
            getString(context, R.string.practikumHeader)
        )
    }

    override fun getMailData(): MailData {
        return MailData(
            "mailto:",
            getString(context, R.string.supportMail),
            getString(context, R.string.suportSubject),
            getString(context, R.string.supportText),
        )
    }

    override fun getTermsData(): TermsData {
        return TermsData(
            getString(context, R.string.practikumLink),
        )
    }
}