package com.example.partyapp

import android.os.Parcel
import android.os.Parcelable

data class LocalEstablishment(
    val establishmentName: String?,
    val address: String?,
    val distance: String?,
    val description: String?,
    val ownerAccount: String?,
    val iP: MutableList<String>?
):Parcelable {
    constructor(parcel: Parcel) : this(
        parcel.readString(),
        parcel.readString(),
        parcel.readString(),
        parcel.readString(),
        parcel.readString(),
        TODO("iP")
    ) {
    }

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeString(establishmentName)
        parcel.writeString(address)
        parcel.writeString(distance)
        parcel.writeString(description)
        parcel.writeString(ownerAccount)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<LocalEstablishment> {
        override fun createFromParcel(parcel: Parcel): LocalEstablishment {
            return LocalEstablishment(parcel)
        }

        override fun newArray(size: Int): Array<LocalEstablishment?> {
            return arrayOfNulls(size)
        }
    }
}
