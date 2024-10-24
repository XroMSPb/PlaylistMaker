package ru.xrom.playlistmaker.utils

import android.content.Context
import android.util.TypedValue
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

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
