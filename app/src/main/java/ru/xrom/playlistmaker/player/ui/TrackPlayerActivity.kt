package ru.xrom.playlistmaker.player.ui

import android.icu.text.SimpleDateFormat
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.content.res.AppCompatResources
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import org.koin.androidx.viewmodel.ext.android.viewModel
import org.koin.core.parameter.parametersOf
import ru.xrom.playlistmaker.R
import ru.xrom.playlistmaker.databinding.ActivityPlayerBinding
import ru.xrom.playlistmaker.player.domain.model.PlayingState
import ru.xrom.playlistmaker.search.domain.model.Track
import ru.xrom.playlistmaker.search.ui.SearchActivity
import ru.xrom.playlistmaker.utils.dpToPx
import ru.xrom.playlistmaker.utils.getReleaseYear
import java.util.Locale

class TrackPlayerActivity : AppCompatActivity() {

    private val binding: ActivityPlayerBinding by lazy {
        ActivityPlayerBinding.inflate(layoutInflater)
    }


    private val dateFormat by lazy { SimpleDateFormat("mm:ss", Locale.getDefault()) }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)
        window.statusBarColor = resources.getColor(R.color.status_bar, theme)
        window.navigationBarColor = resources.getColor(R.color.navigation_bar, theme)
        binding.toolbar.setNavigationOnClickListener {
            onBackPressedDispatcher.onBackPressed()
        }
        val track = intent.getParcelableExtra(SearchActivity.TRACK_DATA) as? Track

        if (track != null) {
            val viewModel: TrackPlayerViewModel by viewModel {
                parametersOf(track.previewUrl)
            }
            render(track, viewModel)

            viewModel.observePlayingState().observe(this) { state ->
                binding.playButton.isEnabled = state != PlayingState.Default
                setButtonImage(state)
                viewModel.stateControl()
            }

            viewModel.observePositionState().observe(this) {
                binding.playingTime.text = dateFormat.format(it)
            }

        } else {
            binding.albumCover.setImageResource(R.drawable.ic_nothing_found)
        }
    }

    private fun render(track: Track, viewModel: TrackPlayerViewModel) {
        binding.playButton.isEnabled = false
        Glide.with(this)
            .load(track.getCoverArtwork())
            .placeholder(R.drawable.ic_cover_placeholder)
            .centerCrop()
            .transform(RoundedCorners(dpToPx(8f, this)))
            .into(binding.albumCover)
        binding.title.text = track.trackName
        binding.artistName.text = track.artistName
        binding.playingTime.text = getString(R.string.time_zero)
        binding.duration.text = dateFormat.format(track.trackTimeMillis)
        binding.album.text = track.collectionName
        binding.year.text = getReleaseYear(track.releaseDate)
        binding.genre.text = track.primaryGenreName
        binding.country.text = track.country
        binding.playButton.setOnClickListener {
            viewModel.playingControl()
        }
    }

    private fun setButtonImage(state: PlayingState) {
        binding.playButton.setImageDrawable(
            AppCompatResources.getDrawable(
                this, when (state) {
                    PlayingState.Default,
                    PlayingState.Prepared,
                    PlayingState.Paused,
                    PlayingState.Complete,
                    -> R.drawable.ic_play

                    PlayingState.Playing -> R.drawable.ic_pause
                }
            )
        )
    }

}