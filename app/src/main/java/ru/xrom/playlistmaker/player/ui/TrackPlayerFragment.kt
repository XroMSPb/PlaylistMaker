package ru.xrom.playlistmaker.player.ui

import android.icu.text.SimpleDateFormat
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.content.res.AppCompatResources
import androidx.core.os.bundleOf
import androidx.fragment.app.Fragment
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import org.koin.androidx.viewmodel.ext.android.viewModel
import org.koin.core.parameter.parametersOf
import ru.xrom.playlistmaker.R
import ru.xrom.playlistmaker.databinding.FragmentPlayerBinding
import ru.xrom.playlistmaker.player.domain.model.PlayingState
import ru.xrom.playlistmaker.search.domain.model.Track
import ru.xrom.playlistmaker.utils.convertDpToPx
import ru.xrom.playlistmaker.utils.getReleaseYear
import java.util.Locale

class TrackPlayerFragment : Fragment() {

    private var _binding: FragmentPlayerBinding? = null
    private val binding get() = _binding!!


    private val dateFormat by lazy { SimpleDateFormat(TIME_PATTERN, Locale.getDefault()) }
    private var track: Track? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        _binding = FragmentPlayerBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        track = requireArguments().getParcelable(TRACK_KEY)
        //(TRACK_KEY, Track::class.java)
        //val track = intent.getParcelableExtra(SearchFragment.TRACK_DATA) as? Track

        if (track != null) {
            val viewModel: TrackPlayerViewModel by viewModel {
                parametersOf(track!!.previewUrl)
            }
            render(track!!, viewModel)

            viewModel.observePlayingState().observe(viewLifecycleOwner) { state ->
                binding.playButton.isEnabled = state != PlayingState.Default
                updateState(state)
                viewModel.stateControl()
            }

            viewModel.observePositionState().observe(viewLifecycleOwner) {
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
            .transform(RoundedCorners(convertDpToPx(8f, requireContext())))
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

    private fun updateState(state: PlayingState) {
        when (state) {
            PlayingState.Default,
            PlayingState.Prepared,
            PlayingState.Paused,
            -> binding.playButton.setImageDrawable(
                AppCompatResources.getDrawable(
                    requireContext(), R.drawable.ic_play
                )
            )

            PlayingState.Playing -> binding.playButton.setImageDrawable(
                AppCompatResources.getDrawable(
                    requireContext(), R.drawable.ic_pause
                )
            )

            PlayingState.Complete -> {
                binding.playButton.setImageDrawable(
                    AppCompatResources.getDrawable(
                        requireContext(), R.drawable.ic_play
                    )
                )
                binding.playingTime.text = getString(R.string.time_zero)
            }
        }
    }

    companion object {
        private const val TIME_PATTERN = "mm:ss"

        const val TRACK_KEY = "TRACK_KEY"
        fun createArgs(track: Track): Bundle =
            bundleOf(TRACK_KEY to track)

    }
}