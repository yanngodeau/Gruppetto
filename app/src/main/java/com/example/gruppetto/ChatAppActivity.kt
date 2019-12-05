package com.example.gruppetto

import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.android.synthetic.main.activity_chat_app.*
import java.util.*
import kotlin.collections.ArrayList

class ChatAppActivity : AppCompatActivity() {


    private lateinit var auth: FirebaseAuth
    private lateinit var user: String
    private lateinit var db : FirebaseFirestore
    //location a remplacer par la location cliquée
    private var location : String = "11o24vQBbFxYvX6lb4Ag"


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_chat_app)
        setSupportActionBar(findViewById(R.id.my_toolbar))
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        db = FirebaseFirestore.getInstance()
        auth = FirebaseAuth.getInstance()
        user = auth.currentUser!!.uid

        val locTitle = intent.extras.getString("locationTitle")

        getMessagesRealTime(locTitle)
        //getMessages()

        button_chatbox_send.setOnClickListener {
            sendMessage()
        }

    }

    private fun getMessagesRealTime(locTitle : String) {
        //recherche location
        val refLoc = db.collection("locations")
            .whereEqualTo("title",locTitle)
            .get()
            .addOnSuccessListener { documents ->
                for (document in documents) {
                    location = document["id"].toString()
                }
                val ref = db.collection("locations").document(location).collection("chat")
                    .addSnapshotListener{ value, e ->
                        if (e != null){
                            Log.w("ERROR","Listen failed.",e)
                            return@addSnapshotListener
                        }

                        val messageList = arrayListOf<TextMessage>()
                        for (document in value!!){
                            messageList.add(
                                TextMessage(
                                    document["sender"].toString(),
                                    document["text"].toString(),
                                    document["date"].toString()
                                )
                            )
                        }
                        setUpMessages(messageList)
                    }
            }
    }

    private fun sendMessage() {
        val messageString = edittext_chatbox.text.toString()
        //mauvaise heure
        val hour = Calendar.HOUR_OF_DAY.toString()+":"+Calendar.MINUTE.toString()

        if (messageString != ""){
            val message = hashMapOf(
                "sender" to user,
                "text" to messageString,
                "date" to hour
            )
            val ref = db.collection("locations").document(location).collection("chat")
                .add(message)
                .addOnSuccessListener {
                    val toast =
                    Toast.makeText(
                        applicationContext,
                        "Message sent",
                        Toast.LENGTH_LONG
                    )
                    toast.show()
                }.addOnFailureListener {
                    val toast =
                        Toast.makeText(
                            applicationContext,
                            "Message not sent",
                            Toast.LENGTH_LONG
                        )
                    toast.show()
                }

        }
    }

    private fun getMessages() {
        val ref = db.collection("locations").document(location).collection("chat")
        ref.get().addOnSuccessListener { result ->
            if (result != null) {
                val messageList = arrayListOf<TextMessage>()
                for (document in result) {
                    messageList.add(
                        TextMessage(
                            document["sender"].toString(),
                            document["text"].toString(),
                            document["date"].toString()
                        )
                    )
                }
                setUpMessages(messageList)
            }
        }

    }


    private fun setUpMessages(messageList : ArrayList<TextMessage>) {
/*        val textMessage =
            TextMessage("WlrVZ1FhfIZCa3G1k8WKUb75Phg1","Wassup", "17:52")
        val textMessage2 =
            TextMessage("ZYfm75KoKbT5k1QlxoJvbAr8bTW2","yo yo", "17:53")

        messageList.add(textMessage)
        messageList.add(textMessage2)
        messageList.add(textMessage)
        messageList.add(textMessage2)
        messageList.add(textMessage)
        messageList.add(textMessage)
        messageList.add(textMessage2)
        messageList.add(textMessage2)
        messageList.add(textMessage)
        messageList.add(textMessage2)
        messageList.add(textMessage)
        messageList.add(textMessage)
        messageList.add(textMessage2)
        messageList.add(textMessage2)
        messageList.add(textMessage)
        messageList.add(textMessage2)*/

        val adapter = MessageListAdapter(this, messageList)
        list_message_list.adapter = adapter
    }

    //nécessaire pour le bouton retour de l'app bar
    override fun onSupportNavigateUp(): Boolean {
        finish()
        return true
    }
}