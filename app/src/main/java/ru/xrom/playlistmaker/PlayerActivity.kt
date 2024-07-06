package ru.xrom.playlistmaker

import android.icu.text.SimpleDateFormat
import android.media.MediaPlayer
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import ru.xrom.playlistmaker.utils.Track
import ru.xrom.playlistmaker.utils.dpToPx
import java.util.Locale

class PlayerActivity : AppCompatActivity() {

    companion object {
        private const val STATE_DEFAULT = 0
        private const val STATE_PREPARED = 1
        private const val STATE_PLAYING = 2
        private const val STATE_PAUSED = 3
        private const val TIMER_UPDATE_DELAY = 250L
    }

    private var playerState = STATE_DEFAULT
    private var mediaPlayer = MediaPlayer()
    private lateinit var playButton: ImageButton
    private lateinit var playingTime: TextView
    private val handler = Handler(Looper.getMainLooper())
    private val timerRunnable by lazy {
        object : Runnable {
            override fun run() {
                if (playerState == STATE_PLAYING) {
                    playingTime.text = dateFormat.format(mediaPlayer.currentPosition)
                    handler.postDelayed(this, TIMER_UPDATE_DELAY)
                }
            }
        }
    }
    private val dateFormat by lazy { SimpleDateFormat("mm:ss", Locale.getDefault()) }

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
        playingTime = findViewById(R.id.playing_time)
        val duration = findViewById<TextView>(R.id.duration)
        val album = findViewById<TextView>(R.id.album)
        val year = findViewById<TextView>(R.id.year)
        val genre = findViewById<TextView>(R.id.genre)
        val country = findViewById<TextView>(R.id.country)
        playButton = findViewById(R.id.play_btn)
        playButton.isEnabled = false

        if (track != null) {
            preparePlayer(track.previewUrl)
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
            playButton.setOnClickListener {
                playbackControl()
            }
        } else {
            albumCover.setImageResource(R.drawable.ic_nothing_found)
        }
    }

    override fun onPause() {
        super.onPause()
        pausePlayer()
    }

    override fun onDestroy() {
        super.onDestroy()
        mediaPlayer.release()
        handler.removeCallbacks(timerRunnable)
    }

    private fun preparePlayer(url: String) {
        mediaPlayer.setDataSource(url)
        mediaPlayer.prepareAsync()
        mediaPlayer.setOnPreparedListener {
            playButton.isEnabled = true
            playerState = STATE_PREPARED
        }
        mediaPlayer.setOnCompletionListener {
            handler.removeCallbacks(timerRunnable)
            playerState = STATE_PREPARED
            playButton.setImageResource(R.drawable.ic_play)
            playingTime.text = dateFormat.format(0)
        }
    }

    private fun playbackControl() {
        when (playerState) {
            STATE_PLAYING -> {
                pausePlayer()
            }

            STATE_PREPARED, STATE_PAUSED -> {
                startPlayer()
            }
        }
    }

    private fun startPlayer() {
        mediaPlayer.start()
        playButton.setImageResource(R.drawable.ic_pause)
        playerState = STATE_PLAYING
        handler.post(timerRunnable)
    }

    private fun pausePlayer() {
        mediaPlayer.pause()
        playButton.setImageResource(R.drawable.ic_play)
        playerState = STATE_PAUSED
        handler.removeCallbacks(timerRunnable)
    }

}