package com.restaurant.esaitbiriyanicenter

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.widget.SimpleAdapter
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.android.volley.DefaultRetryPolicy
import com.android.volley.Request
import com.android.volley.Response
import com.android.volley.RetryPolicy
import com.android.volley.toolbox.StringRequest
import com.android.volley.toolbox.Volley
import org.json.JSONException
import org.json.JSONObject


class SplashActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.splash_screen)
        getItems();
    }

    private fun getItems() {
        //loading = ProgressDialog.show(this, "Loading", "please wait", false, true)
        val stringRequest = StringRequest(
            Request.Method.GET,
            "https://script.google.com/macros/s/AKfycbwbdLqmiFybgjp58eyPwRYrdP8Fkatr5f8rynjVFeZi1HiblLUS/exec?action=getItems",
            Response.Listener { response -> parseItems(response) },
            Response.ErrorListener { }
        )
        val socketTimeOut = 50000
        val policy: RetryPolicy =
            DefaultRetryPolicy(socketTimeOut, 0, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT)
        stringRequest.retryPolicy = policy
        val queue = Volley.newRequestQueue(this)
        queue.add(stringRequest)
    }

    private fun parseItems(jsonResposnce: String) {
        val list: ArrayList<HashMap<String, String?>> = ArrayList()
        try {
            val jobj = JSONObject(jsonResposnce)
            val jarray = jobj.getJSONArray("items")
            for (i in 0 until jarray.length()) {
                val jo = jarray.getJSONObject(i)
                val itemName = jo.getString("name")
                val brand = jo.getString("availability")
                val item: HashMap<String, String?> = HashMap()
                item["name"] = itemName
                item["availability"] = brand
                list.add(item)
            }
            if(list.get(0).get("availability").equals("open")) {
                val intent = Intent(this, MainActivity::class.java)
                startActivity(intent)
                finish()
            } else if(list.get(0).get("availability").equals("closed")){
                Toast.makeText(applicationContext,"Shop Closed!",Toast.LENGTH_SHORT).show();
            }
        } catch (e: JSONException) {
            e.printStackTrace()
        }

    }
}


