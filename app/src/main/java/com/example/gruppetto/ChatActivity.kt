package com.example.gruppetto

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.gruppetto.Adapters.LocationAdapter
import com.example.gruppetto.Models.CardLocation
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.android.synthetic.main.bottom_sheet.view.*
import kotlinx.android.synthetic.main.cards_list.*
import java.util.*

class ChatActivity : AppCompatActivity() {

    private lateinit var auth: FirebaseAuth
    private lateinit var user: String
    private lateinit var db : FirebaseFirestore


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_chat)
        setSupportActionBar(findViewById(R.id.my_toolbar))
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        auth = FirebaseAuth.getInstance()
        user = auth.currentUser!!.uid
        db = FirebaseFirestore.getInstance()
        getLocationHistory()


    }

    private fun getLocationHistory() {
        val ref = db.collection("users").document(user).collection("locations")
        ref.get().addOnSuccessListener { result ->
            if (result != null) {
                var locationUuidList = arrayListOf<String>()
                for (document in result) {
                    locationUuidList.add(document.id)
                }

                //get les détails des locations dans locations/
                db.collection("locations").whereIn("id",locationUuidList).get().addOnSuccessListener {result ->
                    if (result != null) {
                        var locationList = arrayListOf<CardLocation>()
                        for (document in result){
                            locationList.add(
                                CardLocation(
                                    document["title"].toString(),
                                    document["address"].toString()
                                )
                            )
                        }
                        setUpLocationList(locationList)
                    }
                }
            }
        }

    }

    private fun setUpLocationList(cardLocationList: ArrayList<CardLocation>) {
        val cardLocation =
            CardLocation("Place de Verdun", "1 Place de Verdun, 65000 TARBES, France")
        val cardLocation2 =
            CardLocation("Lycée Théophile Gautier", "15 Rue Abbé Torne, 65000 Tarbes, France")

        cardLocationList.add(cardLocation)
        cardLocationList.add(cardLocation2)

        val adapter = LocationAdapter(this, cardLocationList)
        card_listView.adapter = adapter
        card_listView.setOnItemClickListener { parent, view, position, id ->
            val intent = Intent(this, ChatAppActivity::class.java)
            val locTitle = view.current_location.text.toString()
            intent.putExtra("locationTitle",locTitle)
            Toast.makeText(this, "Clicked item :"+" "+locTitle, Toast.LENGTH_SHORT).show()

            startActivity(intent) }

    }


    //nécessaire pour le bouton retour de l'app bar
    override fun onSupportNavigateUp(): Boolean {
        finish()
        return true
    }
}