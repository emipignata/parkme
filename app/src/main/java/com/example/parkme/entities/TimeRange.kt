package com.example.parkme.entities

import android.os.Parcel
import android.os.Parcelable

data class TimeRange(
    val startSeconds: Long,
    val endSeconds: Long
) : Parcelable {
    constructor(parcel: Parcel) : this(
        parcel.readLong(),
        parcel.readLong()
    )

    constructor() : this(
        0, 0
    )

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeLong(startSeconds)
        parcel.writeLong(endSeconds)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<TimeRange> {
        override fun createFromParcel(parcel: Parcel): TimeRange {
            return TimeRange(parcel)
        }

        override fun newArray(size: Int): Array<TimeRange?> {
            return arrayOfNulls(size)
        }
    }
}
