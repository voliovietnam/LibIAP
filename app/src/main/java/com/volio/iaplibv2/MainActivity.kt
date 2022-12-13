package com.volio.iaplibv2

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.example.iaplibrary.IapConnector

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        IapConnector.getAllProductModel()
    }
}