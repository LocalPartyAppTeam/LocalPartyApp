package com.example.partyapp
import android.graphics.Bitmap
import android.net.Uri

data class AddPictureRVEntryModel(
    var image: Uri? = null,
    var imgPath: String? = null
)