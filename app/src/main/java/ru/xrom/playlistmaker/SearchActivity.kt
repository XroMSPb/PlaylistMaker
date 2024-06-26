package ru.xrom.playlistmaker


import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.os.Bundle
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
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import ru.xrom.playlistmaker.itunes.ItunesResponse
import ru.xrom.playlistmaker.itunes.ResultResponse
import ru.xrom.playlistmaker.itunes.api
import ru.xrom.playlistmaker.recycleView.OnItemClickListener
import ru.xrom.playlistmaker.recycleView.TrackAdapter
import ru.xrom.playlistmaker.utils.SearchHistorySaver
import ru.xrom.playlistmaker.utils.Track


class SearchActivity : AppCompatActivity() {
    private var searchValue = TEXT_DEF
    private val baseUrl = "https://itunes.apple.com/"

    private lateinit var searchAdapter: TrackAdapter
    private lateinit var historyAdapter: TrackAdapter
    private val tracks = ArrayList<Track>()
    private lateinit var recyclerView: RecyclerView
    private lateinit var historyRecyclerView: RecyclerView
    private lateinit var placeholderMessage: TextView
    private lateinit var placeholderImage: ImageView
    private lateinit var updateButton: Button
    private lateinit var placeholderLayout: LinearLayout
    private lateinit var historyLayout: LinearLayout
    private lateinit var searchHistorySaver: SearchHistorySaver


    private val retrofit = Retrofit.Builder()
        .baseUrl(baseUrl)
        .addConverterFactory(GsonConverterFactory.create())
        .build()
    private val iTunesService = retrofit.create(api::class.java)

    companion object {
        const val SEARCH_TEXT = "SEARCH_TEXT"
        const val TEXT_DEF = ""
        const val TRACK_DATA = "track_data"
    }

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

        searchHistorySaver = SearchHistorySaver(
            getSharedPreferences(
                PLAYLISTMAKER_PREFERENCES,
                MODE_PRIVATE
            )
        )
        historyAdapter.items = searchHistorySaver.updateTracks()
        historyRecyclerView.adapter = historyAdapter

        val onItemClickListener = OnItemClickListener { item ->
            searchHistorySaver.addTrack(item)
            openPlayer(item)
            updateSearchHistoryAdapter()
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

    private fun updateSearchHistoryAdapter() {
        historyAdapter.items.clear()
        historyAdapter.items.addAll(searchHistorySaver.updateTracks())
        historyAdapter.notifyDataSetChanged()
    }

    private fun openPlayer(track: Track) {
        val intent = Intent(this, PlayerActivity::class.java)
        intent.putExtra(TRACK_DATA, track)
        startActivity(intent)
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
        iTunesService.search(searchValue)
            .enqueue(object : Callback<ItunesResponse> {
                override fun onResponse(
                    call: Call<ItunesResponse>,
                    response: Response<ItunesResponse>,
                ) {
                    when (response.code()) {
                        200 -> {
                            if (response.body()?.results?.isNotEmpty() == true) {
                                tracks.clear()
                                tracks.addAll(response.body()?.results!!)
                                searchAdapter.notifyDataSetChanged()
                                showMessage("", "", ResultResponse.SUCCESS)
                            } else {
                                showMessage(
                                    getString(R.string.nothing_found),
                                    "",
                                    ResultResponse.EMPTY
                                )
                            }
                        }

                        else -> showMessage(
                            getString(R.string.something_went_wrong),
                            response.code().toString(),
                            ResultResponse.ERROR
                        )
                    }
                }

                override fun onFailure(call: Call<ItunesResponse>, t: Throwable) {
                    showMessage(
                        getString(R.string.something_went_wrong),
                        t.message.toString(),
                        ResultResponse.ERROR
                    )
                }

            })
    }

    private fun showMessage(text: String, additionalMessage: String, errorType: ResultResponse) {
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

}