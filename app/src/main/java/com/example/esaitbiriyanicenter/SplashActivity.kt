package com.restaurant.esaitbiriyanicenter

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import com.example.esaitbiriyanicenter.Availability
import com.restaurant.esaitbiriyanicenter.api.APIClient
import com.restaurant.esaitbiriyanicenter.api.ApiInterface
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response


class SplashActivity : AppCompatActivity() {
    var handler: Handler? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.splash_screen)
        val call = APIClient.client.create(ApiInterface::class.java).getBooks()
        call.enqueue(object : Callback<Availability> {
            override fun onResponse(call: Call<Availability>, response: Response<Availability>) {
                Log.d("Success!", response.toString())
                var text = response.body()
                var bookList = text?.values
            }

            override fun onFailure(call: Call<Availability>, t: Throwable)                  {
                Log.e("Failed Query :(", t.toString())

            }
        })
    }
        /*handler = Handler()
        handler!!.postDelayed(Runnable {
            val intent = Intent(this,MainActivity::class.java)
            startActivity(intent)
            finish()
        }, 2000)*/
    }


