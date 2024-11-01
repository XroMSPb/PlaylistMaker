package ru.xrom.playlistmaker.media.ui

import android.os.Environment
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import androidx.core.net.toUri
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import ru.xrom.playlistmaker.R
import ru.xrom.playlistmaker.media.ui.model.Playlist
import ru.xrom.playlistmaker.utils.convertDpToPx
import java.io.File

class PlaylistViewHolder(view: View) : RecyclerView.ViewHolder(view) {

    private val cover: ImageView = itemView.findViewById(R.id.playlist_cover)
    private val name: TextView = itemView.findViewById(R.id.playlist_name)
    private val numTracks: TextView = itemView.findViewById(R.id.playlist_tracks)

    fun bind(playlist: Playlist) {
        if (playlist.imagePath.isNotEmpty()) {

            cover.scaleType = ImageView.ScaleType.CENTER_CROP
            val filePath =
                File(itemView.context.getExternalFilesDir(Environment.DIRECTORY_PICTURES), "cache")
            val file = File(filePath, playlist.imagePath)
            //cover.setImageURI(file.toUri())
            Glide.with(itemView)
                .load(file.toUri())
                .placeholder(R.drawable.ic_cover_placeholder)
                //.centerCrop()
                .transform(RoundedCorners(convertDpToPx(8f, itemView.context)))
                .into(cover)
        }
        name.text = playlist.name
        numTracks.text = "${playlist.tracks.size} треков"
    }
}
