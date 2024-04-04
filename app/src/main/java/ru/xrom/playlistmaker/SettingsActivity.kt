package ru.xrom.playlistmaker

import android.os.Bundle
import android.widget.ImageView
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

        val imgShare = findViewById<ImageView>(R.id.share)
        imgShare.setOnClickListener {
            Toast.makeText(this@SettingsActivity,"Вы нажали: "+getString(R.string.share),Toast.LENGTH_SHORT).show()
        }

        val imgSupport = findViewById<ImageView>(R.id.support)
        imgSupport.setOnClickListener {
            Toast.makeText(this@SettingsActivity,"Вы нажали: "+getString(R.string.support),Toast.LENGTH_SHORT).show()
        }

        val imgTerms = findViewById<ImageView>(R.id.terms)
        imgTerms.setOnClickListener {
            Toast.makeText(this@SettingsActivity,"Вы нажали: "+getString(R.string.terms),Toast.LENGTH_SHORT).show()
        }
    }

}
