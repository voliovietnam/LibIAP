package com.example.iaplibrary

import com.example.iaplibrary.model.ProductModel

interface SubscribeInterface {
    fun subscribeSuccess(productModel: ProductModel)
    fun subscribeError(error: String)
}
