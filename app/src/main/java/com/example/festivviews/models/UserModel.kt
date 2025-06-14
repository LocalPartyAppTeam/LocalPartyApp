package com.example.festivviews.models

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class UserModel(
    var firstName: String?= null,
    var lastName: String?= null,
    var dob: String?= null,
    var email: String?= null,
    var username: String?= null,
    var uid: String?= null,
    var age: Int?= null,
    var profilePicture: String?= null,
    var idPicture: String?= null,
    var friends: MutableList<String>?= null,
    var idVerified: Boolean?= false): Parcelable{}

