package ru.xrom.playlistmaker.search.ui


import android.app.Activity
import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.EditorInfo
import android.view.inputmethod.InputMethodManager
import android.widget.ImageView
import android.widget.Toast
import androidx.core.view.isVisible
import androidx.core.widget.doOnTextChanged
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import org.koin.androidx.viewmodel.ext.android.viewModel
import ru.xrom.playlistmaker.R
import ru.xrom.playlistmaker.databinding.FragmentSearchBinding
import ru.xrom.playlistmaker.player.ui.TrackPlayerActivity
import ru.xrom.playlistmaker.search.domain.model.Track
import ru.xrom.playlistmaker.utils.debounce


class SearchFragment : Fragment() {
    private var searchValue = TEXT_DEF

    private val viewModel by viewModel<SearchViewModel>()

    private var searchAdapter: TrackAdapter? = null
    private lateinit var historyAdapter: TrackAdapter

    private var _binding: FragmentSearchBinding? = null
    private val binding get() = _binding!!

    private lateinit var onTrackClickDebounce: (Track) -> Unit

    companion object {
        const val SEARCH_TEXT = "SEARCH_TEXT"
        const val TEXT_DEF = ""
        private const val CLICK_DEBOUNCE_DELAY = 1000L
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        _binding = FragmentSearchBinding.inflate(layoutInflater)
        return binding.root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.cancelButton.setOnClickListener {
            binding.searchBar.text.clear()
            clearSearchAdapter()
        }

        binding.searchBar.requestFocus()
        binding.searchBar.setText(searchValue)
        binding.searchBar.setOnFocusChangeListener { _, hasFocus ->
            if (hasFocus && binding.searchBar.text.isEmpty()) {
                viewModel.updateHistory()
            } else {
                showContentSearch()
            }
        }

        binding.clearHistory.setOnClickListener {
            viewModel.clearHistory()
            binding.historyLayout.isVisible = false
        }

        binding.searchBar.doOnTextChanged { s, _, _, _ ->
            clearButtonVisibility(s, binding.cancelButton)
            searchValue = s.toString()
            if (binding.searchBar.hasFocus() && s?.isEmpty() == true) {
                clearSearchAdapter()
                viewModel.updateHistory()
            } else viewModel.searchDebounce(searchValue)
        }

        binding.searchBar.setOnEditorActionListener { _, actionId, _ ->
            if (actionId == EditorInfo.IME_ACTION_DONE) {
                viewModel.searchDebounce(searchValue)
            }
            false
        }

        onTrackClickDebounce = debounce(
            CLICK_DEBOUNCE_DELAY, viewLifecycleOwner.lifecycleScope, false
        ) { track -> openPlayer(track) }
        val onItemClickListener = OnItemClickListener { item ->
            onTrackClickDebounce(item)
        }

        val onHistoryItemClickListener = OnItemClickListener { item ->
            onTrackClickDebounce(item)
        }
        historyAdapter = TrackAdapter(onHistoryItemClickListener)
        binding.recycleHistoryView.layoutManager = LinearLayoutManager(context)
        binding.recycleHistoryView.adapter = historyAdapter

        binding.recycleSearchView.layoutManager = LinearLayoutManager(context)
        searchAdapter = TrackAdapter(onItemClickListener)
        binding.recycleSearchView.adapter = searchAdapter

        binding.updateResponse.setOnClickListener {
            viewModel.updateSearch()
        }
        viewModel.observeSearchState().observe(viewLifecycleOwner) { state ->
            renderState(state)
        }
    }

    private fun clearSearchAdapter() {
        viewModel.stopSearch()
        searchAdapter?.clearItems()
    }

    private fun renderState(state: SearchState) {
        when (state) {
            is SearchState.ContentHistory -> updateSearchHistoryAdapter(state)
            is SearchState.ContentSearch -> updateContentSearch(state.tracks)
            is SearchState.Error -> showErrorMessage(state.errorMessage)
            is SearchState.Loading -> showLoading(true)
            is SearchState.NothingFound -> showEmptyView()
        }
    }

    private fun showLoading(isLoading: Boolean) {
        binding.progressBar.isVisible = isLoading
    }

    private fun showContentSearch() {
        binding.recycleSearchView.isVisible = true
        binding.placeholderLayout.isVisible = false
        binding.historyLayout.isVisible = false
    }

    private fun updateContentSearch(tracks: List<Track>) {
        showLoading(false)
        searchAdapter?.items?.clear()
        searchAdapter?.items?.addAll(tracks)
        searchAdapter?.itemCount?.let { searchAdapter?.notifyItemRangeChanged(0, it) }
        showContentSearch()
    }

    private fun showHistory() {
        binding.recycleSearchView.isVisible = false
        binding.placeholderLayout.isVisible = false
        binding.historyLayout.isVisible = (historyAdapter.itemCount != 0)
    }

    private fun updateSearchHistoryAdapter(sdata: SearchState) {
        historyAdapter.clearItems()
        if (sdata is SearchState.ContentHistory && sdata.data.isNotEmpty()) {
            historyAdapter.items.addAll(sdata.data)
            historyAdapter.notifyItemRangeChanged(0, historyAdapter.itemCount)
        }
        showHistory()
    }

    private fun openPlayer(track: Track) {
        viewModel.addToHistory(track)
        startActivity(TrackPlayerActivity.newInstance(requireContext(), track))
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.putString(SEARCH_TEXT, searchValue)
    }

    override fun onViewStateRestored(savedInstanceState: Bundle?) {
        super.onViewStateRestored(savedInstanceState)
        if (savedInstanceState != null) {
            searchValue = savedInstanceState.getString(SEARCH_TEXT, TEXT_DEF)
        }
    }

    private fun clearButtonVisibility(s: CharSequence?, v: ImageView) {
        if (s.isNullOrEmpty()) {
            v.isVisible = false
            view?.let { activity?.hideKeyboard() }
        } else {
            v.isVisible = true
        }
    }

    private fun Activity.hideKeyboard() {
        val imm = getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        val view = currentFocus
        if (view != null) {
            imm.hideSoftInputFromWindow(view.windowToken, 0)
        }
    }

    private fun showEmptyView() {
        showLoading(false)
        binding.recycleSearchView.isVisible = false
        binding.historyLayout.isVisible = false
        binding.placeholderLayout.isVisible = true
        binding.updateResponse.isVisible = false
        binding.placeholderMessage.text = getString(R.string.nothing_found)
        searchAdapter?.let { searchAdapter?.notifyItemRangeChanged(0, it.itemCount) }
        binding.placeholderImage.setImageResource(R.drawable.ic_nothing_found)
    }

    private fun showErrorMessage(additionalMessage: String) {
        showLoading(false)
        binding.recycleSearchView.isVisible = false
        binding.historyLayout.isVisible = false
        binding.placeholderLayout.isVisible = true
        binding.updateResponse.isVisible = true
        searchAdapter?.let { searchAdapter?.notifyItemRangeChanged(0, it.itemCount) }
        binding.placeholderImage.setImageResource(R.drawable.ic_something_went_wrong)
        binding.placeholderMessage.text = getString(R.string.something_went_wrong)
        if (additionalMessage.isNotEmpty()) {
            Toast.makeText(context, additionalMessage, Toast.LENGTH_LONG).show()
        }
    }
}