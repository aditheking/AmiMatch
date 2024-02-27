package com.mini.amimatch
data class Profile(
    val name: String = "",
    val age: Int = 0,
    val gender: String = "",
    val bio: String = "",
    val interests: List<String> = listOf(),
    val hobbies: List<String> = listOf(),
    val profilePictureUri: String = ""
)
