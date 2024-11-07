package ru.xrom.playlistmaker.media.ui

import android.app.Application
import android.graphics.Bitmap
import android.net.Uri
import android.os.Environment
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import ru.xrom.playlistmaker.media.domain.api.PlaylistInteractor
import java.io.File
import java.io.FileOutputStream
import java.util.UUID

class NewPlaylistViewModel(
    private val interactor: PlaylistInteractor,
    private val application: Application,
) : ViewModel() {

    fun createPlaylist(
        playlistName: String,
        playlistDescription: String,
        imageUri: Uri,
        bitmap: Bitmap,
    ): Long {
        var result = 0L
        var playlistImage: String? = null
        if (imageUri != Uri.EMPTY)
            playlistImage = "${UUID.randomUUID()}.png"
        viewModelScope.launch(Dispatchers.IO) {
            result = interactor.createPlaylist(playlistName, playlistDescription, playlistImage)
            if (result > 0 && imageUri != Uri.EMPTY) {
                saveImageToPrivateStorage(
                    bitmap,
                    playlistImage!!
                )
            }
        }
        return result
    }

    private fun saveImageToPrivateStorage(bitmap: Bitmap, fileName: String): Boolean {
        if (fileName.isEmpty()) return false
        val filePath =
            File(application.getExternalFilesDir(Environment.DIRECTORY_PICTURES), "cache")
        if (!filePath.exists()) {
            filePath.mkdirs()
        }
        val file = File(filePath, fileName)
        val outputStream = FileOutputStream(file)
        return bitmap
            .compress(Bitmap.CompressFormat.JPEG, 30, outputStream)
    }
}