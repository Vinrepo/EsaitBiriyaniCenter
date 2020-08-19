package com.restaurant.esaitbiriyanicenter

import android.Manifest
import android.app.AlertDialog
import android.content.Context.LOCATION_SERVICE
import android.content.Context.MODE_PRIVATE
import android.content.pm.PackageManager
import android.location.Location
import android.location.LocationListener
import android.location.LocationManager
import android.os.Bundle
import android.os.Handler
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.os.bundleOf
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentTransaction
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.android.volley.DefaultRetryPolicy
import com.android.volley.Request
import com.android.volley.Response
import com.android.volley.RetryPolicy
import com.android.volley.toolbox.StringRequest
import com.android.volley.toolbox.Volley
import com.example.esaitbiriyanicenter.ImageSliderAdapter
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.restaurant.esaitbiriyanicenter.constants.EsaitConstants
import kotlinx.android.synthetic.main.fragment_first.*
import kotlinx.android.synthetic.main.section_child.*
import org.json.JSONException
import org.json.JSONObject
import java.lang.Math.cos
import java.util.*
import kotlin.collections.ArrayList
import kotlin.collections.HashMap
import javax.mail.*
import javax.mail.internet.InternetAddress
import javax.mail.internet.MimeMessage




/**
 * A simple [Fragment] subclass as the default destination in the navigation.
 */
class FirstFragment : Fragment() {
    var fusedLocationClient: FusedLocationProviderClient? = null
    val PERMISSION_ID = 42
    var deliveryCharges = 0;

