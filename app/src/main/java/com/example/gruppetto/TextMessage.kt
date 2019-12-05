package com.example.gruppetto

import com.google.firebase.Timestamp
import java.util.*

open class TextMessage{
    lateinit var sender: String
    var text: String
    var date: Timestamp

    private lateinit var messages: List<TextMessage>

    constructor(sender: String, text: String, date: Timestamp) {
        this.sender = sender
        this.text = text
        this.date = date
    }

    constructor(text: String, date: Timestamp) {
        this.text = text
        this.date = date
    }

    private fun initializeData() {
    }
}