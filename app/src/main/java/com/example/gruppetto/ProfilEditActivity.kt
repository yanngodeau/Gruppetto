package com.example.gruppetto

import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.drawable.BitmapDrawable
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.text.TextUtils
import android.util.Log
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import androidx.appcompat.app.AppCompatActivity
import androidx.core.graphics.scale
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.UserProfileChangeRequest
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import kotlinx.android.synthetic.main.activity_profil_edit.*
import org.w3c.dom.Text
import java.io.ByteArrayOutputStream


class ProfilEditActivity : AppCompatActivity() {

    private lateinit var auth: FirebaseAuth
    private lateinit var user: String
    private lateinit var db : FirebaseFirestore
    private lateinit var storage : FirebaseStorage
    private var photoChanged : Boolean = false

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
        if(requestCode == 0 && resultCode == RESULT_OK && data != null && data.data != null ) {
            val filePath : Uri = data.data
            val bitmap = MediaStore.Images.Media.getBitmap(contentResolver, filePath)
            upload_image.setImageBitmap(bitmap.scale(400,300,true))
            photoChanged = true
        }

    }



    private fun saveProfil() {
        //update db
        val userRef = db.collection("users").document(user)
        userRef.update("name", nameText.text.toString())
            .addOnSuccessListener{ Log.w("Name","Name updated")}
            .addOnFailureListener { Log.w("Name", "Name not updated") }

        userRef.update("mail",mailText.text.toString())
            .addOnSuccessListener {
                //update auth
                auth.currentUser?.updateEmail(mailText.text.toString())
                Log.w("Mail","Mail updated")}
            .addOnFailureListener { Log.w("Mail", "Mail not updated") }

        //update pw si besoin
        if (!TextUtils.isEmpty(pwText.text)) {
            if (pwText.text.length >= 6){
                auth.currentUser?.updatePassword(pwText.text.toString())
                    ?.addOnSuccessListener { Log.w("Password", "Password updated") }
                    ?.addOnFailureListener { Log.w("Password", "Password not updated") }
            } else {
                pwText.error = ("6 caractères minimum")
            }

        }

        //upload image

        if (photoChanged) {
            var photoUrl = "gs://gruppetto-37713.appspot.com/blank.png"
            val storageRef = storage.reference.child("users/"+user+".png")
            val bitmap = (upload_image.drawable as BitmapDrawable).bitmap
            val baos = ByteArrayOutputStream()
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos)
            val data = baos.toByteArray()
            var uploadTask = storageRef.putBytes(data)
            uploadTask.addOnFailureListener {
                Log.w("Upload photo","FAIL")
            }.addOnSuccessListener {
                    taskSnapshot ->
                storageRef.downloadUrl.addOnSuccessListener {
                    photoUrl = it.toString()
                    Log.w("photoUrl",photoUrl)
                    userRef.update("photoUrl",photoUrl)
                        .addOnSuccessListener {
                            Log.w("photoUrl","photoUrl updated")
                            finish()}
                        .addOnFailureListener { Log.w("photoUrl", "photoUrl not updated")}
                }
            }
        } else {
            finish()
        }




    }

    private fun readProfil() {


        var photoUrl = "gs://gruppetto-37713.appspot.com/blank.png"
        val ref = db.collection("users").document(user)
        ref.get().addOnSuccessListener { document ->
            if (document != null) {
                nameText.setText(document["name"].toString())
                mailText.setText(document["mail"].toString())
                photoUrl = document["photoUrl"].toString()

                //download profile picture
                Log.w("PROFIL", photoUrl)
                val storageRef = storage.getReferenceFromUrl(photoUrl)
                val ONE_MEGABYTE: Long = 1024 * 1024
                storageRef.getBytes(ONE_MEGABYTE).addOnSuccessListener { data ->
                    val bitmap: Bitmap = BitmapFactory.decodeByteArray(data, 0, data.size)
                    upload_image.setImageBitmap(bitmap)
                }
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