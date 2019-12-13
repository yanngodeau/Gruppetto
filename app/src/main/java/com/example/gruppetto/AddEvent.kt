package com.example.gruppetto

import android.content.Intent
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.widget.*
import androidx.appcompat.app.AppCompatActivity

class AddEvent : AppCompatActivity(){

    private lateinit var auth: FirebaseAuth
    private lateinit var db : FirebaseFirestore
    private lateinit var storage : FirebaseStorage

    private lateinit var eventName : EditText
    private lateinit var localisation : EditText
    private lateinit var startingDate : DatePicker
    private lateinit var endingDate : DatePicker
    private lateinit var startingTime : TimePicker
    private lateinit var endingTime : TimePicker
    private lateinit var allDayLong : CheckBox
    private lateinit var btnAddEvent : Button
    private lateinit var succesMessage : TextView
    private var isChecked = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.add_event)
        setSupportActionBar(findViewById(R.id.my_toolbar))
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        auth = FirebaseAuth.getInstance()
        db = FirebaseFirestore.getInstance()
        storage = FirebaseStorage.getInstance()

        eventName = findViewById(R.id.event_name)
        localisation = findViewById(R.id.localisation)
        startingDate = findViewById(R.id.datePicker1)
        endingDate = findViewById(R.id.datePicker2)
        startingTime = findViewById(R.id.timePicker1)
        endingTime = findViewById(R.id.timePicker2)
        allDayLong = findViewById(R.id.checkBox1)
        btnAddEvent = findViewById(R.id.addEventButton)
        succesMessage = findViewById(R.id.successTextMessage)

        btnAddEvent.setOnClickListener {
            addEvent()
        }

    }

    private fun addEvent() {

        if (allDayLong.isChecked) {
            isChecked = true
        }
        var builderInitDate = StringBuilder()
        builderInitDate.append(startingDate.dayOfMonth.toString())
        builderInitDate.append("/")
        builderInitDate.append(startingDate.month.toString())
        builderInitDate.append("/")
        builderInitDate.append(startingDate.year.toString())

        var builderFinalDate = StringBuilder()
        builderFinalDate.append(endingDate.dayOfMonth.toString())
        builderFinalDate.append("/")
        builderFinalDate.append(endingDate.month.toString())
        builderFinalDate.append("/")
        builderFinalDate.append(endingDate.year.toString())

        var builderInitTime = StringBuilder()
        builderInitTime.append(startingTime.hour.toString())
        builderInitTime.append(":")
        builderInitTime.append(startingTime.minute.toString())

        var builderFinalTime = StringBuilder()
        builderFinalTime.append(endingTime.hour.toString())
        builderFinalTime.append(":")
        builderFinalTime.append(endingTime.minute.toString())

        val event = HashMap<String,Any>()
        event["name"] = eventName.text.toString()
        event["address"] = localisation.text.toString()
        event["startingDate"] = builderInitDate.toString()
        event["endingDate"] =  builderFinalDate.toString()
        event["startingTime"] = builderInitTime.toString()
        event["endingTime"] = builderFinalTime.toString()
        event["allDayLong"] = isChecked

        //On ajoute un nouveau doc avec un id généré automatiquement
        db.collection("evenement").add(event)

        //Puis on réinitialise les champs et on affiche un message de succès
        succesMessage.text = "L'évènement a bien été ajouté !"
        eventName.text = null
        localisation.text = null

    }

    //nécessaire pour le bouton retour de l'app bar
    override fun onSupportNavigateUp(): Boolean {
        finish()
        return true
    }
}
