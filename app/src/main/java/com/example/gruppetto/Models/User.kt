package com.example.gruppetto.Models

import java.io.Serializable

class User: Serializable {
    var name: String
    var mail: String
    lateinit var photoUrl: String

    constructor(name: String, mail: String, photoUrl: String) {
        this.name = name
        this.mail = mail
        this.photoUrl = photoUrl
    }

    constructor(name: String, mail: String) {
        this.name = name
        this.mail = mail
    }


}