package com.restaurant.esaitbiriyanicenter
import android.Manifest
import android.annotation.SuppressLint
import android.app.Activity
import android.location.Geocoder
import android.location.LocationManager
import androidx.appcompat.app.AppCompatActivity
import android.provider.ContactsContract
import android.provider.Settings
import com.google.android.gms.location.*
import kotlinx.android.synthetic.main.activity_main.*
import java.security.Permission
import java.security.Provider
import java.util.*
import android.app.AlertDialog
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.location.Location
import android.net.Uri
import android.os.Bundle
import android.os.Looper
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat.getSystemService
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.example.esaitbiriyanicenter.DatabaseHandler
import com.example.esaitbiriyanicenter.EmpModelClass
import com.google.android.gms.location.LocationCallback
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.location.LocationResult
import com.google.android.gms.location.LocationServices
import kotlinx.android.synthetic.main.fragment_first.*
import kotlinx.android.synthetic.main.fragment_second.*
import kotlinx.android.synthetic.main.fragment_second.toolbar
import java.lang.Exception
import java.lang.StrictMath.*
import kotlin.math.cos

/**
 * A simple [Fragment] subclass as the second destination in the navigation.
 */
class SecondFragment : Fragment() {

    var i = "100"
    lateinit var fusedLocationProviderClient: FusedLocationProviderClient
    lateinit var locationRequest: LocationRequest
    val PERMISSION_ID = 1010
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {


        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_second, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState);
        super.onCreate(savedInstanceState)
        var c = 0
        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient( requireContext())
        Log.d("Debug:",CheckPermission().toString())
        Log.d("Debug:",isLocationEnabled().toString())
        RequestPermission()
        getLastLocation()
        //fun viewRecord(context: Context?){
        //creating the instance of DatabaseHandler class
        val databaseHandler: DatabaseHandler= DatabaseHandler(context)
        //calling the viewEmployee method of DatabaseHandler class to read the records
        val emp: List<EmpModelClass> = databaseHandler.viewEmployee()

        val empArrayId = Array<String>(emp.size){"0"}
        val empArrayName = Array<String>(emp.size){"null"}
        val empArrayEmail = Array<String>(emp.size){"null"}
        val empArrayphone = Array<String>(emp.size){"null"}
        var index = 0
        for(e in emp){
            empArrayId[index] = e.userId.toString()
            empArrayName[index] = e.userName
            empArrayEmail[index] = e.userEmail
            empArrayphone[index] = e.userphone
        }
        //}
        if(emp.isNotEmpty()){
            if(empArrayId[0] == "100") {
                editTextPhone.setText(empArrayphone[0]);
                //editTextAddress.setText(empArrayName[0]);
                //editTextTextEmailName.setText(empArrayEmail[0]);
                c = 1
            }

        }
        //check from the database if recode exist.
        //if record exist, fetch the value from the db and show

        toolbar.setNavigationIcon(R.drawable.ic_baseline_arrow_back_ios_24);
        toolbar.setNavigationOnClickListener(View.OnClickListener {
            this.findNavController()
                ?.navigate(R.id.action_SecondFragment_to_FirstFragment, null);
        })
        toolbar.setTitle("Your Order");
        val orderSummary = arguments?.getString("orderSummary");
        val grandTotal = arguments?.getString("grandTotal");
        summary_details.text = orderSummary;
        grand_total.text = grandTotal;

