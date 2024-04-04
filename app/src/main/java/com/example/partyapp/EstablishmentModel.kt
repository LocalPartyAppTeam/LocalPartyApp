package com.example.partyapp

import android.os.Parcel
import android.os.Parcelable

data class EstablishmentModel(
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
    constructor(parcel: Parcel) : this(
        parcel.readString(),
        parcel.readDouble(),
        parcel.readDouble(),
        parcel.readString(),
        parcel.readString(),
        parcel.readString(),
        parcel.createStringArrayList(),
        parcel.createStringArrayList(),
        parcel.createStringArrayList()
    ) {
    }

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeString(ownerAccount)
        parcel.writeDouble(lat!!)
        parcel.writeDouble(long!!)
        parcel.writeString(name)
        parcel.writeString(desc)
        parcel.writeString(address)
        parcel.writeList(imgPaths)
        parcel.writeList(tags)
        parcel.writeList(sanitizedTags)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<EstablishmentModel> {
        override fun createFromParcel(parcel: Parcel): EstablishmentModel {
            return EstablishmentModel(parcel)
        }

        override fun newArray(size: Int): Array<EstablishmentModel?> {
            return arrayOfNulls(size)
        }
    }
}
