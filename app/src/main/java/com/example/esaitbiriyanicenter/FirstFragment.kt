package com.restaurant.esaitbiriyanicenter

import android.app.AlertDialog
import android.content.DialogInterface
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.os.bundleOf
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.android.volley.DefaultRetryPolicy
import com.android.volley.Request
import com.android.volley.Response
import com.android.volley.RetryPolicy
import com.android.volley.toolbox.StringRequest
import com.android.volley.toolbox.Volley
import kotlinx.android.synthetic.main.fragment_first.*
import org.json.JSONException
import org.json.JSONObject


/**
 * A simple [Fragment] subclass as the default destination in the navigation.
 */
class FirstFragment : Fragment() {

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
        (activity as AppCompatActivity?)!!.setSupportActionBar(toolbar)

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
                var specialPacking = "";
                if (specialPackingCharges > 0) {
                    grandTotal += specialPackingCharges;
                    orderSummary += "Special packing charges " + "                               ₹" + specialPackingCharges + "\n";
                }
                var grandTotalStr =
                    "Grand Total " + "                                               ₹" + (grandTotal + 30);
                orderSummary += "Delivery Charges                                             ₹30";
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
            //Read shop value and redirect
            //if shop open, redirect to normal menu items screen
            //else - redirect to shop currently closed screen
        } catch (e: JSONException) {
            e.printStackTrace()
        }
    }
}