package ru.xrom.playlistmaker

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.widget.TextView
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
            if (share.resolveActivity(packageManager) != null) {
                startActivity(share)
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
