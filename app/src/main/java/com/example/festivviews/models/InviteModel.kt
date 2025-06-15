package com.example.festivviews.models

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class InviteModel(
    var pushId: String ?= null,
    var from_uid: String?= null,
    var to_uid: String?= null,
    var event_id: String ?= null,
    var accepted: Boolean?= null
) : Parcelable {
}