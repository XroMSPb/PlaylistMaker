package ru.xrom.playlistmaker.search.ui


import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.text.Editable
import android.text.TextWatcher
import android.view.View.GONE
import android.view.View.VISIBLE
import android.view.inputmethod.EditorInfo
import android.view.inputmethod.InputMethodManager
import android.widget.ImageView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.isVisible
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import ru.xrom.playlistmaker.R
import ru.xrom.playlistmaker.databinding.ActivitySearchBinding
import ru.xrom.playlistmaker.player.ui.TrackPlayerActivity
import ru.xrom.playlistmaker.search.domain.model.Track


class SearchActivity : AppCompatActivity() {
    private var searchValue = TEXT_DEF

    private val binding: ActivitySearchBinding by lazy {
        ActivitySearchBinding.inflate(layoutInflater)
    }

    private val viewModel: SearchViewModel by lazy {
        ViewModelProvider(
            this,
            SearchViewModel.getViewModelFactory()
        )[SearchViewModel::class.java]
    }

    private lateinit var searchAdapter: TrackAdapter
    private lateinit var historyAdapter: TrackAdapter
    private val tracks = mutableListOf<Track>()


    companion object {
        const val SEARCH_TEXT = "SEARCH_TEXT"
        const val TEXT_DEF = ""
        const val TRACK_DATA = "track_data"
        private const val CLICK_DEBOUNCE_DELAY = 1000L
    }

    private var isClickAllowed = true
    private val handler = Handler(Looper.getMainLooper())

    @SuppressLint("ClickableViewAccessibility")
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
            tracks.clear()
            searchAdapter.notifyDataSetChanged()
            showMessage("", "", ResultResponse.HISTORY)
        }
        binding.searchBar.setOnFocusChangeListener { _, hasFocus ->
            if (hasFocus && binding.searchBar.text.isEmpty()) {
                showMessage("", "", ResultResponse.HISTORY)
            } else {
                showMessage("", "", ResultResponse.SUCCESS)
            }
        }


        binding.clearHistory.setOnClickListener {
            viewModel.clearHistory()
            binding.historyLayout.isVisible = false
        }

        val simpleTextWatcher = object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {

            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                clearButtonVisibility(s, binding.cancelButton)
                searchValue = s.toString()
                if (binding.searchBar.hasFocus() && s?.isEmpty() == true) {
                    showMessage("", "", ResultResponse.HISTORY)
                } else {
                    viewModel.searchDebounce(searchValue)
                    showMessage("", "", ResultResponse.SUCCESS)
                }

            }

            override fun afterTextChanged(s: Editable?) {

            }
        }
        binding.searchBar.addTextChangedListener(simpleTextWatcher)
        binding.searchBar.setOnEditorActionListener { _, actionId, _ ->
            if (actionId == EditorInfo.IME_ACTION_DONE) {
                viewModel.searchDebounce(searchValue)
                true
            }
            false
        }

        val onHistoryItemClickListener = OnItemClickListener { item ->
            openPlayer(item)
        }
        historyAdapter = TrackAdapter(onHistoryItemClickListener)
        binding.recycleHistoryView.layoutManager = LinearLayoutManager(this)


        // historyAdapter.items = searchHistorySaver.getHistory().toMutableList()
        binding.recycleHistoryView.adapter = historyAdapter

        val onItemClickListener = OnItemClickListener { item ->
            openPlayer(item)
        }

        binding.recycleView.layoutManager = LinearLayoutManager(this)
        searchAdapter = TrackAdapter(onItemClickListener)
        searchAdapter.items = tracks
        binding.recycleView.adapter = searchAdapter

        binding.updateResponse.setOnClickListener {
            viewModel.searchDebounce(searchValue)
        }
        showMessage("", "", ResultResponse.HISTORY)
    }

    private fun showContentSearch(tracks: List<Track>) {
        searchAdapter.items.clear()
        searchAdapter.items.addAll(tracks)
        searchAdapter.notifyDataSetChanged()
        showMessage("", "", ResultResponse.SUCCESS)
    }

    private fun renderState(state: SearchState) {
        when (state) {
            is SearchState.ContentHistory -> updateSearchHistoryAdapter(state.data)
            is SearchState.ContentSearch -> showContentSearch(state.tracks)
            SearchState.EmptyHistory -> updateSearchHistoryAdapter(state)
            is SearchState.Error -> TODO()
            SearchState.Loading -> showLoading(true)
            SearchState.NothingFound -> TODO()
        }
    }

    private fun showLoading(isLoading: Boolean) {
        binding.progressBar.isVisible = isLoading
    }


    private fun updateSearchHistoryAdapter(sdata: List<Track>) {
        historyAdapter.items.clear()
        historyAdapter.items.addAll(sdata)
        historyAdapter.notifyDataSetChanged()
    }

    private fun openPlayer(track: Track) {
        if (clickDebounce()) {
            val intent = Intent(this, TrackPlayerActivity::class.java)
            intent.putExtra(TRACK_DATA, track)
            startActivity(intent)
            searchHistorySaver.addToHistory(track)
            updateSearchHistoryAdapter()
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
            v.visibility = GONE
            val inputMethodManager =
                getSystemService(Context.INPUT_METHOD_SERVICE) as? InputMethodManager
            inputMethodManager?.hideSoftInputFromWindow(v.windowToken, 0)
        } else {
            v.visibility = VISIBLE
        }
    }


    private fun showMessage(text: String, additionalMessage: String, errorType: ResultResponse) {
        showLoading(false)
        when (errorType) {
            ResultResponse.SUCCESS -> {
                binding.recycleView.visibility = VISIBLE
                binding.placeholderLayout.visibility = GONE
                binding.historyLayout.visibility = GONE
            }

            ResultResponse.EMPTY -> {
                binding.recycleView.visibility = GONE
                binding.historyLayout.visibility = GONE
                binding.placeholderLayout.visibility = VISIBLE
                binding.updateResponse.visibility = GONE
                tracks.clear()
                searchAdapter.notifyDataSetChanged()
                binding.placeholderMessage.text = text
                binding.placeholderImage.setImageResource(R.drawable.ic_nothing_found)
                if (additionalMessage.isNotEmpty()) {
                    Toast.makeText(applicationContext, additionalMessage, Toast.LENGTH_LONG)
                        .show()
                }
            }

            ResultResponse.ERROR -> {
                binding.recycleView.visibility = GONE
                binding.historyLayout.visibility = GONE
                binding.placeholderLayout.visibility = VISIBLE
                binding.updateResponse.visibility = VISIBLE
                tracks.clear()
                searchAdapter.notifyDataSetChanged()
                binding.placeholderMessage.text = text
                binding.placeholderImage.setImageResource(R.drawable.ic_something_went_wrong)
                if (additionalMessage.isNotEmpty()) {
                    Toast.makeText(applicationContext, additionalMessage, Toast.LENGTH_LONG)
                        .show()
                }
            }

            ResultResponse.HISTORY -> {
                binding.recycleView.visibility = GONE
                binding.placeholderLayout.visibility = GONE
                if (historyAdapter.items.isNotEmpty())
                    binding.historyLayout.visibility = VISIBLE
                else
                    binding.historyLayout.visibility = GONE
            }
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