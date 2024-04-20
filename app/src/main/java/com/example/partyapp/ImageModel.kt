
package com.example.partyapp
import android.net.Uri
import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class ImageModel(
    var image: Uri? = null,
    var imgPath: String? = null
): Parcelable{}