package ru.xrom.playlistmaker.main.ui.main

import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import ru.xrom.playlistmaker.R
import ru.xrom.playlistmaker.databinding.ActivityMainBinding
import ru.xrom.playlistmaker.media.ui.media.MediaActivity
import ru.xrom.playlistmaker.search.ui.SearchActivity
import ru.xrom.playlistmaker.settings.ui.SettingsActivity

class MainActivity : AppCompatActivity() {
    private val binding: ActivityMainBinding by lazy {
        ActivityMainBinding.inflate(layoutInflater)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)
        window.statusBarColor = resources.getColor(R.color.main_status_bar, theme)
        window.navigationBarColor = resources.getColor(R.color.main_navigation_bar, theme)

        binding.btnSearch.setOnClickListener {
            val intent = Intent(this, SearchActivity::class.java)
            startActivity(intent)
        }

        val btnLibraryClickListener: View.OnClickListener = View.OnClickListener {
            val intent = Intent(this, MediaActivity::class.java)
            startActivity(intent)
        }

        binding.btnLibrary.setOnClickListener(btnLibraryClickListener)

        binding.btnSettings.setOnClickListener {
            val intent = Intent(this, SettingsActivity::class.java)
            startActivity(intent)
        }
    }
}