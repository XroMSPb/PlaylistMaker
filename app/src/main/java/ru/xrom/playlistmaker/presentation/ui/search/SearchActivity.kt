package ru.xrom.playlistmaker.presentation.ui.search


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
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.ProgressBar
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import ru.xrom.playlistmaker.R
import ru.xrom.playlistmaker.creator.Creator
import ru.xrom.playlistmaker.creator.Creator.provideSharedPreferences
import ru.xrom.playlistmaker.data.repository.SearchHistoryRepositoryImpl
import ru.xrom.playlistmaker.domain.api.OnItemClickListener
import ru.xrom.playlistmaker.domain.api.TrackInteractor
import ru.xrom.playlistmaker.domain.model.Track
import ru.xrom.playlistmaker.presentation.ui.player.PlayerActivity


class SearchActivity : AppCompatActivity() {
    private var searchValue = TEXT_DEF


    private lateinit var searchAdapter: TrackAdapter
    private lateinit var historyAdapter: TrackAdapter
    private val tracks = mutableListOf<Track>()
    private lateinit var recyclerView: RecyclerView
    private lateinit var historyRecyclerView: RecyclerView
    private lateinit var placeholderMessage: TextView
    private lateinit var placeholderImage: ImageView
    private lateinit var updateButton: Button
    private lateinit var placeholderLayout: LinearLayout
    private lateinit var historyLayout: LinearLayout
    private lateinit var searchHistorySaver: SearchHistoryRepositoryImpl
    private lateinit var progressBar: ProgressBar


    companion object {
        const val SEARCH_TEXT = "SEARCH_TEXT"
        const val TEXT_DEF = ""
        const val TRACK_DATA = "track_data"
        private const val CLICK_DEBOUNCE_DELAY = 1000L
        private const val SEARCH_DEBOUNCE_DELAY = 2000L
    }

    private val searchRunnable = Runnable { search() }
    private var isClickAllowed = true
    private val handler = Handler(Looper.getMainLooper())

