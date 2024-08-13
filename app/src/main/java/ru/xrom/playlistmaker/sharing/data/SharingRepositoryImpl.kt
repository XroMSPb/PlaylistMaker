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
            url = getString(context, R.string.practikumLink),
            title = getString(context, R.string.practikumHeader)
        )
    }

    override fun getMailData(): MailData {
        return MailData(
            mail = getString(context, R.string.supportMail),
            subject = getString(context, R.string.suportSubject),
            text = getString(context, R.string.supportText),
            title = getString(context, R.string.practikumHeader)
        )
    }

    override fun getTermsData(): TermsData {
        return TermsData(
            link = getString(context, R.string.practikumLink),
        )
    }
}