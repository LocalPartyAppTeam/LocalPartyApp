package com.example.partyapp.models

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class EventModel(
    var pushId: String?= null,
    var host: String?= null,
    var lat: Double?= null,
    var long: Double?= null,
    var address: String?= null,
    var start: String?= null,
    var end: String?= null,
    var name: String?= null,
    var desc: String?= null,
    var imgPaths: MutableList<String>?= null,
    var tags: MutableList<String>?= null,
    var sanitizedTags: MutableList<String>?= null
): Parcelable {
}
