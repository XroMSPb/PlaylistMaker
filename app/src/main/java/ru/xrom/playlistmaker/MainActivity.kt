package ru.xrom.playlistmaker

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        //enableEdgeToEdge()
        setContentView(R.layout.activity_main)
        val btnSearch = findViewById<Button>(R.id.btn_search)
        btnSearch.setOnClickListener {
            //Toast.makeText(this@MainActivity,"Вы нажали: "+getString(R.string.searchBtn_text),Toast.LENGTH_SHORT).show()
            val intent = Intent(this@MainActivity, SearchActivity::class.java)
            startActivity(intent)
        }
        val btnLibrary = findViewById<Button>(R.id.btn_library)
        val btnLibraryClickListener: View.OnClickListener = object : View.OnClickListener { override fun onClick(v: View?) {
            //Toast.makeText(this@MainActivity,"Вы нажали: "+getString(R.string.libraryBtn_text),Toast.LENGTH_SHORT).show()
            val intent = Intent(this@MainActivity, MediaActivity::class.java)
            startActivity(intent)
        } }
        btnLibrary.setOnClickListener(btnLibraryClickListener)

        val btnSettings = findViewById<Button>(R.id.btn_settings)
        btnSettings.setOnClickListener {
            //Toast.makeText(this@MainActivity,"Вы нажали: "+getString(R.string.settingsBtn_text),Toast.LENGTH_SHORT).show()
            val intent = Intent(this@MainActivity, SettingsActivity::class.java)
            startActivity(intent)
        }

    }
}