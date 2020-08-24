package com.restaurant.esaitbiriyanicenter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import android.widget.Toast
import androidx.navigation.NavController
import com.intrusoft.sectionedrecyclerview.SectionRecyclerViewAdapter
import com.restaurant.esaitbiriyanicenter.constants.EsaitConstants

class AdapterSectionRecycler(context: Context, sectionItemList: MutableList<SectionHeader>,navController : NavController) : SectionRecyclerViewAdapter<SectionHeader, Child, SectionViewHolder, ChildViewHolder>(
    context, sectionItemList
)    {
    var context: Context? = context;
    var navController : NavController? = navController;
    override fun onCreateSectionViewHolder(p0: ViewGroup?, p1: Int): SectionViewHolder {
        val view: View =
            LayoutInflater.from(context).inflate(R.layout.sectionheader, p0, false)
        return SectionViewHolder(view)
    }

    override fun onBindSectionViewHolder(p0: SectionViewHolder?, p1: Int, p2: SectionHeader?) {
        if (p0 != null) {
            if (p2 != null) {
                p0.name?.setText(p2.getSectionText())
            };
        };
    }

    override fun onCreateChildViewHolder(p0: ViewGroup?, p1: Int): ChildViewHolder {
        val view: View =
            LayoutInflater.from(context).inflate(R.layout.section_child, p0, false)

        return ChildViewHolder(view)
    }

    override fun onBindChildViewHolder(p0: ChildViewHolder?, p1: Int, p2: Int, p3: Child?) {
        if(EsaitConstants.availabilityList.size > 0) {
            if (p1 == 0 && p2 >= 0) {
                var availability = EsaitConstants.availabilityList.get(p2 + 1).get("availability");
                if (availability.equals("0")) {
                    p0?.itemView?.findViewById<TextView>(R.id.availability_txt)?.visibility =
                        View.VISIBLE;
                    p0?.itemView?.findViewById<TextView>(R.id.plus)?.isEnabled = false;
                    p0?.itemView?.findViewById<TextView>(R.id.plus)
                        ?.setBackgroundColor(context?.resources?.getColor(R.color.textdisabled)!!);
                    p0?.itemView?.findViewById<TextView>(R.id.minus)
                        ?.setBackgroundColor(context?.resources?.getColor(R.color.textdisabled)!!);
                }
            }

            if (p1 == 1 && p2 == 0) {
                var availability = EsaitConstants.availabilityList.get(p2 + 5).get("availability");
                if (availability.equals("0")) {
                    p0?.itemView?.findViewById<TextView>(R.id.availability_txt)?.visibility =
                        View.VISIBLE;
                    p0?.itemView?.findViewById<TextView>(R.id.plus)?.isEnabled = false;
                    p0?.itemView?.findViewById<TextView>(R.id.plus)
                        ?.setBackgroundColor(context?.resources?.getColor(R.color.textdisabled)!!);
                    p0?.itemView?.findViewById<TextView>(R.id.minus)
                        ?.setBackgroundColor(context?.resources?.getColor(R.color.textdisabled)!!);
                }
            }
        }

        p0?.name?.setText(p3?.name);
        val name = p3?.name;
            var qtyValue = Integer.parseInt(p0?.itemView?.findViewById<TextView>(R.id.quantity)?.text.toString());
        p0?.itemView?.findViewById<TextView>(R.id.plus)?.setOnClickListener(View.OnClickListener {
            if(qtyValue<10) {
                qtyValue++;
                p0?.itemView?.findViewById<TextView>(R.id.quantity)?.text = qtyValue.toString();
            } else {
                Toast.makeText(context,"Maximum Quantity per item is 10.",Toast.LENGTH_SHORT).show();
            }
        });
        p0?.itemView?.findViewById<TextView>(R.id.minus)?.setOnClickListener(View.OnClickListener {
            if(qtyValue>0) {
                qtyValue--;
                p0?.itemView?.findViewById<TextView>(R.id.quantity)?.text = qtyValue.toString();
            }
        });
    }

}