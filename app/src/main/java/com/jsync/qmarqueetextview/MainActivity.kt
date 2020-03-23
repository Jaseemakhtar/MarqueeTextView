package com.jsync.qmarqueetextview

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        topMarquee.text = "Mr. Jaseem akhtar"
        topMarquee.start()
        bottomMarquee.start()
    }
}
