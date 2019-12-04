package com.example.gruppetto.Adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.TextView
import com.example.gruppetto.Models.CardLocation
import com.example.gruppetto.R

class LocationAdapter(private val context: Context,
                      private val dataSource: ArrayList<CardLocation>): BaseAdapter() {

    private val inflater: LayoutInflater
            = context.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater

    override fun getView(position: Int, convertView: View?, parent: ViewGroup?): View {
        val rowView = inflater.inflate(R.layout.list_item_location, parent, false)
        val locationText = rowView.findViewById<TextView>(R.id.current_location)
        val addressText = rowView.findViewById<TextView>(R.id.current_address)

        val location = getItem(position)
        locationText.text = location.location
        addressText.text = location.address
        return rowView
    }

    override fun getItem(position: Int): CardLocation {
        return dataSource[position]
    }

    override fun getItemId(position: Int): Long {
        return position.toLong()
    }

    override fun getCount(): Int {
        return dataSource.size
    }

}