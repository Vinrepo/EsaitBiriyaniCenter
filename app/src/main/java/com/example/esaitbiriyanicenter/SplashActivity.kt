package com.restaurant.esaitbiriyanicenter

import android.app.Activity
import android.app.AlertDialog
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import com.android.volley.DefaultRetryPolicy
import com.android.volley.Request
import com.android.volley.Response
import com.android.volley.RetryPolicy
import com.android.volley.toolbox.StringRequest
import com.android.volley.toolbox.Volley
import com.example.esaitbiriyanicenter.DatabaseHandler
import com.example.esaitbiriyanicenter.EmpModelClass
import com.example.esaitbiriyanicenter.LatlongClass
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
            val databaseHandler: DatabaseHandler = DatabaseHandler(this)
            //calling the viewEmployee method of DatabaseHandler class to read the records
            val emp: List<LatlongClass> = databaseHandler.viewLatlong()
            val empArraylat = Array<String>(emp.size){"null"}
            val empArraylong = Array<String>(emp.size){"null"}
            var index = 0
            var lat = 0.0
            var long = 0.0
            for(e in emp){
                empArraylat[index] = e.userlat
                empArraylong[index] = e.userlong
            }
            if(list.get(0).get("availability").equals("1")) {
                EsaitConstants.availabilityList = list;
                if(emp.isNotEmpty()){
                    lat = empArraylat[0].toDouble()
                    long = empArraylong[0].toDouble()
                    placePickerCall(lat,long);
                }
                else{
                    lat = 12.9231058
                    long = 80.1266854
                    val builder = AlertDialog.Builder(this);
                    builder.setMessage("We would like to know your location to serve you better, please select your location from the Map.");
                    //performing positive action
                    builder.setPositiveButton("Proceed"){dialogInterface, which ->
                        placePickerCall(lat,long);
                    }
                    // Create the AlertDialog
                    val alertDialog: AlertDialog = builder.create()
                    // Set other dialog properties
                    alertDialog.setCancelable(false)
                    alertDialog.show()
                }



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
                    /*****latitude and longitude database handler*****/
                    val databaseHandler: DatabaseHandler= DatabaseHandler(this)

                    //calling the viewEmployee method of DatabaseHandler class to read the records
                    EsaitConstants.latitude = addressData.latitude
                    EsaitConstants.longitude = addressData.longitude
                    EsaitConstants.address = addressData?.addressList?.get(0)?.getAddressLine(0).toString()
                    val emp: List<LatlongClass> = databaseHandler.viewLatlong()
                    val empArraylat = Array<String>(emp.size){"null"}
                    val empArraylong = Array<String>(emp.size){"null"}

                    var index = 0
                    for(e in emp){
                        empArraylat[index] = e.userlat
                        empArraylong[index] = e.userlong
                    }
                    if (emp.isNotEmpty()){
                        updateRecord(this)

                    }
                    else{

                        saveRecord(this)
                    }
                    /*****latitude and longitude database handler*****/
                }
                val intent = Intent(this, MainActivity::class.java)
                startActivity(intent)
                finish()
            }
        } else {
            super.onActivityResult(requestCode, resultCode, data)
        }
    }
    fun saveRecord(context: Context?){
        val lat = EsaitConstants.latitude.toString()
        val long = EsaitConstants.longitude.toString()
        val databaseHandler: DatabaseHandler = DatabaseHandler( context )

        if(lat.trim()!=""&& long.trim()!=""){
            val status = databaseHandler.addLatlong(LatlongClass(lat,long))
            if(status > -1){

            }
        }

    }
    fun updateRecord(context: Context?){
        val lat = EsaitConstants.latitude.toString()
        val long = EsaitConstants.longitude.toString()
        //creating the instance of DatabaseHandler class
        val databaseHandler: DatabaseHandler= DatabaseHandler(context)
        if(lat.trim()!=""&& long.trim()!=""){
            //calling the updateEmployee method of DatabaseHandler class to update record
            val status = databaseHandler.updatelatlong(LatlongClass(lat,long))
        }
    }

    fun placePickerCall(lat : Double, long : Double) {
        val intent = PlacePicker.IntentBuilder()
            .setLatLong(lat, long)  // Initial Latitude and Longitude the Map will load into
            .showLatLong(false)  // Show Coordinates in the Activity
            .setMapZoom(12.0f)  // Map Zoom Level. Default: 14.0
            .setAddressRequired(true) // Set If return only Coordinates if cannot fetch Address for the coordinates. Default: True
            .hideMarkerShadow(true) // Hides the shadow under the map marker. Default: False
            .setMarkerDrawable(R.drawable.map) // Change the default Marker Image
            //.setMarkerImageImageColor(R.color.dark)
            .setFabColor(R.color.dark)
            .setPrimaryTextColor(R.color.black) // Change text color of Shortened Address
            .setSecondaryTextColor(R.color.light) // Change text color of full Address
            .setBottomViewColor(R.color.white) // Change Address View Background Color (Default: White)
            .setMapRawResourceStyle(R.raw.map_style)  //Set Map Style (https://mapstyle.withgoogle.com/)
            .setMapType(MapType.NORMAL)
            .setPlaceSearchBar(false, GOOGLE_API_KEY) //Activate GooglePlace Search Bar. Default is false/not activated. SearchBar is a chargeable feature by Google
            .onlyCoordinates(false)  //Get only Coordinates from Place Picker
            .hideLocationButton(true)   //Hide Location Button (Default: false)
            .disableMarkerAnimation(false)   //Disable Marker Animation (Default: false)
            .build(this)
        startActivityForResult(intent, Constants.PLACE_PICKER_REQUEST)
    }

}


