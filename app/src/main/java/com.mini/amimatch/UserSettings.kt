package com.mini.amimatch
import com.google.firebase.firestore.auth.User

class UserSettings {
    private var user: User? = null

    constructor(user: User?) {
        this.user = user
    }

    constructor() {}

    fun getUser(): User? {
        return user
    }

    fun setUser(user: User?) {
        this.user = user
    }

    override fun toString(): String {
        return "UserSettings{" +
                "user=" + user +
                '}'
    }
}
