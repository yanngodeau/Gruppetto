package com.example.gruppetto

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import kotlinx.android.synthetic.main.activity_profil_edit.*


class ProfilEditActivity : AppCompatActivity() {

    private lateinit var auth: FirebaseAuth
    private lateinit var user: String
    private lateinit var db : FirebaseFirestore
    private lateinit var storage : FirebaseStorage

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_profil_edit)
        setSupportActionBar(findViewById(R.id.my_toolbar))
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        auth = FirebaseAuth.getInstance()
        user = auth.currentUser!!.uid
        db = FirebaseFirestore.getInstance()
        storage = FirebaseStorage.getInstance()

        readProfil()

        val saveButton : Button = findViewById(R.id.save_profile_button)
        saveButton.setOnClickListener {
            saveProfil()
        }

        val uploadButton : Button = findViewById(R.id.upload_image_button)
        uploadButton.setOnClickListener {
            val intent = Intent()
            intent.type = "image/*"
            intent.action = Intent.ACTION_GET_CONTENT
            startActivityForResult(Intent.createChooser(intent, "Select Picture"), 0)
        }

    }

    //result apres avoir pick une photo de la gallerie
    override fun onActivityResult(requestCode:Int, resultCode:Int, data:Intent?){
        super.onActivityResult(requestCode, resultCode, data)
        val uploadImage : ImageView = findViewById(R.id.upload_image)
        if(requestCode == 0 && resultCode == RESULT_OK && data != null && data.data != null ) {
            val filePath : Uri = data.data
            val bitmap = MediaStore.Images.Media.getBitmap(contentResolver, filePath)
            uploadImage.setImageBitmap(bitmap)
        }

    }



    private fun saveProfil() {
        val nameText : EditText = findViewById(R.id.nameText)
        val mailText : EditText = findViewById(R.id.mailText)
        val profilPhoto : ImageView = findViewById(R.id.upload_image)


    }

    private fun readProfil() {


        var url = ""
        val ref = db.collection("users").document(user)
        ref.get().addOnSuccessListener { document ->
            if (document != null) {
                nameText.setText(document["name"].toString())
                mailText.setText(document["mail"].toString())
            } else {
                Log.w("PROFIL", "Profil null")
            }
        }

    }


    //nécessaire pour le bouton retour de l'app bar
    override fun onSupportNavigateUp(): Boolean {
        finish()
        return true
    }
}