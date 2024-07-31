package ru.xrom.playlistmaker.presentation.ui.settings

import android.content.ActivityNotFoundException
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.SwitchCompat
import androidx.appcompat.widget.Toolbar
import ru.xrom.playlistmaker.App
import ru.xrom.playlistmaker.R

class SettingsActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_settings)
        window.statusBarColor = resources.getColor(R.color.status_bar, theme)
        window.navigationBarColor = resources.getColor(R.color.navigation_bar, theme)
        val toolbar = findViewById<Toolbar>(R.id.toolbar)
        toolbar.setNavigationOnClickListener {
            onBackPressedDispatcher.onBackPressed()
        }

        val swDarkTheme = findViewById<SwitchCompat>(R.id.dark_theme)
        swDarkTheme.isChecked = (applicationContext as App).darkTheme
        swDarkTheme.setOnCheckedChangeListener { _, checked ->
            (applicationContext as App).switchTheme(checked)
        }


        val lineShare = findViewById<TextView>(R.id.share)
        lineShare.setOnClickListener {
            val share = Intent.createChooser(Intent().apply {
                action = Intent.ACTION_SEND
                putExtra(Intent.EXTRA_TEXT, getString(R.string.practikumLink))
                setType("text/plain")
                putExtra(Intent.EXTRA_TITLE, getString(R.string.practikumHeader))
            }, null)
            startActivity(share)
        }

        val lineSupport = findViewById<TextView>(R.id.support)
        lineSupport.setOnClickListener {
            val share = Intent.createChooser(Intent().apply {
                action = Intent.ACTION_SENDTO
                data = Uri.parse("mailto:")
                putExtra(Intent.EXTRA_EMAIL, arrayOf(getString(R.string.supportMail)))
                putExtra(Intent.EXTRA_SUBJECT, getString(R.string.suportSubject))
                putExtra(Intent.EXTRA_TEXT, getString(R.string.supportText))
            }, getString(R.string.practikumHeader))
            try {
                startActivity(share)
            } catch (ex: ActivityNotFoundException) {
                Toast.makeText(this, R.string.application_not_found, Toast.LENGTH_SHORT).show()
            }
        }

        val lineTerms = findViewById<TextView>(R.id.terms)
        lineTerms.setOnClickListener {
            val share = Intent.createChooser(Intent().apply {
                action = Intent.ACTION_VIEW
                data = Uri.parse(getString(R.string.termsLink))
            }, null)
            startActivity(share)
        }
    }

}
