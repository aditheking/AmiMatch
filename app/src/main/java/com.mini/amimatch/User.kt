package com.mini.amimatch

import android.os.Parcel
import android.os.Parcelable

class Users(
    var userId: String?,
    var name: String?,
    var profileImageUrl: String?,
    var bio: String?,
    var interest: String?,
    var age: Int,
    var distance: Int,
    var phoneNumber: String?,
    var sports: Boolean,
    var fishing: Boolean,
    var music: Boolean,
    var travel: Boolean,
    var preferSex: String = "",
    var dateOfBirth: String? = null

) : Parcelable {

    constructor(parcel: Parcel) : this(
        parcel.readString(),
        parcel.readString(),
        parcel.readString(),
        parcel.readString(),
        parcel.readString(),
        parcel.readInt(),
        parcel.readInt(),
        parcel.readString(),
        parcel.readByte() != 0.toByte(),
        parcel.readByte() != 0.toByte(),
        parcel.readByte() != 0.toByte(),
        parcel.readByte() != 0.toByte(),
        parcel.readString() ?: "",
        parcel.readString()

    )

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
        parcel.writeString(dateOfBirth)

    }

    override fun describeContents(): Int {
        return 0
    }

    // Setter method for age
    @JvmName("setUserAge")
    fun setAge(age: Int) {
        this.age = age
    }

    @JvmName("setUserProfileImageUrl")
    fun setProfileImageUrl(profileImageUrl: String?) {
        this.profileImageUrl = profileImageUrl
    }

    @JvmName("setUserSex")
    fun setSex(sex: String) {
        preferSex = sex
    }

    @JvmName("getUserSports")
    fun isSports(): Boolean {
        return sports
    }

    @JvmName("setUserSports")
    fun setSports(sports: Boolean) {
        this.sports = sports
    }

    @JvmName("getUserFishing")
    fun isFishing(): Boolean {
        return fishing
    }

    @JvmName("setUserFishing")
    fun setFishing(fishing: Boolean) {
        this.fishing = fishing
    }

    @JvmName("getUserMusic")
    fun isMusic(): Boolean {
        return music
    }

    @JvmName("setUserMusic")
    fun setMusic(music: Boolean) {
        this.music = music
    }

    @JvmName("getUserTravel")
    fun isTravel(): Boolean {
        return travel
    }

    @JvmName("setUserTravel")
    fun setTravel(travel: Boolean) {
        this.travel = travel
    }

    @JvmName("setUserDateOfBirth")
    fun setDateOfBirth(date: String) {
        this.dateOfBirth = date
    }

    companion object CREATOR : Parcelable.Creator<Users> {
        override fun createFromParcel(parcel: Parcel): Users {
            return Users(parcel)
        }

        override fun newArray(size: Int): Array<Users?> {
            return arrayOfNulls(size)
        }
    }
}
