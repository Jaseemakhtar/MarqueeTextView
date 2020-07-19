package com.jsync.marqueetextview

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        topMarquee.text = getString(R.string.default_marquee_text)
        topMarquee.start()
        bottomMarquee.start()
    }
}
