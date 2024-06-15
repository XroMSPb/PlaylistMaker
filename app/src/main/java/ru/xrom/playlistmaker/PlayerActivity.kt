package ru.xrom.playlistmaker

import android.icu.text.SimpleDateFormat
import android.os.Bundle
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import java.util.Locale

class PlayerActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_player)
        window.statusBarColor = resources.getColor(R.color.status_bar, theme)
        window.navigationBarColor = resources.getColor(R.color.navigation_bar, theme)
        val toolbar = findViewById<Toolbar>(R.id.toolbar)
        toolbar.setNavigationOnClickListener {
            onBackPressedDispatcher.onBackPressed()
        }
        val track = intent.getParcelableExtra(SearchActivity.TRACK_DATA) as? Track
        val albumCover = findViewById<ImageView>(R.id.album_cover)
        val title = findViewById<TextView>(R.id.title)
        val artistName = findViewById<TextView>(R.id.artist_name)
        val playingTime = findViewById<TextView>(R.id.playing_time)
        val duration = findViewById<TextView>(R.id.duration)
        val album = findViewById<TextView>(R.id.album)
        val year = findViewById<TextView>(R.id.year)
        val genre = findViewById<TextView>(R.id.genre)
        val country = findViewById<TextView>(R.id.country)
        if (track != null) {
            Glide.with(this)
                .load(track.getCoverArtwork())
                .placeholder(R.drawable.ic_cover_placeholder)
                .centerCrop()
                .transform(RoundedCorners(dpToPx(8f, this)))
                .into(albumCover)
            title.text = track.trackName
            artistName.text = track.artistName
            playingTime.text = getString(R.string.time_zero)
            duration.text =
                SimpleDateFormat("mm:ss", Locale.getDefault()).format(track.trackTimeMillis)
            album.text = track.collectionName
            year.text = track.releaseDate.substring(0, 4)
            genre.text = track.primaryGenreName
            country.text = track.country
        } else {
            albumCover.setImageResource(R.drawable.ic_nothing_found)
        }

    }

}