package ru.xrom.playlistmaker

import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        //enableEdgeToEdge()
        setContentView(R.layout.activity_main)
        val btnSearch = findViewById<Button>(R.id.btn_search)
        btnSearch.setOnClickListener {
            Toast.makeText(this@MainActivity,"Вы нажали: "+getString(R.string.searchBtn_text),Toast.LENGTH_SHORT).show()
        }
        val btnLibrary = findViewById<Button>(R.id.btn_library)
        val btnLibraryClickListener: View.OnClickListener = object : View.OnClickListener { override fun onClick(v: View?) {
            Toast.makeText(this@MainActivity,"Вы нажали: "+getString(R.string.libraryBtn_text),Toast.LENGTH_SHORT).show()
        } }
        btnLibrary.setOnClickListener(btnLibraryClickListener)

        val btnSettings = findViewById<Button>(R.id.btn_settings)
        btnSettings.setOnClickListener {
            Toast.makeText(this@MainActivity,"Вы нажали: "+getString(R.string.searchBtn_text),Toast.LENGTH_SHORT).show()
        }

    }
}