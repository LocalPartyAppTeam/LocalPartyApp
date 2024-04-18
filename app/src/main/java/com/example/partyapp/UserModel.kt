package com.example.partyapp

data class UserModel(
    var firstName: String?= null,
    var lastName: String?= null,
    var email: String?= null,
    var username: String?= null,
    var uid: String?= null,
    var age: Int?= null,
    var profilePicture: String?= null,
    var idPicture: String?= null,
    var friends: MutableList<String>?= null,
    var idVerified: Boolean?= false)

