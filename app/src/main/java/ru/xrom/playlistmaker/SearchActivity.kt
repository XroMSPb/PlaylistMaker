package ru.xrom.playlistmaker


import android.annotation.SuppressLint
import android.content.Context
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.MotionEvent
import android.view.View.OnTouchListener
import android.view.inputmethod.InputMethodManager
import android.widget.EditText
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar


class SearchActivity : AppCompatActivity() {
    private var clearButtonVisibility = false
    private var searchValue = TEXT_DEF

    companion object {
        const val SEARCH_TEXT = "SEARCH_TEXT"
        const val TEXT_DEF = ""
        const val DRAWABLE_RIGHT = 2
    }

    @SuppressLint("ClickableViewAccessibility")
    override fun onCreate(savedInstanceState: Bundle?) {


        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_search)


        val toolbar = findViewById<Toolbar>(R.id.toolbar)
        toolbar.setNavigationOnClickListener{
            onBackPressedDispatcher.onBackPressed()
        }

        val searchBar = findViewById<EditText>(R.id.search_bar)
        searchBar.requestFocus()
        searchBar.setText(searchValue)

        val simpleTextWatcher = object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
                // empty
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                clearButtonVisibility(s, searchBar)
                searchValue = s.toString()
            }

            override fun afterTextChanged(s: Editable?) {
                // empty
            }
        }
        searchBar.addTextChangedListener(simpleTextWatcher)

        searchBar.setOnTouchListener(OnTouchListener { _, event ->
            if (clearButtonVisibility) {
                if (event.action == MotionEvent.ACTION_UP) {
                    if (event.rawX + searchBar.getCompoundDrawables()[DRAWABLE_RIGHT].getBounds()
                            .width() >= searchBar.right - searchBar.getCompoundDrawables()[DRAWABLE_RIGHT].getBounds()
                            .width()
                    ) {
                        searchBar.setText("")
                        return@OnTouchListener true
                    }
                }
            }
            false
        })
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.putString(SEARCH_TEXT, searchValue)
    }

    override fun onRestoreInstanceState(savedInstanceState: Bundle) {
        super.onRestoreInstanceState(savedInstanceState)
        searchValue = savedInstanceState.getString(SEARCH_TEXT, TEXT_DEF)
    }
    private fun clearButtonVisibility(s: CharSequence?, v: EditText) {
        clearButtonVisibility = if (s.isNullOrEmpty()) {
            v.setCompoundDrawablesWithIntrinsicBounds(R.drawable.search_icon, 0, 0, 0);
            val inputMethodManager =
                getSystemService(Context.INPUT_METHOD_SERVICE) as? InputMethodManager
            inputMethodManager?.hideSoftInputFromWindow(v.windowToken, 0)
            false
        } else {
            v.setCompoundDrawablesWithIntrinsicBounds(
                R.drawable.search_icon,
                0,
                R.drawable.cancel_icon,
                0
            );
            true
        }
    }

}