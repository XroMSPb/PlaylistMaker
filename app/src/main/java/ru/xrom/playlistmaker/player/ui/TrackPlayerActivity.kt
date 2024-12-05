package ru.xrom.playlistmaker.player.ui

import android.content.Context
import android.content.Intent
import android.icu.text.SimpleDateFormat
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.content.res.AppCompatResources
import androidx.core.content.IntentCompat
import androidx.lifecycle.lifecycleScope
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.google.android.material.bottomsheet.BottomSheetBehavior
import org.koin.androidx.viewmodel.ext.android.viewModel
import org.koin.core.parameter.parametersOf
import ru.xrom.playlistmaker.R
import ru.xrom.playlistmaker.databinding.ActivityPlayerBinding
import ru.xrom.playlistmaker.media.ui.NewPlaylistFragment
import ru.xrom.playlistmaker.media.ui.model.Playlist
import ru.xrom.playlistmaker.player.ui.model.PlayerState
import ru.xrom.playlistmaker.search.domain.model.Track
import ru.xrom.playlistmaker.utils.convertDpToPx
import ru.xrom.playlistmaker.utils.debounce
import ru.xrom.playlistmaker.utils.getPreviewUrl
import ru.xrom.playlistmaker.utils.getReleaseYear
import java.util.Locale

class TrackPlayerActivity : AppCompatActivity() {

    private val binding: ActivityPlayerBinding by lazy {
        ActivityPlayerBinding.inflate(layoutInflater)
    }

    private val dateFormat by lazy { SimpleDateFormat(TIME_PATTERN, Locale.getDefault()) }

    private var bottomSheetState = BottomSheetBehavior.STATE_HIDDEN

    private lateinit var onPlaylistClickDebounce: (Playlist) -> Unit


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)
        window.statusBarColor = resources.getColor(R.color.status_bar, theme)
        window.navigationBarColor = resources.getColor(R.color.navigation_bar, theme)
        binding.toolbar.setNavigationOnClickListener {
            onBackPressedDispatcher.onBackPressed()
        }
        val track = IntentCompat.getParcelableExtra(intent, TRACK_KEY, Track::class.java)

        if (track != null) {
            val viewModel: TrackPlayerViewModel by viewModel {
                parametersOf(getPreviewUrl(track.previewUrl))
            }
            render(track, viewModel)

            val bottomSheetContainer = binding.playlistsBottomSheet
            val bottomSheetBehavior = BottomSheetBehavior.from(bottomSheetContainer)
            bottomSheetBehavior.state = BottomSheetBehavior.STATE_HIDDEN
            bottomSheetBehavior.addBottomSheetCallback(object :
                BottomSheetBehavior.BottomSheetCallback() {
                override fun onStateChanged(bottomSheet: View, newState: Int) {
                    when (newState) {
                        BottomSheetBehavior.STATE_COLLAPSED -> {
                            bottomSheetBehavior.peekHeight = binding.root.height / 3 * 2
                            viewModel.updatePlaylists()
                        }

                        BottomSheetBehavior.STATE_HIDDEN -> {
                            binding.overlay.visibility = View.GONE
                        }

                        else -> {
                            binding.overlay.visibility = View.VISIBLE
                            viewModel.updatePlaylists()
                        }
                    }
                }

                override fun onSlide(bottomSheet: View, slideOffset: Float) {
                    binding.overlay.alpha = (slideOffset + 1f) / 2f
                }
            })

            binding.favoriteBtn.setOnClickListener {
                viewModel.onFavoriteClick(track)
            }

            binding.queueBtn.setOnClickListener {
                bottomSheetBehavior.state = BottomSheetBehavior.STATE_COLLAPSED
            }

            binding.createPlaylistButton.setOnClickListener {
                bottomSheetState = bottomSheetBehavior.state
                bottomSheetBehavior.state = BottomSheetBehavior.STATE_HIDDEN

                val fragment = NewPlaylistFragment.newInstance(false)
                supportFragmentManager.beginTransaction()
                    .replace(R.id.fragment_view, fragment)
                    .addToBackStack(null)
                    .commit()
                binding.playerContent.visibility = View.GONE
                binding.fragmentView.visibility = View.VISIBLE
            }

            supportFragmentManager.setFragmentResultListener(
                NewPlaylistFragment.RESULT, this
            ) { _, bundle ->
                bottomSheetBehavior.state = bottomSheetState
                binding.playerContent.visibility = View.VISIBLE
                binding.fragmentView.visibility = View.GONE

            }

            binding.overlay.setOnClickListener {
                bottomSheetBehavior.state = BottomSheetBehavior.STATE_HIDDEN
            }

            viewModel.observeFavoriteState().observe(this) { state ->
                updateFavoriteState(state)
            }

            viewModel.observePlayingState().observe(this) { state ->
                updateState(state)
            }
            val onItemClickListener = OnItemClickListener { item ->
                onPlaylistClickDebounce(item)
            }
            onPlaylistClickDebounce = debounce(
                CLICK_DEBOUNCE_DELAY_MILLIS, lifecycleScope, false
            ) { item ->
                viewModel.onAddToPlaylistClick(track, item)
            }

            viewModel.observeAddingToPlaylistState().observe(this) { state ->
                when (state.isAdded) {
                    true -> {
                        bottomSheetBehavior.state = BottomSheetBehavior.STATE_HIDDEN
                        Toast.makeText(
                            this,
                            getString(R.string.playlist_added).format(state.playlist.name),
                            Toast.LENGTH_SHORT
                        ).show()
                    }

                    false -> {
                        Toast.makeText(
                            this,
                            getString(R.string.playlist_not_added).format(state.playlist.name),
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                }
            }

            viewModel.observePlaylists().observe(this) { state ->
                binding.recyclePlaylistsView.adapter =
                    PlaylistHorizontalAdapter(state, onItemClickListener)
            }


        } else {
            binding.albumCover.setImageResource(R.drawable.ic_nothing_found)
        }
    }

    private fun render(track: Track, viewModel: TrackPlayerViewModel) {
        binding.playButton.isEnabled = false
        Glide.with(this).load(track.getCoverArtwork()).placeholder(R.drawable.ic_cover_placeholder)
            .centerCrop().transform(RoundedCorners(convertDpToPx(8f, this)))
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
        viewModel.isTrackInFavorites(track.trackId)
    }

    private fun updateFavoriteState(isFavorite: Boolean) {
        if (isFavorite) {
            binding.favoriteBtn.setImageResource(R.drawable.ic_favorite_liked)
        } else {
            binding.favoriteBtn.setImageResource(R.drawable.ic_favorite_unliked)
        }
    }

    private fun updateState(state: PlayerState) {
        binding.playButton.isEnabled = state.isPlayButtonEnabled
        binding.playingTime.text = state.progress
        binding.playButton.setImageDrawable(AppCompatResources.getDrawable(this, state.buttonIcon))

    }

    companion object {
        private const val TIME_PATTERN = "mm:ss"
        const val TRACK_KEY = "TRACK_KEY"
        private const val CLICK_DEBOUNCE_DELAY_MILLIS = 300L

        fun newInstance(context: Context, track: Track): Intent {
            return Intent(context, TrackPlayerActivity::class.java).apply {
                putExtra(TRACK_KEY, track)
            }
        }
    }
}