package com.example.festivviews.models

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class InviteUserModel(
    var user: UserModel?= null,
    var invited: Boolean?= null
): Parcelable{}