        submitbutton.setOnClickListener(View.OnClickListener {
            //checkbox
            if(checkBox4.isChecked){
                //save detail user detail pto sqlite database
                if(c == 0){
                    saveRecord(context);
                }
                else{
                    updateRecord(context)
                }


            }
            if(editTextTextEmailName.text.toString().equals("") || editTextAddress.text.toString().equals("") || editTextPhone.text.toString().equals("")) {
                Toast.makeText(context,"All fields are mandatory",Toast.LENGTH_SHORT).show();
            } else {
                val builder = AlertDialog.Builder(context);
                builder.setMessage(R.string.dialogMessage);
                //performing positive action
                builder.setPositiveButton("Proceed"){dialogInterface, which ->
                    var finalWhatsAppMessage = "Order number : "+System.currentTimeMillis()+"\n\n";
                    finalWhatsAppMessage+= orderSummary + "\n\n";
                    finalWhatsAppMessage+= grandTotal + "\n\n";
                    finalWhatsAppMessage+= "For Mr/Mrs : "+editTextTextEmailName.text + "\n";
                    finalWhatsAppMessage+= "Staying @ "+editTextAddress.text + "\n";
                    finalWhatsAppMessage+= "Phone : "+editTextPhone.text + "\n";
                    if(!editTextComments.text.equals("")){
                        finalWhatsAppMessage+= "Comments : "+editTextComments.text + "\n";
                        val toNumber = "917299158123";
                        try {

                            val intent = Intent(Intent.ACTION_VIEW)
                            intent.setData(Uri.parse("http://api.whatsapp.com/send?phone="+toNumber +"&text="+finalWhatsAppMessage));
                            startActivity(intent)
                        } catch (e:Exception){
                            e.printStackTrace();
                        }

                    }
                }

                //performing negative action
                builder.setNegativeButton("Cancel"){dialogInterface, which ->
                    dialogInterface.dismiss();
                }
                // Create the AlertDialog
                val alertDialog: AlertDialog = builder.create()
                // Set other dialog properties
                alertDialog.setCancelable(false)
                alertDialog.show()

            }
        })
    }
    fun saveRecord(context: Context?){
        val id = i.toString()
        val name = editTextAddress.text.toString()
        val email = editTextTextEmailName.text.toString()
        val phone = editTextPhone.text.toString()
        val databaseHandler: DatabaseHandler = DatabaseHandler( context )
        if(id.trim()!="" && name.trim()!="" && email.trim()!=""){
            val status = databaseHandler.addEmployee(EmpModelClass(Integer.parseInt(id),name, email,phone))
            if(status > -1){
                var applicationContext = null
                Toast.makeText(context,"record save",Toast.LENGTH_LONG).show()

            }
        }

    }
    fun CheckPermission():Boolean{
        //this function will return a boolean
        //true: if we have permission
        //false if not
        if(
            ActivityCompat.checkSelfPermission(requireContext(),android.Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED ||
            ActivityCompat.checkSelfPermission(requireContext(),android.Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED
        ){
            return true
        }

        return false

    }
    fun RequestPermission(){
        //this function will allows us to tell the user to requesut the necessary permsiion if they are not garented
        ActivityCompat.requestPermissions(
            requireActivity(),
            arrayOf(android.Manifest.permission.ACCESS_COARSE_LOCATION,android.Manifest.permission.ACCESS_FINE_LOCATION),
            PERMISSION_ID
        )
    }
    fun isLocationEnabled():Boolean{
        //this function will return to us the state of the location service
        //if the gps or the network provider is enabled then it will return true otherwise it will return false

        var locationManager = context?.getSystemService(Context.LOCATION_SERVICE) as LocationManager
        return locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER) || locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER)
    }
    fun getLastLocation(){
        if(CheckPermission()){
            if(isLocationEnabled()){
                if (ActivityCompat.checkSelfPermission(
                        requireContext(),
                        Manifest.permission.ACCESS_FINE_LOCATION
                    ) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(
                        requireContext(),
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
                fusedLocationProviderClient.lastLocation.addOnCompleteListener { task->
                    var location: Location? = task.result
                    if(location == null){
                        Toast.makeText(context,"Please new",Toast.LENGTH_SHORT).show()
                        NewLocationData()
                    }else{
                        Toast.makeText(context,"ruunned",Toast.LENGTH_SHORT).show()
                        val long1 = 80.1180644 / 57.29577951
                        val lat1 = 12.9233093 / 57.29577951
                        val long2 = location.longitude / 57.29577951
                        val lat2 = location.latitude / 57.29577951

                        val Distance = 3963.0 * acos((sin(lat1) * sin(lat2)) + cos(lat1) * cos(lat2) * cos(long2-long1))
                        val fin = Distance*1.60934

                        //Log.d("Debug:" ,"Your Location:"+ location.longitude)
                        //Toast.makeText(context,"lon"+location.longitude,Toast.LENGTH_SHORT).show()
                        //editTextTextEmailName.text = "You Current Location is : Long: "+ location.longitude + " , Lat: " + location.latitude + "\n"
                        editTextTextEmailName.setText(fin.toString());
                        editTextPhone.setText(getCityName(location.latitude,location.longitude));
                    }
                }
            }else{
                Toast.makeText(context,"Please Turn on Your device Location",Toast.LENGTH_SHORT).show()
            }
        }else{
            RequestPermission()
        }
    }
    fun NewLocationData(){
        var locationRequest =  LocationRequest()
        locationRequest.priority = LocationRequest.PRIORITY_HIGH_ACCURACY
        locationRequest.interval = 0
        locationRequest.fastestInterval = 0
        locationRequest.numUpdates = 1
        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(requireContext())
        if (ActivityCompat.checkSelfPermission(
                requireContext(),
                Manifest.permission.ACCESS_FINE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(
                requireContext(),
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
        fusedLocationProviderClient!!.requestLocationUpdates(
            locationRequest,locationCallback, Looper.myLooper()
        )

    }


    private val locationCallback = object : LocationCallback(){
        override fun onLocationResult(locationResult: LocationResult) {
            var lastLocation: Location = locationResult.lastLocation
            Log.d("Debug:","your last last location: "+ lastLocation.longitude.toString())
            //textView.text = "You Last Location is : Long: "+ lastLocation.longitude + " , Lat: " + lastLocation.latitude + "\n" + getCityName(lastLocation.latitude,lastLocation.longitude)
        }
    }
    private fun getCityName(lat: Double,long: Double):String{
        var cityName:String = ""
        var countryName = ""
        var geoCoder = Geocoder(requireContext(), Locale.getDefault())
        var Adress = geoCoder.getFromLocation(lat,long,3)

        cityName = Adress.get(0).locality
        countryName = Adress.get(0).countryName
        Log.d("Debug:","Your City: " + cityName + " ; your Country " + countryName)
        return cityName
    }

    fun updateRecord(context: Context?){
        //val dialogBuilder = AlertDialog.Builder(this)
        //val inflater = this.layoutInflater
        //val dialogView = inflater.inflate(R.layout.update_dialog, null)
        /*dialogBuilder.setView(dialogView)

        val edtId = dialogView.findViewById(R.id.editTextPhone) as EditText
        val edtName = dialogView.findViewById(R.id.editTextAddress) as EditText
        val edtEmail = dialogView.findViewById(R.id.editTextTextEmailName) as EditText

        dialogBuilder.setTitle("Update Record")
        dialogBuilder.setMessage("Enter data below")
        dialogBuilder.setPositiveButton("Update", DialogInterface.OnClickListener { _, _ ->*/

        val updateId = i.toString()
        val updateName = editTextAddress.text.toString()
        val updateEmail = editTextTextEmailName.text.toString()
        val updatephone = editTextPhone.text.toString()
        //creating the instance of DatabaseHandler class
        val databaseHandler: DatabaseHandler= DatabaseHandler(context)
        if(updateId.trim()!="" && updateName.trim()!="" && updateEmail.trim()!="" && updatephone.trim()!=""){
            //calling the updateEmployee method of DatabaseHandler class to update record
            val status = databaseHandler.updateEmployee(EmpModelClass(Integer.parseInt(updateId),updateName, updateEmail, updatephone))
        }


    }


}