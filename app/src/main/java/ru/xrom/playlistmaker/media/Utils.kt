package ru.xrom.playlistmaker.media

import android.content.Context
import android.util.TypedValue

fun dpToPx(dp: Float, context: Context): Int {
    return TypedValue.applyDimension(
        TypedValue.COMPLEX_UNIT_DIP,
        dp,
        context.resources.displayMetrics
    ).toInt()
}

fun getReleaseYear(str: String): String {
    return if (str.length >= 4) {
        str.substring(0, 4)
    } else {
        "1900"
    }
}

