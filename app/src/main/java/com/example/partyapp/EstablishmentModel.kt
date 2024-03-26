package com.example.partyapp

data class EstablishmentModel(
    var ownerAccount: String?= null,
    var lat: Double?= null,
    var long: Double?= null,
    var name: String?= null,
    var desc: String?= null,
    var imgPaths: MutableList<String>?= null)
