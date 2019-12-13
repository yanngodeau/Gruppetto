package com.example.gruppetto

import android.content.Intent
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.gruppetto.Adapters.EventsAdapter
import com.example.gruppetto.Adapters.LocationAdapter
import com.example.gruppetto.Models.CardEvent
import com.example.gruppetto.Models.CardLocation
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.android.synthetic.main.bottom_sheet.view.*
import kotlinx.android.synthetic.main.cards_list.*
import java.util.*

class EventsList : AppCompatActivity() {

    private lateinit var auth: FirebaseAuth
    private lateinit var user: String
    private lateinit var db: FirebaseFirestore


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.events_list)
        setSupportActionBar(findViewById(R.id.my_toolbar))
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        auth = FirebaseAuth.getInstance()
        user = auth.currentUser!!.uid
        db = FirebaseFirestore.getInstance()
        getEvents()

    }

    /**
     * Récupère la liste des évènements
     */
    private fun getEvents() {
        val ref = db.collection("evenement")
        ref.get().addOnSuccessListener { result ->

            if (result != null) {
                var eventList = arrayListOf<CardEvent>()
                for (document in result) {
                    eventList.add(
                        CardEvent(
                            document["name"].toString(),
                            document["address"].toString(),
                            document["startingDate"].toString(),
                            document["startingTime"].toString(),
                            document["endingDate"].toString(),
                            document["endingTime"].toString()
                        )
                    )
                }
                setUpEventList(eventList)
            }

        }
    }

    private fun setUpEventList(cardEventList: ArrayList<CardEvent>) {
        val cardEVent =
            CardEvent("Tournoi de pétanque", "12 avenue Etienne Billières, 31000 Toulouse, France", "07/12/2019", "07/12/2019","18:00","21:00")

        cardEventList.add(cardEVent)

        val adapter = EventsAdapter(this, cardEventList)
        card_listView.adapter = adapter

    }


    //nécessaire pour le bouton retour de l'app bar
    override fun onSupportNavigateUp(): Boolean {
        finish()
        return true
    }

    //lien entre appbar et menu xml
    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.app_bar_event, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem) : Boolean  {
        when (item.itemId) {
            R.id.action_add_event -> {
                val intent = Intent(this, AddEvent::class.java)
                startActivity(intent)
                return true
            }
        }
        return super.onOptionsItemSelected(item)
    }
}