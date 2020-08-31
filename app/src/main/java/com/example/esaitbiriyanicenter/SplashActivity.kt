package com.restaurant.esaitbiriyanicenter

import android.Manifest
import android.app.Activity
import android.app.AlertDialog
import android.content.Context
import android.content.Intent
import android.content.IntentSender
import android.content.pm.PackageManager
import android.location.Location
import android.location.LocationManager
import android.net.Uri
import android.os.Bundle
import android.os.Looper
import android.util.Log
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
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
import com.sucho.placepicker.*
import com.sucho.placepicker.Constants.GOOGLE_API_KEY
import kotlinx.android.synthetic.main.fragment_second.*
import org.json.JSONException
import org.json.JSONObject
import android.provider.Settings;
import android.view.View
import com.google.android.gms.common.api.ResolvableApiException
import com.google.android.gms.location.*
import kotlinx.android.synthetic.main.splash_screen.*
import kotlinx.coroutines.delay
import java.lang.Exception
import kotlinx.coroutines.*


class SplashActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.splash_screen)
        createLocationRequest();
        getItems();
    }
    var fusedLocationClient: FusedLocationProviderClient? = null
    val PERMISSION_ID = 42
    var deliveryCharges = 0;
    private lateinit var context: Context
    var intent1: Intent? = null
    lateinit var textView: TextView
    private lateinit var locationManager: LocationManager
    var gpsStatus = true
    var lat = 0.0
    var long = 0.0
    var f = 0

    private lateinit var locationRequest: LocationRequest
    private var locationUpdateState = false
    var l = false

    companion object {
        private const val MY_LOCATION_REQUEST_CODE = 329
        private const val NEW_REMINDER_REQUEST_CODE = 330
        private const val EXTRA_LAT_LNG = "EXTRA_LAT_LNG"
        private const val LOCATION_PERMISSION_REQUEST_CODE = 1
        private const val REQUEST_CHECK_SETTINGS = 2
        private const val PLACE_PICKER_REQUEST = 3

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

            for(e in emp){
                empArraylat[index] = e.userlat
                empArraylong[index] = e.userlong
            }
            /***current location cheking    ******/
            if(empArraylat.size>0 && empArraylong.size >0) {
                placePickerCall(empArraylat[0].toDouble(), empArraylong[0].toDouble());
            }
             else {
                fusedLocationClient = LocationServices.getFusedLocationProviderClient(this)
                if (checkPermission(
                        Manifest.permission.ACCESS_COARSE_LOCATION,
                        Manifest.permission.ACCESS_FINE_LOCATION
                    )
                ) {

                    fusedLocationClient?.lastLocation?.addOnCompleteListener({ task ->
                        var location: Location? = task.result
                        if (location == null) {
                            NewLocationData();
                        } else location.apply {
                            // Handle location object
                            Log.e("LOG", location.toString())
                            lat = location.latitude
                            long = location.longitude
                            placePickerCall(lat, long);
                            //EsaitConstants.address = location.latitude.toString();
                        }
                    })
                }
            }
            /***current location cheking    ******/
            if(list.get(0).get("availability").equals("0")){
                val intent = Intent(this, ShopClosedActivity::class.java)
                startActivity(intent)
                finish()
            }

        } catch (e: JSONException) {
            e.printStackTrace()
        }

    }


    fun NewLocationData(){
        var locationRequest =  LocationRequest()
        locationRequest.priority = LocationRequest.PRIORITY_HIGH_ACCURACY
        locationRequest.interval = 0
        locationRequest.fastestInterval = 0
        locationRequest.numUpdates = 1
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this)
        if (ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_COARSE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return
        }
        fusedLocationClient!!.requestLocationUpdates(
            locationRequest,locationCallback, Looper.myLooper()
        )
    }

    private val locationCallback = object : LocationCallback(){
        override fun onLocationResult(locationResult: LocationResult) {
            var lastLocation: Location = locationResult.lastLocation
            placePickerCall(lastLocation.latitude ,lastLocation.longitude);
            //textView.text = "You Last Location is : Long: "+ lastLocation.longitude + " , Lat: " + lastLocation.latitude + "\n";
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
        progressBar.visibility = View.INVISIBLE
        val intent = PlacePicker.IntentBuilder()
            .setLatLong(lat, long)  // Initial Latitude and Longitude the Map will load into
            .showLatLong(false)  // Show Coordinates in the Activity
            .setMapZoom(15.0f)  // Map Zoom Level. Default: 14.0
            .setAddressRequired(true) // Set If return only Coordinates if cannot fetch Address for the coordinates. Default: True
            .hideMarkerShadow(true) // Hides the shadow under the map marker. Default: False
            .setMarkerDrawable(R.drawable.map) // Change the default Marker Image
            //.setMarkerImageImageColor(R.color.dark)
            .setFabColor(R.color.dark)
            .setPrimaryTextColor(R.color.black) // Change text color of Shortened Address
            .setSecondaryTextColor(R.color.light) // Change text color of full Address
            .setBottomViewColor(R.color.white) // Change Address View Background Color (Default: White)
            //.setMapRawResourceStyle(R.raw.map_style)  //Set Map Style (https://mapstyle.withgoogle.com/)
            .setMapType(MapType.NORMAL)
            .setPlaceSearchBar(false, GOOGLE_API_KEY) //Activate GooglePlace Search Bar. Default is false/not activated. SearchBar is a chargeable feature by Google
            .onlyCoordinates(false)  //Get only Coordinates from Place Picker
            .hideLocationButton(true)   //Hide Location Button (Default: false)
            .disableMarkerAnimation(false)   //Disable Marker Animation (Default: false)
            .build(this)
        startActivityForResult(intent, Constants.PLACE_PICKER_REQUEST)
    }

    private fun checkPermission(vararg perm:String) : Boolean {
        val havePermissions = perm.toList().all {
            ContextCompat.checkSelfPermission(this,it) ==
                    PackageManager.PERMISSION_GRANTED
        }
        if (!havePermissions) {
            if(perm.toList().any {
                    ActivityCompat.
                    shouldShowRequestPermissionRationale(this, it)}
            ) {
                val dialog = AlertDialog.Builder(this)
                    .setTitle("Permission")
                    .setMessage("Permission needed!")
                    .setPositiveButton("OK", {id, v ->
                        ActivityCompat.requestPermissions(
                            this, perm, PERMISSION_ID)
                    })
                    .setNegativeButton("No", {id, v -> })
                    .create()
                dialog.show()
            } else {
                ActivityCompat.requestPermissions(this, perm, PERMISSION_ID)
            }
            return false
        }
        return true
    }

    override fun onRequestPermissionsResult(requestCode: Int,
                                            permissions: Array<String>, grantResults: IntArray) {
        if(requestCode == PERMISSION_ID){
            if(grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED){
                progressBar.visibility = View.VISIBLE
                NewLocationData();
            }
            if(grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_DENIED){
                val dialog = AlertDialog.Builder(this)
                dialog.setTitle("Location permission")
                    .setMessage("Witout location permission we will not be able to serve you better!")
                    .setPositiveButton("OK") { paramDialogInterface, paramInt ->
                        finish();
                    }

                dialog.show()
            }
        }
    }

    private fun checkGPSEnabled(): Boolean {
        if (!isLocationEnabled())
            showAlert()
        return isLocationEnabled()

    }
    private fun isLocationEnabled(): Boolean {
        var locationManager = getSystemService(Context.LOCATION_SERVICE) as LocationManager
        return locationManager!!.isProviderEnabled(LocationManager.GPS_PROVIDER) || locationManager!!.isProviderEnabled(LocationManager.NETWORK_PROVIDER)
    }
    private fun showAlert() {
        val dialog = AlertDialog.Builder(this)
        dialog.setTitle("Enable Location")
            .setMessage("Locations Settings is set to 'Off'.\nEnable Location to use this app")
            .setPositiveButton("Location Settings") { paramDialogInterface, paramInt ->
                val myIntent = Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS)
                startActivity(myIntent)
                finish()
            }
            .setNegativeButton("Cancel") { paramDialogInterface, paramInt -> }
        dialog.show()
    }

    fun createLocationRequest() {
        locationRequest = LocationRequest()
        locationRequest.interval = 1000
        //locationRequest.fastestInterval = 1000
        locationRequest.priority = LocationRequest.PRIORITY_HIGH_ACCURACY

        val builder = LocationSettingsRequest.Builder()
            .addLocationRequest(locationRequest)
        val client = LocationServices.getSettingsClient(this)
        val task = client.checkLocationSettings(builder.build())

        task.addOnSuccessListener {
            locationUpdateState = true
        }
        task.addOnFailureListener { e ->
            if (e is ResolvableApiException) {
                // Location settings are not satisfied, but this can be fixed
                // by showing the user a dialog.
                try {
                    // Show the dialog by calling startResolutionForResult(),
                    // and check the result in onActivityResult().
                    e.startResolutionForResult(this,
                        REQUEST_CHECK_SETTINGS)
                } catch (sendEx: IntentSender.SendIntentException) {
                    // Ignore the error.
                }
            }
        }
    }

}


