package ru.xrom.playlistmaker

import android.os.Bundle
import android.widget.LinearLayout
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.SwitchCompat
import androidx.appcompat.widget.Toolbar

class SettingsActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        //enableEdgeToEdge()
        setContentView(R.layout.activity_settings)
        val toolbar = findViewById<Toolbar>(R.id.toolbar)
        toolbar.setNavigationOnClickListener{
            onBackPressedDispatcher.onBackPressed()
        }

        val swDarkTheme = findViewById<SwitchCompat>(R.id.dark_theme)
        swDarkTheme.setOnClickListener {
            Toast.makeText(this@SettingsActivity,"Вы нажали: "+getString(R.string.darkTheme),Toast.LENGTH_SHORT).show()
        }

        val lineShare = findViewById<LinearLayout>(R.id.share)
        lineShare.setOnClickListener {
            Toast.makeText(this@SettingsActivity,"Вы нажали: "+getString(R.string.share),Toast.LENGTH_SHORT).show()
        }

        val lineSupport = findViewById<LinearLayout>(R.id.support)
        lineSupport.setOnClickListener {
            Toast.makeText(this@SettingsActivity,"Вы нажали: "+getString(R.string.support),Toast.LENGTH_SHORT).show()
        }

        val lineTerms = findViewById<LinearLayout>(R.id.terms)
        lineTerms.setOnClickListener {
            Toast.makeText(this@SettingsActivity,"Вы нажали: "+getString(R.string.terms),Toast.LENGTH_SHORT).show()
        }
    }

}
