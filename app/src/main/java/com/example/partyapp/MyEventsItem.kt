package com.example.partyapp

import android.os.Parcel
import android.os.Parcelable

data class MyItem(
    val eventName: String?,
    val host: String?,
    val address: String?,
    val time: String?,
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
        TODO("iP")
    ) {
    }

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeString(eventName)
        parcel.writeString(host)
        parcel.writeString(address)
        parcel.writeString(time)
        parcel.writeString(distance)
        parcel.writeString(dayOfWeek)
        parcel.writeString(dayOfMonth)
        parcel.writeString(dateFormatter)
        parcel.writeString(description)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<MyItem> {
        override fun createFromParcel(parcel: Parcel): MyItem {
            return MyItem(parcel)
        }

        override fun newArray(size: Int): Array<MyItem?> {
            return arrayOfNulls(size)
        }
    }
}
