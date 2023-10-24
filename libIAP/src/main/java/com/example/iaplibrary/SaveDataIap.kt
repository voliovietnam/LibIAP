package com.example.iaplibrary

import android.os.Parcelable
import com.example.iaplibrary.model.IapModel
import com.tencent.mmkv.MMKV
import kotlinx.android.parcel.Parcelize

@Parcelize
data class SaveDataIap(
    val data: MutableList<IapModel>
) : Parcelable {
    companion object {
        val KEY_SAVE = "Iap_Save"
        fun saveDataIapModel(data: MutableList<IapModel>) {
            MMKV.defaultMMKV().encode(KEY_SAVE, SaveDataIap(data = data))
        }

        fun getDataIapModel(): List<IapModel> {
            MMKV.defaultMMKV().decodeParcelable(KEY_SAVE, SaveDataIap::class.java)?.let {
                return it.data
            }
            return emptyList()
        }
    }
}
