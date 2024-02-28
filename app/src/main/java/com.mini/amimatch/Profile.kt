package com.mini.amimatch
import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class Profile(
    val name: String = "",
    val age: Int = 0,
    val gender: String = "",
    val bio: String = "",
    val hobbies: List<String> = listOf(),
    val interests: List<String> = listOf(),
    val profilePictureUri: String = ""
) : Parcelable
