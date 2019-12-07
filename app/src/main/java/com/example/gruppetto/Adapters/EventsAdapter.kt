package com.example.gruppetto.Adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.TextView
import com.example.gruppetto.Models.CardEvent
import com.example.gruppetto.R

class EventsAdapter(private val context: Context,
                      private val dataSource: ArrayList<CardEvent>): BaseAdapter() {

    private val inflater: LayoutInflater
            = context.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater

    override fun getView(position: Int, convertView: View?, parent: ViewGroup?): View {
        val rowView = inflater.inflate(R.layout.list_item_events, parent, false)
        val eventName = rowView.findViewById<TextView>(R.id.eventName)
        val address = rowView.findViewById<TextView>(R.id.address)
        val startingDate = rowView.findViewById<TextView>(R.id.startingDate)
        val startingTime = rowView.findViewById<TextView>(R.id.startingTime)
        val endingDate = rowView.findViewById<TextView>(R.id.endingDate)
        val endingTime = rowView.findViewById<TextView>(R.id.endingTime)

        val event = getItem(position)
        eventName.text = event.eventName
        address.text = event.address
        startingDate.text = event.startingDate
        startingTime.text = event.startingTime
        endingDate.text = event.endingDate
        endingTime.text = event.endingTime
        return rowView
    }

    override fun getItem(position: Int): CardEvent {
        return dataSource[position]
    }

    override fun getItemId(position: Int): Long {
        return position.toLong()
    }

    override fun getCount(): Int {
        return dataSource.size
    }

}