package com.restaurant.esaitbiriyanicenter

import android.app.AlertDialog
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import kotlinx.android.synthetic.main.fragment_second.*
import java.lang.Exception

/**
 * A simple [Fragment] subclass as the second destination in the navigation.
 */
class SecondFragment : Fragment() {


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_second, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState);
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
}