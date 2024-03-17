package com.mini.amimatch

class Cards(
    var userId: String?,
    var name: String?,
    var age: Int,
    var profileImageUrl: String?,
    var bio: String?,
    var interest: String?,
    var distance: Int
) {
    constructor(profileImageUrl: String?) : this(null, null, 0, profileImageUrl, null, null, 0)
}