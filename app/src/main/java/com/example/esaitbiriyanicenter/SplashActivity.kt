package com.restaurant.esaitbiriyanicenter

import android.app.Activity
import android.app.AlertDialog
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import com.android.volley.DefaultRetryPolicy
import com.android.volley.Request
import com.android.volley.Response
import com.android.volley.RetryPolicy
import com.android.volley.toolbox.StringRequest
import com.android.volley.toolbox.Volley
import com.example.esaitbiriyanicenter.ShopClosedActivity
import com.restaurant.esaitbiriyanicenter.constants.EsaitConstants
import com.sucho.placepicker.AddressData
import com.sucho.placepicker.Constants
import com.sucho.placepicker.Constants.GOOGLE_API_KEY
import com.sucho.placepicker.MapType
import com.sucho.placepicker.PlacePicker
import kotlinx.android.synthetic.main.fragment_second.*
import org.json.JSONException
import org.json.JSONObject
import java.lang.Exception


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
            if(list.get(0).get("availability").equals("1")) {
                EsaitConstants.availabilityList = list;

                val builder = AlertDialog.Builder(this);
                builder.setMessage("We would like to know your location to serve you better, please select your location from the Map.");
                //performing positive action
                builder.setPositiveButton("Proceed"){dialogInterface, which ->
                    val intent = PlacePicker.IntentBuilder()
                        .setLatLong(12.9231058, 80.1266854)  // Initial Latitude and Longitude the Map will load into
                        .showLatLong(false)  // Show Coordinates in the Activity
                        .setMapZoom(12.0f)  // Map Zoom Level. Default: 14.0
                        .setAddressRequired(true) // Set If return only Coordinates if cannot fetch Address for the coordinates. Default: True
                        .hideMarkerShadow(true) // Hides the shadow under the map marker. Default: False
                        .setMarkerDrawable(R.drawable.halal) // Change the default Marker Image
                        .setMarkerImageImageColor(R.color.colorPrimary)
                        .setFabColor(R.color.colorAccent)
                        .setPrimaryTextColor(R.color.colorPrimaryDark) // Change text color of Shortened Address
                        .setSecondaryTextColor(R.color.colorPrimary) // Change text color of full Address
                        .setBottomViewColor(R.color.colorAccent) // Change Address View Background Color (Default: White)
                        .setMapRawResourceStyle(R.raw.map_style)  //Set Map Style (https://mapstyle.withgoogle.com/)
                        .setMapType(MapType.NORMAL)
                        .setPlaceSearchBar(true, GOOGLE_API_KEY) //Activate GooglePlace Search Bar. Default is false/not activated. SearchBar is a chargeable feature by Google
                        .onlyCoordinates(true)  //Get only Coordinates from Place Picker
                        .hideLocationButton(true)   //Hide Location Button (Default: false)
                        .disableMarkerAnimation(true)   //Disable Marker Animation (Default: false)
                        .build(this)
                    startActivityForResult(intent, Constants.PLACE_PICKER_REQUEST)

                }
                // Create the AlertDialog
                val alertDialog: AlertDialog = builder.create()
                // Set other dialog properties
                alertDialog.setCancelable(false)
                alertDialog.show()

            } else if(list.get(0).get("availability").equals("0")){
                val intent = Intent(this, ShopClosedActivity::class.java)
                startActivity(intent)
                finish()
            }
        } catch (e: JSONException) {
            e.printStackTrace()
        }

    }

    override fun onActivityResult(requestCode: Int,resultCode: Int,data: Intent?) {
        if (requestCode == Constants.PLACE_PICKER_REQUEST) {
            if (resultCode == Activity.RESULT_OK) {
                val addressData = data?.getParcelableExtra<AddressData>(Constants.ADDRESS_INTENT)
                if (addressData != null) {
                    EsaitConstants.latitude = addressData.latitude
                    EsaitConstants.longitude = addressData.longitude
                }
                val intent = Intent(this, MainActivity::class.java)
                startActivity(intent)
                finish()
            }
        } else {
            super.onActivityResult(requestCode, resultCode, data)
        }
    }
}


