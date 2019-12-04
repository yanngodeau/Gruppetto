package com.example.gruppetto.Models

open class CardLocation {
    var location: String
    var address: String
    lateinit var date: String

    private lateinit var locations: List<CardLocation>

    constructor(location: String, address: String, date: String) {
        this.location = location
        this.address = address
        this.date = date
    }

    constructor(location: String, address: String) {
        this.location = location
        this.address = address
    }

    private fun initializeData() {
    }
}