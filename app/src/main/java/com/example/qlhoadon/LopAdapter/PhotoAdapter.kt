package com.example.qlhoadon.LopAdapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.viewpager.widget.PagerAdapter
import com.example.qlhoadon.R
import com.example.qlhoadon.LopProduct.Photo

class PhotoAdapter : PagerAdapter() {

    private var photoList: List<Photo> = listOf()

    override fun getCount(): Int {
        return photoList.size
    }

    override fun instantiateItem(container: ViewGroup, position: Int): Any {
        val inflater = LayoutInflater.from(container.context)
        val view = inflater.inflate(R.layout.item_photo, container, false)
        val imageView = view.findViewById<ImageView>(R.id.img_photo)
        imageView.setImageResource(photoList[position].getResourceId())
        container.addView(view)
        return view
    }

    override fun destroyItem(container: ViewGroup, position: Int, `object`: Any) {
        container.removeView(`object` as View)
    }

    override fun isViewFromObject(view: View, `object`: Any): Boolean {
        return view === `object`
    }

    fun setPhotoList(photoList: List<Photo>) {
        this.photoList = photoList
    }
}
