package com.example.partyapp

import android.os.Parcel
import android.os.Parcelable

data class EventModel(
    var pushId: String?= null,
    var host: String?= null,
    var lat: Double?= null,
    var long: Double?= null,
    var address: String?= null,
    var start: String?= null,
    var end: String?= null,
    var name: String?= null,
    var desc: String?= null,
    var imgPaths: MutableList<String>?= null,
    var tags: MutableList<String>?= null,
    var sanitizedTags: MutableList<String>?= null
): Parcelable {
    constructor(parcel: Parcel) : this(
        parcel.readString(),
        parcel.readString(),
        parcel.readDouble(),
        parcel.readDouble(),
        parcel.readString(),
        parcel.readString(),
        parcel.readString(),
        parcel.readString(),
        parcel.readString(),
        parcel.createStringArrayList(),
        parcel.createStringArrayList(),
        parcel.createStringArrayList()
    ) {
    }

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeString(pushId)
        parcel.writeString(host)
        parcel.writeDouble(lat!!)
        parcel.writeDouble(long!!)
        parcel.writeString(address)
        parcel.writeString(start)
        parcel.writeString(end)
        parcel.writeString(name)
        parcel.writeString(desc)
        parcel.writeList(imgPaths)
        parcel.writeList(tags)
        parcel.writeList(sanitizedTags)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<EventModel> {
        override fun createFromParcel(parcel: Parcel): EventModel {
            return EventModel(parcel)
        }

        override fun newArray(size: Int): Array<EventModel?> {
            return arrayOfNulls(size)
        }
    }
}
