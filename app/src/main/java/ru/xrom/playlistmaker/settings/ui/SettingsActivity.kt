package ru.xrom.playlistmaker.settings.ui

import android.content.ActivityNotFoundException
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import ru.xrom.playlistmaker.R
import ru.xrom.playlistmaker.databinding.ActivitySettingsBinding
import ru.xrom.playlistmaker.utils.App

class SettingsActivity : AppCompatActivity() {
    private val binding: ActivitySettingsBinding by lazy {
        ActivitySettingsBinding.inflate(layoutInflater)
    }

    private lateinit var viewModel: SettingsViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)
        window.statusBarColor = resources.getColor(R.color.status_bar, theme)
        window.navigationBarColor = resources.getColor(R.color.navigation_bar, theme)

        binding.toolbar.setNavigationOnClickListener {
            onBackPressedDispatcher.onBackPressed()
        }

        binding.darkTheme.isChecked = (applicationContext as App).darkTheme
        binding.darkTheme.setOnCheckedChangeListener { _, checked ->
            (applicationContext as App).switchTheme(checked)
        }

        binding.share.setOnClickListener {
            val share = Intent.createChooser(Intent().apply {
                action = Intent.ACTION_SEND
                putExtra(Intent.EXTRA_TEXT, getString(R.string.practikumLink))
                setType("text/plain")
                putExtra(Intent.EXTRA_TITLE, getString(R.string.practikumHeader))
            }, null)
            startActivity(share)
        }

        binding.support.setOnClickListener {
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

        binding.terms.setOnClickListener {
            val share = Intent.createChooser(Intent().apply {
                action = Intent.ACTION_VIEW
                data = Uri.parse(getString(R.string.termsLink))
            }, null)
            startActivity(share)
        }
    }
}
