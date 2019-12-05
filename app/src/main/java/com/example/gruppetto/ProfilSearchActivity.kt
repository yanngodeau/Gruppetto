package com.example.gruppetto

import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.example.gruppetto.Adapters.LocationAdapter
import com.example.gruppetto.Models.CardLocation
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import kotlinx.android.synthetic.main.cards_list.*
import java.util.ArrayList


class ProfilSearchActivity : AppCompatActivity(){

    private lateinit var auth: FirebaseAuth
    private lateinit var user: String
    private lateinit var db : FirebaseFirestore
    private lateinit var storage : FirebaseStorage


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_profil_search)
        setSupportActionBar(findViewById(R.id.my_toolbar))
        supportActionBar?.setDisplayHomeAsUpEnabled(true)



        auth = FirebaseAuth.getInstance()
        //user = auth.currentUser!!.uid
        db = FirebaseFirestore.getInstance()
        storage = FirebaseStorage.getInstance()

        val profilName = intent.extras.getString("profilName")

        getUserProfile(profilName)



    }

    private fun getUserProfile(profilName: String) {
        val refUser = db.collection("users")
            .whereEqualTo("name",profilName)
            .get()
            .addOnSuccessListener { documents ->
                for (document in documents) {
                    user = document["id"].toString()
                }
                getLocationHistory()
                readProfil()
            }
    }


    private fun readProfil() {

        //partie database
        val nameText : TextView = findViewById(R.id.profilText)
        var photoUrl = "gs://gruppetto-37713.appspot.com/blank.png"


        val ref = db.collection("users").document(user)
        ref.get().addOnSuccessListener { document ->
            if (document != null) {
                nameText.text = document["name"].toString()
                photoUrl = document["photoUrl"].toString()

                //download profile picture
                Log.w("PROFIL", photoUrl)
                val storageRef = storage.getReferenceFromUrl(photoUrl)
                val imageProfil = findViewById<ImageView>(R.id.profilPicture)
                val ONE_MEGABYTE: Long = 1024 * 1024
                storageRef.getBytes(ONE_MEGABYTE).addOnSuccessListener{
                        data ->
                    val bitmap : Bitmap = BitmapFactory.decodeByteArray(data,0,data.size)
                    imageProfil.setImageBitmap(bitmap)

                }

            } else {
                Log.w("PROFIL", "Profil null")
            }
        }

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
    }


    //nécessaire pour le bouton retour de l'app bar
    override fun onSupportNavigateUp(): Boolean {
        finish()
        return true
    }

}
