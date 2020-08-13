package com.example.esaitbiriyanicenter

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.content.Intent
import android.net.Uri
import android.view.Menu
import android.view.MenuItem
import android.view.View
import com.restaurant.esaitbiriyanicenter.R
import kotlinx.android.synthetic.main.activity_main2.*


class ShopClosedActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.shop_closed)
        close_button.setOnClickListener(View.OnClickListener {
            finish()
        })
    }
}
