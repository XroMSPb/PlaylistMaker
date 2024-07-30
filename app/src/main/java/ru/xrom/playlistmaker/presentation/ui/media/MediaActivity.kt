package ru.xrom.playlistmaker.presentation.ui.media

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import ru.xrom.playlistmaker.R

class MediaActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_media)
        window.statusBarColor = resources.getColor(R.color.status_bar, theme)
        window.navigationBarColor = resources.getColor(R.color.navigation_bar, theme)
        val toolbar = findViewById<Toolbar>(R.id.toolbar)
        toolbar.setNavigationOnClickListener {
            onBackPressedDispatcher.onBackPressed()
        }

    }
}