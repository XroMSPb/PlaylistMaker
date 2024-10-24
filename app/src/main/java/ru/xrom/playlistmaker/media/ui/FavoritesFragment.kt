package ru.xrom.playlistmaker.media.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import org.koin.androidx.viewmodel.ext.android.viewModel
import ru.xrom.playlistmaker.databinding.FragmentFavoritesBinding
import ru.xrom.playlistmaker.player.ui.TrackPlayerActivity
import ru.xrom.playlistmaker.search.domain.model.Track
import ru.xrom.playlistmaker.search.ui.TrackAdapter
import ru.xrom.playlistmaker.utils.debounce

class FavoritesFragment : Fragment() {

    private var _binding: FragmentFavoritesBinding? = null
    private val binding get() = _binding!!

    private val viewModel: FavoritesViewModel by viewModel()

    private lateinit var adapter: TrackAdapter

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentFavoritesBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val onTrackClickDebounce = debounce<Track>(
            CLICK_DEBOUNCE_DELAY, viewLifecycleOwner.lifecycleScope, false
        ) { track -> openPlayer(track) }

        binding.recycleView.layoutManager = LinearLayoutManager(context)
        adapter = TrackAdapter { item ->
            onTrackClickDebounce(item)
        }
        viewModel.refreshFavoriteTracks()
        viewModel.getFavoriteTracks().observe(viewLifecycleOwner) { data ->
            if (data.isEmpty()) {
                binding.groupEmpty.isVisible = true
                binding.recycleView.isVisible = false
            } else {
                updateFavoriteState(data)
                binding.groupEmpty.isVisible = false
                binding.recycleView.isVisible = true
            }
        }
    }

    private fun updateFavoriteState(data: List<Track>) {
        adapter.items = data.toMutableList()
        binding.recycleView.adapter = adapter
    }

    private fun openPlayer(item: Track) {
        startActivity(TrackPlayerActivity.newInstance(requireContext(), item))
    }

    override fun onResume() {
        super.onResume()
        viewModel.refreshFavoriteTracks()
    }
    
    companion object {
        fun newInstance() = FavoritesFragment()
        private const val CLICK_DEBOUNCE_DELAY = 1000L
    }
}