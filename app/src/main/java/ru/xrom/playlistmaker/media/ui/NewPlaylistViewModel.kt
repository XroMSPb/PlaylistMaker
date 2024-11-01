package ru.xrom.playlistmaker.media.ui

import android.app.Application
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Environment
import androidx.lifecycle.ViewModel
import ru.xrom.playlistmaker.media.domain.api.PlaylistInteractor
import java.io.File
import java.io.FileOutputStream

class NewPlaylistViewModel(
    private val interactor: PlaylistInteractor,
    private val application: Application,
) : ViewModel() {
    fun createPlaylist(playlistName: String, playlistDescription: String, playlistImage: String) =
        interactor.createPlaylist(playlistName, playlistDescription, playlistImage)

    fun saveImageToPrivateStorage(uri: Uri, name: String) {
        val filePath =
            File(application.getExternalFilesDir(Environment.DIRECTORY_PICTURES), "cache")
        if (!filePath.exists()) {
            filePath.mkdirs()
        }
        val file = File(filePath, name)
        val inputStream = application.contentResolver.openInputStream(uri)
        val outputStream = FileOutputStream(file)
        BitmapFactory.decodeStream(inputStream)
            .compress(Bitmap.CompressFormat.JPEG, 30, outputStream)
    }
}