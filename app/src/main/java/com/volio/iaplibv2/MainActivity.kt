package com.volio.iaplibv2

import android.annotation.SuppressLint
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.widget.TextView
import com.example.iaplibrary.IapConnector
import kotlinx.coroutines.delay

class MainActivity : AppCompatActivity() {
    @SuppressLint("MissingInflatedId")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        IapConnector.initIap(application,"iap_id.json",true)
        IapConnector.getAllProductModel()

        findViewById<TextView>(R.id.sale).setOnClickListener {
            IapConnector.buyIap(activity = this,"test_weekly",isPrioritizeTrial = false)
        }
        findViewById<TextView>(R.id.trial).setOnClickListener {
            IapConnector.buyIap(activity = this,"test_weekly", isPrioritizeTrial = true)
        }
    }
}