package com.mini.amimatch

import java.util.Calendar


class CalculateAge(dob: String) {
    var age = 0
        private set

    init {
        val splitDOB = dob.split("-".toRegex()).dropLastWhile { it.isEmpty() }.toTypedArray()
        setAge(splitDOB[2].toInt(), splitDOB[0].toInt(), splitDOB[1].toInt())
    }

    fun setAge(year: Int, month: Int, day: Int) {
        val dateOfBirth = Calendar.getInstance()
        val today = Calendar.getInstance()
        dateOfBirth[year, month] = day
        var age = today[Calendar.YEAR] - dateOfBirth[Calendar.YEAR]
        if (today[Calendar.DAY_OF_YEAR] < dateOfBirth[Calendar.DAY_OF_YEAR]) {
            age--
        }
        this.age = age
    }
}
