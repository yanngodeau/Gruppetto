package com.example.gruppetto

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import com.google.firebase.firestore.FirebaseFirestore

class ProfilActivity : AppCompatActivity(){

    private lateinit var auth: FirebaseAuth
    private lateinit var user: String
    private lateinit var db : FirebaseFirestore

    override fun onCreate(savedInstanceState: Bundle?) {
         super.onCreate(savedInstanceState)
         setContentView(R.layout.activity_profil)

        auth = FirebaseAuth.getInstance()
        user = auth.currentUser!!.uid
        db = FirebaseFirestore.getInstance()



        readProfil()
    }

    private fun readProfil() {
        val nameText : TextView = findViewById(R.id.profilText)
        val mailText : TextView = findViewById(R.id.mailText)

        val ref = db.collection("users").document(user)
        ref.get().addOnSuccessListener { document ->
            if (document != null) {
                nameText.text = document["name"].toString()
                mailText.text = document["mail"].toString()
            } else {
                Log.w("PROFIL", "Profil null")
            }
        }

    }

}