    //calling the viewEmployee method of DatabaseHandler class to read the records


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_first, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState);
        toolbar.setTitle("Esait Biriyani center");
        checkFirstRun();
        //getItems()
        //imageslider fun call
        imageSliderImplementation();
        //toolbar.setLogo(R.drawable.plus);
        //Transport.send(plainMail())



        (activity as AppCompatActivity?)!!.setSupportActionBar(toolbar)
        //toolbar.setLogo(R.drawable.plus);
        toolbar.setOverflowIcon(ContextCompat.getDrawable(requireContext(),R.drawable.call));
        //(activity as AppCompatActivity?)!!.getSupportActionBar()?.setHomeButtonEnabled(true);
        //(activity as AppCompatActivity?)!!.getSupportActionBar()?.setDisplayShowHomeEnabled(true);
        //(activity as AppCompatActivity?)!!.getSupportActionBar()?.setDisplayUseLogoEnabled(true);
        //(activity as AppCompatActivity?)!!.getSupportActionBar()?.setLogo(R.drawable.plus);
        //(activity as AppCompatActivity?)!!.getSupportActionBar()?.setDisplayShowTitleEnabled(false);





        /*** Distance calculation ****/


        fusedLocationClient = LocationServices.getFusedLocationProviderClient(requireActivity())
        var distance = 0.0;
        var currentLocationLat = EsaitConstants.latitude;
        var currentLocationLong = EsaitConstants.longitude;
        val long1 = 80.1266854 / 57.29577951
        val lat1 = 12.9231058 / 57.29577951
        val long2 = currentLocationLong / 57.29577951
        val lat2 = currentLocationLat / 57.29577951

        val Distance = 3963.0 * StrictMath.acos(
            (StrictMath.sin(lat1) * StrictMath.sin(
                lat2
            )) +cos(lat1) * cos(lat2) * cos(long2 - long1)
        )
        val fin = Distance*1.60934
        distance = fin * 1.6;
        deliveryCharges = getDeliveryChargesBasedOnDistance(distance);
        var deliveryChargesText = textView2.text.toString();
        deliveryChargesText = String.format(deliveryChargesText,deliveryCharges);
        //deliveryChargesText = "Delivery charges "+deliveryCharges
        textView2.setText(deliveryChargesText);
        if(deliveryCharges >=50){
            distancefee.visibility = View.VISIBLE;
        }


        /*if (checkPermission(
                Manifest.permission.ACCESS_COARSE_LOCATION,
                Manifest.permission.ACCESS_FINE_LOCATION)) {
            fusedLocationClient?.lastLocation?.addOnSuccessListener(requireActivity(),{location : Location? ->
                // Got last known location. In some rare
                // situations this can be null.
                if(location == null) {
                    Toast.makeText(context,"please turn on loaction",Toast.LENGTH_SHORT).show()

                    // TODO, handle it
                } else location.apply {
                    // Handle location object
                    Log.e("LOG", location.toString())

                }
            })
        } else {

        }*/
        /*** Distance calculation ****/




        val childList1: MutableList<Child> = ArrayList()
        childList1.add(Child("Mutton Biriyani  ₹270/pack"));
        childList1.add(Child("Chicken Biriyani ₹180/pack"));
        childList1.add(Child("Khuska                  ₹120/pack"));
        childList1.add(Child("Chicken 65(4 pcs) ₹110"));

        val sections: MutableList<SectionHeader> = ArrayList();
        sections.add(SectionHeader(childList1, "Non Vegetarian"));

        val childList2: MutableList<Child> = ArrayList();
        childList2.add(Child("Veg Biriyani        ₹50/pack"));
        sections.add(SectionHeader(childList2, "Vegetarian"));

        recycler_view.adapter =
            context?.let { AdapterSectionRecycler(it, sections, this.findNavController()) };
        recycler_view.layoutManager = LinearLayoutManager(context);
        recycler_view.setHasFixedSize(true);

        info_icon.setOnClickListener(View.OnClickListener {
            val builder = AlertDialog.Builder(context);
            val factory = LayoutInflater.from(context)
            val view: View = factory.inflate(R.layout.special_packing_dialog, null)
            builder.setView(view)
            builder.setMessage(R.string.dialogMessageSpecialPacking);
            builder.setPositiveButton("Ok") { dialogInterface, which ->
                dialogInterface.dismiss();
            }
            builder.show()
        })

        proceed_button.setOnClickListener(View.OnClickListener {
            //Collection all the values from the recycler view where value is greater than zero
            //pass them Via bundle and receive them in the next fragment
            //We can make the calculations here itself and just pass the values

            /*var mbQuantity = Integer.parseInt(recycler_view?.getChildViewHolder(recycler_view.getChildAt(1))?.itemView?.findViewById<TextView>(R.id.quantity)?.text.toString());
            var cbQuantity = Integer.parseInt(recycler_view?.getChildViewHolder(recycler_view.getChildAt(2))?.itemView?.findViewById<TextView>(R.id.quantity)?.text.toString());
            var khuskaQuantity = Integer.parseInt(recycler_view?.getChildViewHolder(recycler_view.getChildAt(3))?.itemView?.findViewById<TextView>(R.id.quantity)?.text.toString());
            var chicken65Quantity = Integer.parseInt(recycler_view?.getChildViewHolder(recycler_view.getChildAt(4))?.itemView?.findViewById<TextView>(R.id.quantity)?.text.toString());*/
            //var vegBiriyaniQuantity = Integer.parseInt(recycler_view?.getChildViewHolder(recycler_view.getChildAt(5))?.itemView?.findViewById<TextView>(R.id.quantity)?.text.toString());
            var mbQuantity = Integer.parseInt(
                recycler_view.findViewHolderForAdapterPosition(1)?.itemView?.findViewById<TextView>(
                    R.id.quantity
                )?.text.toString()
            );
            var cbQuantity = Integer.parseInt(
                recycler_view.findViewHolderForAdapterPosition(2)?.itemView?.findViewById<TextView>(
                    R.id.quantity
                )?.text.toString()
            );
            var khuskaQuantity = Integer.parseInt(
                recycler_view.findViewHolderForAdapterPosition(3)?.itemView?.findViewById<TextView>(
                    R.id.quantity
                )?.text.toString()
            );
            var chicken65Quantity = Integer.parseInt(
                recycler_view.findViewHolderForAdapterPosition(4)?.itemView?.findViewById<TextView>(
                    R.id.quantity
                )?.text.toString()
            );
            var vegBiriyaniQuantity = Integer.parseInt(
                recycler_view.findViewHolderForAdapterPosition(6)?.itemView?.findViewById<TextView>(
                    R.id.quantity
                )?.text.toString()
            );

            var orderSummary = "";
            var grandTotal = 0;
            if (mbQuantity > 0) {
                orderSummary =
                    "Mutton Biriyani x " + mbQuantity + "                                          ₹" + (mbQuantity * 270) + "\n";
                grandTotal = mbQuantity * 270;
            }

            if (cbQuantity > 0) {
                orderSummary += "Chicken Biriyani x " + cbQuantity + "                                        ₹" + (cbQuantity * 180) + "\n";
                grandTotal += cbQuantity * 180;
            }

            if (khuskaQuantity > 0) {
                orderSummary += "Khuska x " + khuskaQuantity + "                                                        ₹" + (khuskaQuantity * 120) + "\n";
                grandTotal += khuskaQuantity * 120;
            }

            if (chicken65Quantity > 0) {
                orderSummary += "Chicken 65 x " + chicken65Quantity + "                                                 ₹" + (chicken65Quantity * 110) + "\n";
                grandTotal += chicken65Quantity * 110;
            }

            if (vegBiriyaniQuantity > 0) {
                orderSummary += "Veg Biriyani x " + vegBiriyaniQuantity + "                                                ₹" + (vegBiriyaniQuantity * 50) + "\n";
                grandTotal += vegBiriyaniQuantity * 50;
            }

            var specialPackingCharges = 0;
            if (checkBox.isChecked) {
                if (mbQuantity > 0) {
                    specialPackingCharges = mbQuantity * 20;
                }
                if (cbQuantity > 0) {
                    specialPackingCharges += cbQuantity * 20;
                }
                if (khuskaQuantity > 0) {
                    specialPackingCharges += khuskaQuantity * 20;
                }
                if (vegBiriyaniQuantity > 0) {
                    specialPackingCharges += vegBiriyaniQuantity * 20;
                }
            }

            if (grandTotal > 0) {
                if((grandTotal > 500) && distance <10){
                    deliveryCharges = 0;
                }
                if (specialPackingCharges > 0) {
                    grandTotal += specialPackingCharges;
                    orderSummary += "Special packing charges " + "                               ₹" + specialPackingCharges + "\n";
                }
                grandTotal = grandTotal + deliveryCharges;
                var discountedPrice = 0;
                if(grandTotal>1000) {
                    discountedPrice = grandTotal/10;
                    grandTotal = grandTotal - grandTotal/10;
                }

                var grandTotalStr =
                    "Grand Total " + "                                               ₹" + grandTotal;
                orderSummary += "Delivery Charges                                             ₹"+deliveryCharges+"\n";
                if(discountedPrice>0){
                    orderSummary += "10% Discount                                                  -₹"+discountedPrice;
                }
                val bundle =
                    bundleOf("orderSummary" to orderSummary, "grandTotal" to grandTotalStr);
                this.findNavController()
                    ?.navigate(R.id.action_FirstFragment_to_SecondFragment, bundle);
            } else {
                Toast.makeText(
                    context,
                    "Please select some items from the menu",
                    Toast.LENGTH_SHORT
                ).show();
            }
        })

    }

    /*************** USER LOCATION TRACKING *********************************/
   private fun checkPermission(vararg perm:String) : Boolean {
        val havePermissions = perm.toList().all {
            ContextCompat.checkSelfPermission(requireContext(),it) ==
                    PackageManager.PERMISSION_GRANTED
        }
        if (!havePermissions) {
            if(perm.toList().any {
                    ActivityCompat.
                    shouldShowRequestPermissionRationale(requireActivity(), it)}
            ) {
                val dialog = AlertDialog.Builder(requireContext())
                    .setTitle("Permission")
                    .setMessage("Permission needed!")
                    .setPositiveButton("OK", {id, v ->
                        ActivityCompat.requestPermissions(
                            requireActivity(), perm, PERMISSION_ID)
                    })
                    .setNegativeButton("No", {id, v -> })
                    .create()
                dialog.show()
            } else {
                ActivityCompat.requestPermissions(requireActivity(), perm, PERMISSION_ID)
            }
            return false
        }
        return true
    }

    override fun onRequestPermissionsResult(requestCode: Int,
                                            permissions: Array<String>, grantResults: IntArray) {
        when (requestCode) {
            PERMISSION_ID -> {
                //Handle some logic here
            }
        }
    }

    /*************** USER LOCATION TRACKING *********************************/

    /**************** Distance based Delivery charges calculation ********************************************/
    fun getDeliveryChargesBasedOnDistance(distance:Double) : Int {
        if(distance<3){
            return 30;
        } else if(distance>3 && distance<6) {
            return 50;
        } else if(distance>6 && distance<8){
            return 80;
        } else if(distance>8 && distance<10){
            return 100;
        } else if(distance>10 && distance<15){
            return 150;
        } else if(distance > 15) {
            return 150;
        } else{
            return 30;
        }
    }

    /**************** Distance based Delivery charges calculation ********************************************/



    /*************** Block of code will be used later for the menu items availability logic *********************************/
    private fun getItems() {
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
        val queue = Volley.newRequestQueue(context)
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
            if(list.get(1).get("availability").equals("0")) {


            } else if(list.get(2).get("availability").equals("0")){
                plus.isEnabled = false
                plus.isClickable = false
            }
            //Read shop value and redirect
            //if shop open, redirect to normal menu items screen
            //else - redirect to shop currently closed screen
        } catch (e: JSONException) {
            e.printStackTrace()
        }
    }
    private fun imageSliderImplementation() {
        var currentPage = 0
        val timer: Timer
        val DELAY_MS: Long = 500 //delay in milliseconds before task is to be executed
        val PERIOD_MS: Long =
            3000 // time in milliseconds between successive task executions.

        val adapter = ImageSliderAdapter(requireContext())
        viewpager?.adapter = adapter
        /*After setting the adapter use the timer */
        /*After setting the adapter use the timer */
        val handler = Handler()
        val Update = Runnable {
            if (currentPage === 4) {
                currentPage = 0
            }
            viewpager?.setCurrentItem(currentPage++, true)
        }

        timer = Timer() // This will create a new Thread

        timer.schedule(object : TimerTask() {
            // task to be scheduled
            override fun run() {
                handler.post(Update)
            }
        }, DELAY_MS, PERIOD_MS)

    }

    /*************** Block of code will be used later for the menu items availability logic *********************************/
     fun checkFirstRun(): Unit {
        val isFirstRun = context?.getSharedPreferences("PREFERENCE", MODE_PRIVATE)?.getBoolean("isFirstRun", true);
        if(isFirstRun!!) {
            context?.getSharedPreferences("PREFERENCE", MODE_PRIVATE)
                ?.edit()
                ?.putBoolean("isFirstRun", false)
                ?.apply()
            val builder = AlertDialog.Builder(context);
            val factory = LayoutInflater.from(context)
            val view: View = factory.inflate(R.layout.offer_popup_menu, null)
            builder.setView(view)
            builder.setMessage("");
            builder.setPositiveButton("Ok") { dialogInterface, which ->
                getFragmentManager()?.beginTransaction()?.detach(this)?.attach(this)?.commit();
                dialogInterface.dismiss();
            }
            builder.show()
        }
    }
  
}

