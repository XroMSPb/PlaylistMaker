package ru.xrom.playlistmaker.media.ui

import android.content.Intent
import android.icu.text.SimpleDateFormat
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
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.dialog.MaterialAlertDialogBuilder
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
import java.util.Locale

class PlaylistViewerFragment : Fragment() {

    companion object {
        const val PLAYLIST_ID_KEY = "PLAYLIST_ID_KEY"
        const val CLICK_DEBOUNCE_DELAY_MILLIS = 300L
        const val SMALL_ZOOM = 1.1f
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
        adapter = TrackAdapter({ item ->
            onTrackClickDebounce(item)
        }, { item -> onTrackLongClick(item) })
        viewModel.loadPlaylist(playlistId)

        binding.backButton.setOnClickListener {
            findNavController().navigateUp()
        }

        viewModel.observePlaylist().observe(viewLifecycleOwner) { playlist ->
            if (playlist == null) {
                Toast.makeText(requireContext(), "Playlist not found", Toast.LENGTH_SHORT).show()
                return@observe
            } else renderUI(playlist)
        }

        val bottomSheetContainer = binding.playlistsBottomSheet
        val bottomSheetBehavior = BottomSheetBehavior.from(bottomSheetContainer)

        val bottomMenuContainer = binding.playlistsBottomMenu
        val bottomMenuBehavior = BottomSheetBehavior.from(bottomMenuContainer)
        bottomMenuBehavior.state = BottomSheetBehavior.STATE_HIDDEN
        bottomMenuBehavior.addBottomSheetCallback(object :
            BottomSheetBehavior.BottomSheetCallback() {
            override fun onStateChanged(bottomSheet: View, newState: Int) {
                when (newState) {
                    BottomSheetBehavior.STATE_HIDDEN -> {
                        binding.overlay.visibility = View.GONE
                    }

                    else -> {
                        binding.overlay.visibility = View.VISIBLE
                    }
                }
            }

            override fun onSlide(bottomSheet: View, slideOffset: Float) {
                binding.overlay.alpha = (slideOffset + 1f) / 2f
            }
        })

        binding.overlay.setOnClickListener {
            bottomMenuBehavior.state = BottomSheetBehavior.STATE_HIDDEN
            bottomSheetBehavior.state = BottomSheetBehavior.STATE_COLLAPSED
        }

        binding.menuButton.post {
            val location = IntArray(2)
            binding.menuButton.getLocationOnScreen(location)
            bottomSheetBehavior.peekHeight =
                binding.root.height - location[1] - binding.menuButton.height
            binding.playlistName.getLocationOnScreen(location)
            bottomMenuBehavior.peekHeight =
                binding.root.height - location[1] - binding.playlistName.height
        }

        binding.menuButton.setOnClickListener {
            bottomMenuBehavior.state = BottomSheetBehavior.STATE_COLLAPSED
        }
        binding.shareButton.setOnClickListener {
            sharePlaylist()
        }
        binding.share.setOnClickListener {
            sharePlaylist()
        }

        binding.deletePlaylist.setOnClickListener {
            deletePlaylist(binding.playlistName.text.toString())
        }

        binding.editPlaylist.setOnClickListener {
            findNavController().navigate(R.id.action_playlistViewer_to_newPlaylist, Bundle().apply {
                putBoolean(NewPlaylistFragment.FROM_NAVCONTROLLER_KEY, true)
            })
        }

        viewModel.observeAllTracks().observe(viewLifecycleOwner) { tracks ->
            if (!tracks.isNullOrEmpty()) {
                val duration = tracks.sumOf { it.trackTimeMillis }
                binding.playlistInfo.text = getString(
                    R.string.playlist_info,
                    SimpleDateFormat("mm", Locale.getDefault()).format(duration),
                    getPluralForm(tracks.size).format(tracks.size)
                )
                adapter.items = tracks.toMutableList()
                binding.recyclerView.adapter = adapter
                binding.playlistSmallTracks.text = getPluralForm(tracks.size).format(tracks.size)
            } else {
                binding.playlistInfo.text = getPluralForm(0).format(0)
                binding.playlistSmallTracks.text = getPluralForm(0).format(0)
                adapter.clearItems()
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    private fun deletePlaylist(playlistName: String) {
        MaterialAlertDialogBuilder(requireContext()).setTitle(getString(R.string.delete_playlist))
            .setMessage(getString(R.string.delete_playlist_message).format(playlistName))
            .setNegativeButton(R.string.no) { _, _ ->
            }.setPositiveButton(R.string.yes) { dialog, which ->
                synchronized(this) {
                    viewModel.deletePlaylist()
                    findNavController().navigateUp()
                }
            }.show()
    }

    private fun renderUI(playlist: Playlist) {
        if (playlist.imagePath.isNullOrEmpty()) binding.playlistCover.setImageDrawable(
            getDrawable(
                requireContext(), R.drawable.ic_cover_placeholder
            )
        )
        else {
            binding.playlistCover.setImageURI(
                File(
                    getDefaultCacheImagePath(requireContext()), playlist.imagePath
                ).toUri()
            )
            binding.playlistCover.scaleX = SMALL_ZOOM
            binding.playlistCover.scaleY = SMALL_ZOOM

            binding.playlistSmallCover.setImageURI(
                File(
                    getDefaultCacheImagePath(requireContext()), playlist.imagePath
                ).toUri()
            )
        }

        binding.playlistName.text = playlist.name
        binding.playlistDescription.text = playlist.description
        binding.playlistSmallName.text = playlist.name

    }

    private fun openPlayer(item: Track) {
        startActivity(TrackPlayerActivity.newInstance(requireContext(), item))
    }

    private fun onTrackLongClick(track: Track) {
        MaterialAlertDialogBuilder(requireContext()).setTitle(getString(R.string.delete_track))
            .setNegativeButton(R.string.no) { _, _ ->
            }.setPositiveButton(R.string.yes) { dialog, which ->
                synchronized(this) {
                    viewModel.removeTrackFromPlaylist(track.trackId)
                }
            }.show()
    }

    private fun sharePlaylist() {
        if (adapter.items.isEmpty())
            Toast.makeText(requireContext(), R.string.no_track_in_playlist, Toast.LENGTH_SHORT)
                .show()
        else {
            var message = "${binding.playlistName.text}\n${binding.playlistDescription.text}\n${
                getPluralForm(adapter.items.size).format(adapter.items.size)
            }\n"
            var i = 0
            adapter.items.forEach { track ->
                message = message + "${i++}. ${track.artistName} - ${track.trackName} (${
                    SimpleDateFormat(
                        "mm:ss",
                        Locale.getDefault()
                    ).format(track.trackTimeMillis)
                })\n"
            }

            val share = Intent.createChooser(Intent().apply {
                action = Intent.ACTION_SEND
                putExtra(Intent.EXTRA_TEXT, message)
                type = "text/plain"
                putExtra(Intent.EXTRA_TITLE, R.string.app_name)
            }, null)
            startActivity(share)
        }

    }

    private fun getPluralForm(num: Int): String {
        val n = num % 100
        return when {
            n in 11..14 -> requireContext().getString(R.string.trackss)
            n % 10 == 1 -> requireContext().getString(R.string.track)
            n % 10 in 2..4 -> requireContext().getString(R.string.tracks)
            else -> requireContext().getString(R.string.trackss)
        }
    }
}


