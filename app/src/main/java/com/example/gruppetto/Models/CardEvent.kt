package com.example.gruppetto.Models

open class CardEvent {
    var eventName: String
    var address: String
    var startingDate : String
    var endingDate : String
    var startingTime : String
    var endingTime : String


    constructor(eventName: String, address: String, startingDate : String, startingTime : String, endingDate : String, endingTime : String) {
        this.eventName = eventName
        this.address = address
        this.startingDate = startingDate
        this.startingTime = startingTime
        this.endingDate = endingDate
        this.endingTime = endingTime
    }


    private fun initializeData() {
    }
}