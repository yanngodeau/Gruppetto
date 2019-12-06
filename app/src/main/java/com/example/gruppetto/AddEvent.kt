package com.example.gruppetto

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import android.os.Bundle
import android.widget.*
import androidx.appcompat.app.AppCompatActivity

class AddEvent : AppCompatActivity(){

    private lateinit var auth: FirebaseAuth
    private lateinit var db : FirebaseFirestore
    private lateinit var storage : FirebaseStorage

    private var eventName : EditText = findViewById(R.id.event_name)
    private var localisation : EditText = findViewById(R.id.localisation)
    private var startingDate : DatePicker = findViewById(R.id.datePicker1)
    private var endingDate : DatePicker = findViewById(R.id.datePicker2)
    private var startingTime : TimePicker = findViewById(R.id.timePicker1)
    private var endingTime : TimePicker = findViewById(R.id.timePicker2)
    private var allDayLong : CheckBox = findViewById(R.id.checkBox1)
    private var isChecked = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.add_event)
        //setSupportActionBar(findViewById(R.id.my_toolbar))
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        auth = FirebaseAuth.getInstance()
        db = FirebaseFirestore.getInstance()
        storage = FirebaseStorage.getInstance()

        val btnAddEvent = findViewById<Button>(R.id.addEventButton)

        btnAddEvent.setOnClickListener {
            addEvent()
        }

    }

    private fun addEvent() {

        if (allDayLong.isChecked) {
            isChecked = true
        }

        val event = HashMap<String,Any>()
        event["name"] = eventName
        event["address"] = localisation
        event["startingDate"] = startingDate as String
        event["endingDate"] = endingDate as String
        event["startingTime"] = startingTime as String
        event["endingTime"] = endingTime as String
        event["allDayLong"] = isChecked

        //On ajoute un nouveau doc avec un id généré automatiquement
        db.collection("evenemenet").add(event)

    }

    /*
    //nécessaire pour le bouton retour de l'app bar
    override fun onSupportNavigateUp(): Boolean {
        finish()
        return true
    }

    //lien entre appbar et menu xml
    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.app_bar_profil, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem) = when (item.itemId) {
        R.id.action_edit -> {
            val intent = Intent(this, ProfilEditActivity::class.java)
            startActivity(intent)
            true
        }
        R.id.action_logout -> {
            auth.signOut()
            val intent = Intent(this, LoginActivity::class.java)
            startActivity(intent)
            true
        }
        else -> {
            super.onOptionsItemSelected(item)
        }
    }

     */
}
