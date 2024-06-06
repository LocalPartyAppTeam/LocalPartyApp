package com.example.partyapp.models

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class EstablishmentModel(
    var pushId: String?= null,
    var ownerAccount: String?= null,
    var lat: Double?= 0.0,
    var long: Double?= 0.0,
    var name: String?= null,
    var desc: String?= null,
    var address: String?= null,
    var imgPaths: MutableList<String>?= null,
    var tags: MutableList<String>?= null,
    var sanitizedTags: MutableList<String>?= null
): Parcelable {
}
