package com.example.gruppetto

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.ListView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.mancj.materialsearchbar.MaterialSearchBar


class SearchActivity : AppCompatActivity() {

    private var userList = arrayListOf<String>()
    private lateinit var userListView: ListView
    private lateinit var db: FirebaseFirestore
    private lateinit var auth: FirebaseAuth
    private lateinit var userUID: String
    private lateinit var inputSearch: MaterialSearchBar

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.acitvity_search)

        userListView = findViewById(R.id.search_listView)
        inputSearch = findViewById(R.id.search)

        auth = FirebaseAuth.getInstance()
        userUID = auth.currentUser!!.uid
        db = FirebaseFirestore.getInstance()


        val ref = db.collection("users")
            .get()
            .addOnSuccessListener { result ->
                if (result != null) {
                    userList = arrayListOf<String>()
                    for (document in result) {
                        userList.add(
                            document["name"].toString()
                        )
                    }
                }

                val adapter =
                    ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, userList)
                userListView.adapter = adapter

                inputSearch.addTextChangeListener(object : TextWatcher {
                    override fun beforeTextChanged(
                        charSequence: CharSequence,
                        i: Int,
                        i1: Int,
                        i2: Int
                    ) {

                    }

                    override fun onTextChanged(
                        charSequence: CharSequence,
                        i: Int,
                        i1: Int,
                        i2: Int
                    ) {
                        //SEARCH FILTER
                        adapter.filter.filter(charSequence)
                    }

                    override fun afterTextChanged(editable: Editable) {

                    }
                })

                userListView.onItemClickListener =
                    AdapterView.OnItemClickListener { adapterView, view, i, l ->
                        Toast.makeText(
                            this@SearchActivity,
                            adapter.getItem(i)!!.toString(),
                            Toast.LENGTH_SHORT
                        ).show()
                    }

            }
    }
}