package ru.xrom.playlistmaker.sharing.domain.api

import ru.xrom.playlistmaker.sharing.domain.model.MailData
import ru.xrom.playlistmaker.sharing.domain.model.ShareData
import ru.xrom.playlistmaker.sharing.domain.model.TermsData

interface SharingRepository {
    fun getShareData(): ShareData
    fun getMailData(): MailData
    fun getTermsData(): TermsData
}
