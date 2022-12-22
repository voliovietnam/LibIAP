package com.example.iaplibrary.model

import java.util.*

enum class Expiry {
    P1W, P1M, P3M, P6M, P1Y, PFV
}

fun String.convertExpiry(): Expiry {
    return when (this) {
        "P1W" -> {
            Expiry.P1W
        }
        "P1M" -> {
            Expiry.P1M
        }
        "P3M" -> {
            Expiry.P3M
        }
        "P6M" -> {
            Expiry.P6M
        }
        "P1Y" -> {
            Expiry.P1Y
        }
        else -> {
            Expiry.PFV
        }
    }
}

fun Long.convertExpiry(expiry: Expiry): Long {
    val calendar = Calendar.getInstance()
    calendar.timeInMillis = this
    return when (expiry) {
        Expiry.P1W -> {
            var checkTime = true
            while (checkTime) {
                calendar.add(Calendar.HOUR, 7 * 24)
                if (System.currentTimeMillis() < calendar.timeInMillis) {
                    checkTime = false
                }
            }
            calendar.timeInMillis
        }
        Expiry.P1M -> {
            var checkTime = true
            while (checkTime) {
                calendar.add(Calendar.MONTH, 1)
                if (System.currentTimeMillis() < calendar.timeInMillis) {
                    checkTime = false
                }
            }
            calendar.timeInMillis
        }
        Expiry.P3M -> {

            var checkTime = true
            while (checkTime) {
                calendar.add(Calendar.MONTH, 3)
                if (System.currentTimeMillis() < calendar.timeInMillis) {
                    checkTime = false
                }
            }
            calendar.timeInMillis
        }
        Expiry.P6M -> {
            var checkTime = true
            while (checkTime) {
                calendar.add(Calendar.MONTH, 6)
                if (System.currentTimeMillis() < calendar.timeInMillis) {
                    checkTime = false
                }
            }
            calendar.timeInMillis
        }
        Expiry.P1Y -> {

            var checkTime = true
            while (checkTime) {
                calendar.add(Calendar.YEAR, 1)
                if (System.currentTimeMillis() < calendar.timeInMillis) {
                    checkTime = false
                }
            }
            calendar.timeInMillis
        }
        else -> {
            0
        }
    }
}

