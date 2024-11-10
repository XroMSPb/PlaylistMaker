package ru.xrom.playlistmaker.media.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.appcompat.content.res.AppCompatResources.getDrawable
import androidx.core.bundle.bundleOf
import androidx.core.net.toUri
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import org.koin.androidx.viewmodel.ext.android.viewModel
import ru.xrom.playlistmaker.R
import ru.xrom.playlistmaker.databinding.FragmentPlaylistViewerBinding
import ru.xrom.playlistmaker.media.ui.model.Playlist
import ru.xrom.playlistmaker.player.ui.TrackPlayerActivity
import ru.xrom.playlistmaker.search.domain.model.Track
import ru.xrom.playlistmaker.search.ui.TrackAdapter
import ru.xrom.playlistmaker.utils.debounce
import ru.xrom.playlistmaker.utils.getDefaultCacheImagePath
import java.io.File
import java.text.SimpleDateFormat
import java.util.Locale

class PlaylistViewerFragment : Fragment() {

    companion object {
        const val PLAYLIST_ID_KEY = "PLAYLIST_ID_KEY"
        const val CLICK_DEBOUNCE_DELAY_MILLIS = 300L
        fun newInstance(playlistId: Int) = PlaylistViewerFragment().apply {
            arguments = bundleOf(PLAYLIST_ID_KEY to playlistId)
        }
    }

    private lateinit var adapter: TrackAdapter
    private val viewModel: PlaylistViewerViewModel by viewModel()
    private var _binding: FragmentPlaylistViewerBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        _binding = FragmentPlaylistViewerBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val playlistId = requireArguments().getInt(PLAYLIST_ID_KEY, -1)
        if (playlistId == -1) {
            Toast.makeText(requireContext(), "Playlist not found", Toast.LENGTH_SHORT).show()
            findNavController().navigateUp()
        }
        val onTrackClickDebounce = debounce<Track>(
            CLICK_DEBOUNCE_DELAY_MILLIS, viewLifecycleOwner.lifecycleScope, false
        ) { track -> openPlayer(track) }

        binding.recyclerView.layoutManager = LinearLayoutManager(context)
        adapter = TrackAdapter { item ->
            onTrackClickDebounce(item)
        }
        viewModel.loadPlaylist(playlistId)

        binding.backButton.setOnClickListener {
            findNavController().navigateUp()
        }

        viewModel.observePlaylist().observe(viewLifecycleOwner) { playlist ->
            if (playlist == null) {
                Toast.makeText(requireContext(), "Playlist not found", Toast.LENGTH_SHORT).show()
                return@observe
            } else
                renderUI(playlist)
        }

        viewModel.observeAllTracks().observe(viewLifecycleOwner) { tracks ->
            if (!tracks.isNullOrEmpty()) {
                val duration = tracks.sumOf { it.trackTimeMillis }
                binding.playlistInfo.text =
                    getString(
                        R.string.playlist_info,
                        SimpleDateFormat("mm", Locale.getDefault()).format(duration),
                        tracks.size
                    )
                adapter.items = tracks.toMutableList()
                binding.recyclerView.adapter = adapter
            }
            //duration.toString() + " · " + tracks?.size.toString()
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    private fun renderUI(playlist: Playlist) {
        if (playlist.imagePath.isNullOrEmpty())
            binding.playlistCover.setImageDrawable(
                getDrawable(
                    requireContext(),
                    R.drawable.ic_cover_placeholder
                )
            )
        else {
            binding.playlistCover.setImageURI(
                File(
                    getDefaultCacheImagePath(requireContext()),
                    playlist.imagePath
                ).toUri()
            )
            binding.playlistCover.scaleX = 1.1f
            binding.playlistCover.scaleY = 1.1f
        }

        binding.playlistName.text = playlist.name
        binding.playlistDescription.text = playlist.description
    }

    private fun openPlayer(item: Track) {
        startActivity(TrackPlayerActivity.newInstance(requireContext(), item))
    }
}