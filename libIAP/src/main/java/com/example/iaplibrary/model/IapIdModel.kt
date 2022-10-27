package com.example.iaplibrary.model

import android.content.Context
import com.example.iaplibrary.utils.Utils
import com.google.gson.Gson
import com.google.gson.annotations.SerializedName

data class IapIdModel(
    @SerializedName("id")
    var idProduct: String = "null",

    @SerializedName("type")
    var type: String = "null"

) {
    companion object {
        fun getDataInput(context: Context, nameFile: String): List<IapIdModel> {
            val listIap = mutableListOf<IapIdModel>()

            try {
                val data = Utils.getStringAssetFile(nameFile, context)
                val ads = Gson().fromJson<Array<IapIdModel>>(data, Array<IapIdModel>::class.java)

                listIap.addAll(ads)
            } catch (e: Exception) {
            }
            return listIap
        }
    }
}