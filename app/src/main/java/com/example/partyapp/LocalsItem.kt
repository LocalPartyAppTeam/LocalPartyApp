package com.example.partyapp

import android.os.Parcel
import android.os.Parcelable

data class LocalItem(
    val eventName: String?,
    val host: String?,
    val address: String?,
    val startTime: String?,
    val endTime: String?,
    val distance: String?,
    val dayOfWeek: String?,
    val dayOfMonth: String?,
    val dateFormatter: String?,
    val description: String?,
    val iP: MutableList<String>?
):Parcelable {
    constructor(parcel: Parcel) : this(
        parcel.readString(),
        parcel.readString(),
        parcel.readString(),
        parcel.readString(),
        parcel.readString(),
        parcel.readString(),
        parcel.readString(),
        parcel.readString(),
        parcel.readString(),
        parcel.readString(),
        TODO("iP")
    ) {
    }

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeString(eventName)
        parcel.writeString(host)
        parcel.writeString(address)
        parcel.writeString(startTime)
        parcel.writeString(endTime)
        parcel.writeString(distance)
        parcel.writeString(dayOfWeek)
        parcel.writeString(dayOfMonth)
        parcel.writeString(dateFormatter)
        parcel.writeString(description)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<LocalItem> {
        override fun createFromParcel(parcel: Parcel): LocalItem {
            return LocalItem(parcel)
        }

        override fun newArray(size: Int): Array<LocalItem?> {
            return arrayOfNulls(size)
        }
    }
}
