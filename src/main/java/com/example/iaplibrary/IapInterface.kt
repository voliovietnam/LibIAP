package com.example.iaplibrary

import com.example.iaplibrary.model.IapIdModel

interface IapInterface {
     suspend fun getInformation(listID: List<IapIdModel>)
     fun checkSubscribeIap()
     suspend fun getPriceSubscribeIap(listID: List<IapIdModel>)
     fun unSubscribeIap()
}