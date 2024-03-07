package com.example.partyapp

data class EventModel(
    var host: String?= null,
    var lat: Double?= null,
    var long: Double?= null,
    var start: String?= null,
    var end: String?= null,
    var name: String?= null,
    var desc: String?= null,
    var imgPaths: MutableList<String>?= null
)
