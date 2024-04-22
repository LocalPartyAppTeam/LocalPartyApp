package com.example.partyapp

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class CheckInUserModel(
    var user: UserModel?= null,
    var checkedIn: Boolean?= null
): Parcelable{}
