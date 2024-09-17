package com.example.qlhoadon.QuanLy

import android.content.Context
import android.os.Handler
import android.os.Looper
import android.util.AttributeSet
import android.view.LayoutInflater
import android.widget.FrameLayout
import androidx.viewpager.widget.ViewPager
import com.example.qlhoadon.LopProduct.Photo
import com.example.qlhoadon.LopAdapter.PhotoAdapter
import com.example.qlhoadon.R
import me.relex.circleindicator.CircleIndicator

class HomeView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : FrameLayout(context, attrs, defStyleAttr) {

    private lateinit var pager: ViewPager
    private lateinit var circleIndicator: CircleIndicator

    private val autoScrollHandler = Handler(Looper.getMainLooper())
    private val autoScrollRunnable = object : Runnable {
        override fun run() {
            val currentItem = pager.currentItem
            val nextItem = if (currentItem + 1 < pager.adapter?.count ?: 0) {
                currentItem + 1
            } else {
                0
            }
            pager.setCurrentItem(nextItem, true)
            autoScrollHandler.postDelayed(this, 2000) // 2000 milliseconds = 2 seconds
        }
    }

    init {
        LayoutInflater.from(context).inflate(R.layout.view_home, this, true)
        pager = findViewById(R.id.viewPager)
        circleIndicator = findViewById(R.id.circle_indicator)

        setupViewPager()
    }

    private fun setupViewPager() {
        val photoList = getListPhoto()
        val adapter = PhotoAdapter()
        adapter.setPhotoList(photoList)
        pager.adapter = adapter
        circleIndicator.setViewPager(pager)

        startAutoScroll()
    }

    private fun startAutoScroll() {
        autoScrollHandler.postDelayed(autoScrollRunnable, 3000)
    }

    private fun getListPhoto(): List<Photo> {
        return listOf(
            Photo(R.drawable.photo1),
            Photo(R.drawable.photo2),
            Photo(R.drawable.photo3)
        )
    }

    override fun onDetachedFromWindow() {
        super.onDetachedFromWindow()
        autoScrollHandler.removeCallbacks(autoScrollRunnable)
    }
}
