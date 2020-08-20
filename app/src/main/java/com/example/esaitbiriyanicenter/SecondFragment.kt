package com.restaurant.esaitbiriyanicenter

import android.app.AlertDialog
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.example.esaitbiriyanicenter.DatabaseHandler
import com.example.esaitbiriyanicenter.EmpModelClass
import com.restaurant.esaitbiriyanicenter.constants.EsaitConstants
import kotlinx.android.synthetic.main.fragment_second.*
import kotlinx.android.synthetic.main.fragment_second.toolbar
import java.lang.Exception
/**
 * A simple [Fragment] subclass as the second destination in the navigation.
 */
class SecondFragment : Fragment() {

    var i = "100"


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

        //fun viewRecord(context: Context?){
        //creating the instance of DatabaseHandler class
        val databaseHandler: DatabaseHandler= DatabaseHandler(context)
        //calling the viewEmployee method of DatabaseHandler class to read the records
        val emp: List<EmpModelClass> = databaseHandler.viewEmployee()

        val empArrayId = Array<String>(emp.size){"0"}
        val empArrayName = Array<String>(emp.size){"null"}
        val empArrayEmail = Array<String>(emp.size){"null"}
        val empArrayphone = Array<String>(emp.size){"null"}
        val empArraylat = Array<String>(emp.size){"null"}
        val empArraylong = Array<String>(emp.size){"null"}
        var index = 0
        for(e in emp){
            empArrayId[index] = e.userId.toString()
            empArrayName[index] = e.userName
            empArrayEmail[index] = e.userEmail
            empArrayphone[index] = e.userphone
            empArraylat[index] = e.userlat
            empArraylong[index] = e.userlong
        }
        //}
        editTextAddress.setText(EsaitConstants.address)
        if(emp.isNotEmpty()){
            if(empArrayId[0] == "100") {
                editTextPhone.setText(empArrayphone[0]);
                //editTextAddress.setText(empArrayName[0]);
                editTextTextEmailName.setText(empArrayEmail[0]);
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
                    finalWhatsAppMessage+= "Map Link : https://maps.google.com/?q="+EsaitConstants.latitude+","+EsaitConstants.longitude+"\n";
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
        val lat = EsaitConstants.latitude.toString()
        val long = EsaitConstants.longitude.toString()
        val databaseHandler: DatabaseHandler = DatabaseHandler( context )

        if(id.trim()!="" && name.trim()!="" && email.trim()!=""&& lat.trim()!=""&& long.trim()!=""){
            val status = databaseHandler.addEmployee(EmpModelClass(Integer.parseInt(id),name,email,phone,lat,long))
            if(status > -1){
                var applicationContext = null
                Toast.makeText(context,"record save",Toast.LENGTH_LONG).show()

            }
        }

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
        val lat = EsaitConstants.latitude.toString()
        val long = EsaitConstants.longitude.toString()
        //creating the instance of DatabaseHandler class
        val databaseHandler: DatabaseHandler= DatabaseHandler(context)
        if(updateId.trim()!="" && updateName.trim()!="" && updateEmail.trim()!="" && updatephone.trim()!=""&& lat.trim()!=""&& long.trim()!=""){
            //calling the updateEmployee method of DatabaseHandler class to update record
            val status = databaseHandler.updateEmployee(EmpModelClass(Integer.parseInt(updateId),updateName, updateEmail, updatephone,lat,long))
        }


    }


}