package ru.xrom.playlistmaker.search.ui


import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.inputmethod.EditorInfo
import android.view.inputmethod.InputMethodManager
import android.widget.ImageView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.isVisible
import androidx.core.widget.doOnTextChanged
import androidx.recyclerview.widget.LinearLayoutManager
import org.koin.androidx.viewmodel.ext.android.viewModel
import ru.xrom.playlistmaker.R
import ru.xrom.playlistmaker.databinding.ActivitySearchBinding
import ru.xrom.playlistmaker.player.ui.TrackPlayerActivity
import ru.xrom.playlistmaker.search.domain.model.Track


class SearchActivity : AppCompatActivity() {
    private var searchValue = TEXT_DEF

    private val binding: ActivitySearchBinding by lazy {
        ActivitySearchBinding.inflate(layoutInflater)
    }

    private val viewModel by viewModel<SearchViewModel>()

    private var searchAdapter: TrackAdapter? = null
    private lateinit var historyAdapter: TrackAdapter


    companion object {
        const val SEARCH_TEXT = "SEARCH_TEXT"
        const val TEXT_DEF = ""
        const val TRACK_DATA = "track_data"
        private const val CLICK_DEBOUNCE_DELAY = 1000L
    }

    private var isClickAllowed = true
    private val handler = Handler(Looper.getMainLooper())

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)
        window.statusBarColor = resources.getColor(R.color.status_bar, theme)
        window.navigationBarColor = resources.getColor(R.color.navigation_bar, theme)

        binding.toolbar.setNavigationOnClickListener {
            onBackPressedDispatcher.onBackPressed()
        }
        viewModel.observeSearchState().observe(this) { state ->
            renderState(state)
        }
        binding.searchBar.requestFocus()
        binding.searchBar.setText(searchValue)
        binding.cancelButton.setOnClickListener {
            binding.searchBar.text.clear()
            searchAdapter?.itemCount?.let { it1 -> searchAdapter?.notifyItemRangeChanged(0, it1) }
            showHistory()
        }
        binding.searchBar.setOnFocusChangeListener { _, hasFocus ->
            if (hasFocus && binding.searchBar.text.isEmpty()) {
                showHistory()
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
            if (binding.searchBar.hasFocus() && s?.isEmpty() == true)
                showHistory()
            else
                viewModel.searchDebounce(searchValue)
        }

        binding.searchBar.setOnEditorActionListener { _, actionId, _ ->
            if (actionId == EditorInfo.IME_ACTION_DONE) {
                viewModel.searchRequest(searchValue)
            }
            false
        }

        val onHistoryItemClickListener = OnItemClickListener { item ->
            openPlayer(item)
        }
        historyAdapter = TrackAdapter(onHistoryItemClickListener)
        binding.recycleHistoryView.layoutManager = LinearLayoutManager(this)
        binding.recycleHistoryView.adapter = historyAdapter

        val onItemClickListener = OnItemClickListener { item ->
            openPlayer(item)
        }

        binding.recycleView.layoutManager = LinearLayoutManager(this)
        searchAdapter = TrackAdapter(onItemClickListener)
        binding.recycleView.adapter = searchAdapter

        binding.updateResponse.setOnClickListener {
            viewModel.searchDebounce(searchValue)
        }
    }

    private fun renderState(state: SearchState) {
        when (state) {
            is SearchState.ContentHistory -> updateSearchHistoryAdapter(state)
            is SearchState.ContentSearch -> updateContentSearch(state.tracks)
            is SearchState.EmptyHistory -> updateSearchHistoryAdapter(state)
            is SearchState.Error -> showErrorMessage(state.errorMessage)
            is SearchState.Loading -> showLoading(true)
            is SearchState.NothingFound -> showEmptyView()
        }
    }

    private fun showLoading(isLoading: Boolean) {
        binding.progressBar.isVisible = isLoading
    }

    private fun showContentSearch() {
        binding.recycleView.isVisible = true
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
        binding.recycleView.isVisible = false
        binding.placeholderLayout.isVisible = false
        binding.historyLayout.isVisible = (historyAdapter.itemCount != 0)
    }

    private fun updateSearchHistoryAdapter(sdata: SearchState) {
        historyAdapter.items.clear()
        if (sdata is SearchState.ContentHistory) {
            historyAdapter.items.addAll(sdata.data)
        }
        historyAdapter.notifyItemRangeChanged(0, historyAdapter.itemCount)
        showHistory()
    }

    private fun openPlayer(track: Track) {
        binding.searchBar.text.clear()
        if (clickDebounce()) {
            binding.searchBar.text.clear()
            viewModel.addToHistory(track)
            val intent = Intent(this, TrackPlayerActivity::class.java)
            intent.putExtra(TRACK_DATA, track)
            startActivity(intent)
        }
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.putString(SEARCH_TEXT, searchValue)
    }

    override fun onRestoreInstanceState(savedInstanceState: Bundle) {
        super.onRestoreInstanceState(savedInstanceState)
        searchValue = savedInstanceState.getString(SEARCH_TEXT, TEXT_DEF)
    }

    private fun clearButtonVisibility(s: CharSequence?, v: ImageView) {
        if (s.isNullOrEmpty()) {
            v.isVisible = false
            val inputMethodManager =
                getSystemService(Context.INPUT_METHOD_SERVICE) as? InputMethodManager
            inputMethodManager?.hideSoftInputFromWindow(v.windowToken, 0)
        } else {
            v.isVisible = true
        }
    }

    private fun showEmptyView() {
        showLoading(false)
        binding.recycleView.isVisible = false
        binding.historyLayout.isVisible = false
        binding.placeholderLayout.isVisible = true
        binding.updateResponse.isVisible = false
        binding.placeholderMessage.text = getString(R.string.nothing_found)
        searchAdapter?.let { searchAdapter?.notifyItemRangeChanged(0, it.itemCount) }
        binding.placeholderImage.setImageResource(R.drawable.ic_nothing_found)
    }

    private fun showErrorMessage(additionalMessage: String) {
        showLoading(false)
        binding.recycleView.isVisible = false
        binding.historyLayout.isVisible = false
        binding.placeholderLayout.isVisible = true
        binding.updateResponse.isVisible = true
        searchAdapter?.let { searchAdapter?.notifyItemRangeChanged(0, it.itemCount) }
        binding.placeholderImage.setImageResource(R.drawable.ic_something_went_wrong)
        binding.placeholderMessage.text = getString(R.string.something_went_wrong)
        if (additionalMessage.isNotEmpty()) {
            Toast.makeText(applicationContext, additionalMessage, Toast.LENGTH_LONG)
                .show()
        }
    }

    private fun clickDebounce(): Boolean {
        val current = isClickAllowed
        if (isClickAllowed) {
            isClickAllowed = false
            handler.postDelayed({ isClickAllowed = true }, CLICK_DEBOUNCE_DELAY)
        }
        return current
    }
}