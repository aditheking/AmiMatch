package com.mini.amimatch

import android.os.Parcel
import android.os.Parcelable

class Cards() : Parcelable {
    var userId: String? = null
    var name: String? = null
    var profileImageUrl: String? = null
    val profilePhotoUrl: String? = null
    var bio: String? = null
    var interest: String? = null
    var age: Int = 0
    var distance: Int = 0
    var phoneNumber: String? = null
    var sports: Boolean = false
    var fishing: Boolean = false
    var music: Boolean = false
    var travel: Boolean = false
    var preferSex: String = ""
    var about: String? = null
    var year_semester: String? = null
    var course: String? = null
    var school: String? = null
    var userMusic: Boolean = false
    var userSports: Boolean = false
    var dateOfBirth: String? = null
    var userFishing: Boolean = false
    var userTravel: Boolean = false
    val isVerified: Boolean = false


    constructor(parcel: Parcel, userId: String?) : this(parcel) {
        this.userId = userId
    }

    constructor(parcel: Parcel) : this() {
        userId = parcel.readString()
        name = parcel.readString()
        profileImageUrl = parcel.readString()
        bio = parcel.readString()
        interest = parcel.readString()
        age = parcel.readInt()
        distance = parcel.readInt()
        phoneNumber = parcel.readString()
        sports = parcel.readByte() != 0.toByte()
        fishing = parcel.readByte() != 0.toByte()
        music = parcel.readByte() != 0.toByte()
        travel = parcel.readByte() != 0.toByte()
        preferSex = parcel.readString() ?: ""
    }

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeString(userId)
        parcel.writeString(name)
        parcel.writeString(profileImageUrl)
        parcel.writeString(bio)
        parcel.writeString(interest)
        parcel.writeInt(age)
        parcel.writeInt(distance)
        parcel.writeString(phoneNumber)
        parcel.writeByte(if (sports) 1 else 0)
        parcel.writeByte(if (fishing) 1 else 0)
        parcel.writeByte(if (music) 1 else 0)
        parcel.writeByte(if (travel) 1 else 0)
        parcel.writeString(preferSex)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<Cards> {
        override fun createFromParcel(parcel: Parcel): Cards {
            return Cards(parcel)
        }

        override fun newArray(size: Int): Array<Cards?> {
            return arrayOfNulls(size)
        }
    }
}
