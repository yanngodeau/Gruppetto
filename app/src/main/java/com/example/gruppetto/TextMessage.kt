package com.example.gruppetto

open class TextMessage{
    lateinit var sender: String
    var text: String
    var date: String

    private lateinit var messages: List<TextMessage>

    constructor(sender: String, text: String, date: String) {
        this.sender = sender
        this.text = text
        this.date = date
    }

    constructor(text: String, date: String) {
        this.text = text
        this.date = date
    }

    private fun initializeData() {
    }
}