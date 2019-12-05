package com.example.gruppetto

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.TextView
import com.google.firebase.Timestamp
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import java.util.*
import kotlin.collections.ArrayList

class MessageListAdapter (private val context: Context,
                          private val dataSource: ArrayList<TextMessage>) : BaseAdapter(){

    private val inflater: LayoutInflater
            = context.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater

    private lateinit var auth: FirebaseAuth
    private lateinit var user: String
    private lateinit var db : FirebaseFirestore


    override fun getView(position: Int, convertView: View?, parent: ViewGroup?): View {

        auth = FirebaseAuth.getInstance()
        user = auth.currentUser!!.uid
        db = FirebaseFirestore.getInstance()


        val message = getItem(position)
        var rowView : View

        if (message.sender == user){
            rowView = inflater.inflate(R.layout.item_message_sent,parent,false)
        } else {
            rowView = inflater.inflate(R.layout.item_message_received,parent,false)
            getInfoUser(rowView, message.sender)
        }


        val messageText = rowView.findViewById<TextView>(R.id.text_message_body)
        val heureText = rowView.findViewById<TextView>(R.id.text_message_time)

        messageText.text = message.text

        val cal : Calendar = Calendar.getInstance()
        cal.time = message.date.toDate()
        val heure = cal.get(Calendar.HOUR).toString()+":"+cal.get(Calendar.MINUTE).toString()
        heureText.text = heure

        return rowView
    }

    private fun getInfoUser(rowView : View, sender : String) {
        val nameText = rowView.findViewById<TextView>(R.id.text_message_name)
        val ref = db.collection("users").document(sender)
        ref.get().addOnSuccessListener { document ->
            if (document != null) {
                nameText.text = document["name"].toString()
                //photo de profil à récupérer maybe
            }
        }
    }

    override fun getItem(position: Int): TextMessage {
        return dataSource[position]
    }

    override fun getItemId(position: Int): Long {
        return position.toLong()
    }

    override fun getCount(): Int {
        return dataSource.size
    }

}