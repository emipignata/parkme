package com.example.parkme.entities

import android.os.Parcel
import android.os.Parcelable

data class DailyAvailability(
    val dayOfWeek: String = "",
    var timeRanges: MutableList<TimeRange> = mutableListOf()
) : Parcelable {
    constructor(parcel: Parcel) : this(
        parcel.readString() ?: "",
        parcel.createTypedArrayList(TimeRange.CREATOR) ?: mutableListOf()
    )

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeString(dayOfWeek)
        parcel.writeTypedList(timeRanges)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<DailyAvailability> {
        override fun createFromParcel(parcel: Parcel): DailyAvailability {
            return DailyAvailability(parcel)
        }

        override fun newArray(size: Int): Array<DailyAvailability?> {
            return arrayOfNulls(size)
        }
    }
}
