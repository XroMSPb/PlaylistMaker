package ru.xrom.playlistmaker.utils

import android.content.Context
import android.os.Environment
import android.util.TypedValue
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.io.File

const val PLAYLISTMAKER_PREFERENCES = "_preferences"

fun convertDpToPx(dp: Float, context: Context): Int {
    return TypedValue.applyDimension(
        TypedValue.COMPLEX_UNIT_DIP, dp, context.resources.displayMetrics
    ).toInt()
}

fun getReleaseYear(str: String?): String {
    return if (str != null && str.length >= 4) {
        str.substring(0, 4)
    } else {
        "1900"
    }
}

fun getPreviewUrl(url: String?): String {
    return if (!url.isNullOrEmpty()) {
        url
    } else {
        "https://audio-ssl.itunes.apple.com/itunes-assets/AudioPreview126/v4/48/8f/a4/488fa4b5-b606-71ee-572e-691f840503c8/mzaf_15586272016916254191.plus.aac.p.m4a"
    }
}

fun <T> debounce(
    delayMillis: Long,
    coroutineScope: CoroutineScope,
    useLastParam: Boolean,
    action: (T) -> Unit,
): (T) -> Unit {
    var debounceJob: Job? = null
    return { param: T ->
        if (useLastParam) {
            debounceJob?.cancel()
        }
        if (debounceJob?.isCompleted != false) {
            debounceJob = coroutineScope.launch {
                if (useLastParam) {
                    delay(delayMillis)
                    action(param)
                } else {
                    action(param)
                    delay(delayMillis)
                }
            }
        }
    }
}

fun getFileNameFromText(fileName: String): String {
    return fileName.replace(Regex("[^a-zA-Zа-яА-Я0-9 _.-]"), "").lowercase()
}

fun getDefaultCacheImagePath(context: Context): File =
    File(context.getExternalFilesDir(Environment.DIRECTORY_PICTURES), "cache")
