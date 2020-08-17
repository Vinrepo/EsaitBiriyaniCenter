package com.example.esaitbiriyanicenter

import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import com.restaurant.esaitbiriyanicenter.R
import kotlinx.android.synthetic.main.activity_main2.*

class Sorryoutofdistance : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.sorry)
        close_button.setOnClickListener(View.OnClickListener {
            finish()
        })
    }
}