    @SuppressLint("ClickableViewAccessibility")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_search)
        window.statusBarColor = resources.getColor(R.color.status_bar, theme)
        window.navigationBarColor = resources.getColor(R.color.navigation_bar, theme)
        placeholderImage = findViewById(R.id.placeholder_image)
        placeholderMessage = findViewById(R.id.placeholder_message)
        updateButton = findViewById(R.id.update_response)
        placeholderLayout = findViewById(R.id.placeholder_layout)
        recyclerView = findViewById(R.id.recycle_view)
        historyRecyclerView = findViewById(R.id.recycle_history_view)
        historyLayout = findViewById(R.id.history_layout)
        progressBar = findViewById(R.id.progress_bar)

        val toolbar = findViewById<Toolbar>(R.id.toolbar)
        toolbar.setNavigationOnClickListener {
            onBackPressedDispatcher.onBackPressed()
        }

        val cancelBtn = findViewById<ImageView>(R.id.cancel_button)
        val searchBar = findViewById<EditText>(R.id.search_bar)
        searchBar.requestFocus()
        searchBar.setText(searchValue)
        cancelBtn.setOnClickListener {
            searchBar.text.clear()
            tracks.clear()
            searchAdapter.notifyDataSetChanged()
            showMessage("", "", ResultResponse.HISTORY)
        }
        searchBar.setOnFocusChangeListener { _, hasFocus ->
            if (hasFocus && searchBar.text.isEmpty()) {
                showMessage("", "", ResultResponse.HISTORY)
            } else {
                showMessage("", "", ResultResponse.SUCCESS)
            }
        }

        val clearHistoryBtn = findViewById<Button>(R.id.clear_history)
        clearHistoryBtn.setOnClickListener {
            searchHistorySaver.clearHistory()
            historyAdapter.items.clear()
            historyAdapter.notifyDataSetChanged()
            historyLayout.visibility = GONE
        }

        val simpleTextWatcher = object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {

            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                clearButtonVisibility(s, cancelBtn)
                searchValue = s.toString()
                if (searchBar.hasFocus() && s?.isEmpty() == true) {
                    showMessage("", "", ResultResponse.HISTORY)
                } else {
                    searchDebounce()
                    showMessage("", "", ResultResponse.SUCCESS)
                }

            }

            override fun afterTextChanged(s: Editable?) {

            }
        }
        searchBar.addTextChangedListener(simpleTextWatcher)
        searchBar.setOnEditorActionListener { _, actionId, _ ->
            if (actionId == EditorInfo.IME_ACTION_DONE) {
                search()
                true
            }
            false
        }

        val onHistoryItemClickListener = OnItemClickListener { item ->
            openPlayer(item)
        }
        historyAdapter = TrackAdapter(onHistoryItemClickListener)
        historyRecyclerView.layoutManager = LinearLayoutManager(this)

        searchHistorySaver = SearchHistoryRepositoryImpl(
            provideSharedPreferences()
        )
        historyAdapter.items = searchHistorySaver.updateTracks().toMutableList()
        historyRecyclerView.adapter = historyAdapter

        val onItemClickListener = OnItemClickListener { item ->
            openPlayer(item)
        }

        recyclerView.layoutManager = LinearLayoutManager(this)
        searchAdapter = TrackAdapter(onItemClickListener)
        searchAdapter.items = tracks
        recyclerView.adapter = searchAdapter

        updateButton.setOnClickListener {
            search()
        }
        showMessage("", "", ResultResponse.HISTORY)
    }

    override fun onDestroy() {
        super.onDestroy()
        handler.removeCallbacks(searchRunnable)
    }

    private fun updateSearchHistoryAdapter() {
        historyAdapter.items.clear()
        historyAdapter.items.addAll(searchHistorySaver.updateTracks())
        historyAdapter.notifyDataSetChanged()
    }

    private fun openPlayer(track: Track) {
        if (clickDebounce()) {
            val intent = Intent(this, PlayerActivity::class.java)
            intent.putExtra(TRACK_DATA, track)
            startActivity(intent)
            searchHistorySaver.addTrack(track)
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

    private fun search() {
        progressBar.visibility = VISIBLE
        Creator.provideTrackInteractor()
            .search(searchValue, object : TrackInteractor.TrackConsumer {
                override fun consume(foundTracks: List<Track>) {
                    runOnUiThread {
                        progressBar.visibility = GONE

                        if (foundTracks.isNotEmpty()) {
                            tracks.clear()
                            tracks.addAll(foundTracks)
                            searchAdapter.notifyDataSetChanged()
                            showMessage("", "", ResultResponse.SUCCESS)
                        } else {
                            showMessage(getString(R.string.nothing_found), "", ResultResponse.EMPTY)
                        }

                    }
                }

                override fun onFailure(t: Throwable) {
                    showMessage(
                        getString(R.string.something_went_wrong),
                        t.message.toString(),
                        ResultResponse.ERROR
                    )
                }
            })
    }

    private fun showMessage(text: String, additionalMessage: String, errorType: ResultResponse) {
        progressBar.visibility = GONE
        when (errorType) {
            ResultResponse.SUCCESS -> {
                recyclerView.visibility = VISIBLE
                placeholderLayout.visibility = GONE
                historyLayout.visibility = GONE
            }

            ResultResponse.EMPTY -> {
                recyclerView.visibility = GONE
                historyLayout.visibility = GONE
                placeholderLayout.visibility = VISIBLE
                updateButton.visibility = GONE
                tracks.clear()
                searchAdapter.notifyDataSetChanged()
                placeholderMessage.text = text
                placeholderImage.setImageResource(R.drawable.ic_nothing_found)
                if (additionalMessage.isNotEmpty()) {
                    Toast.makeText(applicationContext, additionalMessage, Toast.LENGTH_LONG)
                        .show()
                }
            }

            ResultResponse.ERROR -> {
                recyclerView.visibility = GONE
                historyLayout.visibility = GONE
                placeholderLayout.visibility = VISIBLE
                updateButton.visibility = VISIBLE
                tracks.clear()
                searchAdapter.notifyDataSetChanged()
                placeholderMessage.text = text
                placeholderImage.setImageResource(R.drawable.ic_something_went_wrong)
                if (additionalMessage.isNotEmpty()) {
                    Toast.makeText(applicationContext, additionalMessage, Toast.LENGTH_LONG)
                        .show()
                }
            }

            ResultResponse.HISTORY -> {
                recyclerView.visibility = GONE
                placeholderLayout.visibility = GONE
                if (historyAdapter.items.isNotEmpty())
                    historyLayout.visibility = VISIBLE
                else
                    historyLayout.visibility = GONE
            }
        }
    }

    private fun searchDebounce() {
        handler.removeCallbacks(searchRunnable)
        handler.postDelayed(searchRunnable, SEARCH_DEBOUNCE_DELAY)
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