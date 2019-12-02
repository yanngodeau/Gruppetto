package com.example.gruppetto

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        var profilButton : Button = findViewById(R.id.buttonProfil)
        profilButton.setOnClickListener { openProfil() }
    }

    private fun openProfil() {
        val intent = Intent(this, ProfilActivity::class.java)
        startActivity(intent)
    }
}
