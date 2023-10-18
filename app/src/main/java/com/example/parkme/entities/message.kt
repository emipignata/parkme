package com.example.parkme.entities

import android.os.Parcel
import android.os.Parcelable

class Message(sender: String, receiver: String, timestamp: String, message: String) :
    Parcelable {
    var sender: String
    var receiver: String
    var timestamp: String
    var message: String

    constructor() : this("","","", "")


    init {
        this.sender = sender!!
        this.receiver = receiver!!
        this.timestamp = timestamp!!
        this.message = message!!
    }

    constructor(source: Parcel) : this(
        source.readString()!!,
        source.readString()!!,
        source.readString()!!,
        source.readString()!!
    )

    override fun describeContents() = 0

    override fun writeToParcel(dest: Parcel, flags: Int) = with(dest) {
        writeString(sender)
        writeString(receiver)
        writeString(timestamp)
        writeString(message)
    }

    override fun toString(): String {
        return "Message(sender='$sender', receiver='$receiver', timestamp='$timestamp', message='$message')"
    }

    companion object {
        @JvmField
        val CREATOR: Parcelable.Creator<Message> = object : Parcelable.Creator<Message> {
            override fun createFromParcel(source: Parcel): Message = Message(source)
            override fun newArray(size: Int): Array<Message?> = arrayOfNulls(size)
        }
    }
}