package com.example.iaplibrary

import com.example.iaplibrary.model.IapModel

interface SubscribeInterface {
    fun subscribeSuccess(productModel: IapModel)
    fun subscribeError(error: String)
}
