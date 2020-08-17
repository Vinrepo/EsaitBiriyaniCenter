package com.example.esaitbiriyanicenter
import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.viewpager.widget.PagerAdapter
import androidx.viewpager.widget.ViewPager
import com.restaurant.esaitbiriyanicenter.FirstFragment
import com.restaurant.esaitbiriyanicenter.R
import kotlinx.android.synthetic.main.image_slider_item.view.*

class ImageSliderAdapter(context: Context) : PagerAdapter() {

    var context: Context? = context;
    private var inflater: LayoutInflater? = null
    //private val images = arrayOf(R.drawable.biriyani_1, R.drawable.biriyani_2, R.drawable.biriyani_3, R.drawable.chicken_65)
    private val images = arrayOf(R.drawable.ic_launcher,R.drawable.halal)

    override fun isViewFromObject(view: View, `object`: Any): Boolean {

        return view === `object`
    }

    override fun getCount(): Int {

        return images.size
    }

    override fun instantiateItem(container: ViewGroup, position: Int): Any {

        inflater = context?.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
        val view = inflater!!.inflate(R.layout.image_slider_item, null)
        view.imageView_slide.setImageResource(images[position])

        val vp = container as ViewPager
        vp.addView(view, 0)
        return view
    }

    override fun destroyItem(container: ViewGroup, position: Int, `object`: Any) {

        val vp = container as ViewPager
        val view = `object` as View
        vp.removeView(view)
    }

}