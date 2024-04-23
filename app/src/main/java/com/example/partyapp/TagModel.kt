package com.example.partyapp

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class TagModel(
    val backgroundColor: String ?= "#000000",
    val textColor: String ?= "#FFFFFF",
    val text: String ?= null,
): Parcelable{}
