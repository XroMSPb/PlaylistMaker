package ru.xrom.playlistmaker.main.ui.main

import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import ru.xrom.playlistmaker.R
import ru.xrom.playlistmaker.databinding.ActivityMainBinding
import ru.xrom.playlistmaker.media.ui.MediaFragment
import ru.xrom.playlistmaker.search.ui.SearchFragment
import ru.xrom.playlistmaker.settings.ui.SettingsFragment

class MainActivity : AppCompatActivity() {
    private val binding: ActivityMainBinding by lazy {
        ActivityMainBinding.inflate(layoutInflater)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)
        window.statusBarColor = resources.getColor(R.color.main_status_bar, theme)
        window.navigationBarColor = resources.getColor(R.color.main_navigation_bar, theme)

        
    }
}