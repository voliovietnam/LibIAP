package com.example.iaplibrary.utils

import android.content.Context
import android.util.DisplayMetrics
import java.io.InputStream

object Utils {

    fun getStringAssetFile(path: String, activity: Context): String? {
        var json: String? = null
        try {
            val inputStream: InputStream = activity.assets.open(path)
            json = inputStream.bufferedReader().use { it.readText() }
        } catch (ex: Exception) {
            ex.printStackTrace()
            return ""
        }
        return json
    }

}