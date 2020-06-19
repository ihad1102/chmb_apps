package com.im.ui.browserImage

import android.graphics.Bitmap
import android.os.Bundle
import android.support.v4.view.PagerAdapter
import android.util.LruCache
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import cn.jiguang.imui.commons.BitmapLoader
import com.g.base.ui.BaseActivity
import com.im.R
import com.im.databinding.ActivityImageBrowserBinding
import com.im.ui.browserImage.photoview.ImgBrowserViewPager
import com.im.ui.browserImage.photoview.PhotoView
import java.io.File
import java.util.*


class BrowserImageActivity : BaseActivity<ActivityImageBrowserBinding>() {
    override var isFullScreen: Boolean = true

    private var mViewPager: ImgBrowserViewPager? = null
    private var mPathList: List<String> = ArrayList()
    private var mMsgIdList: List<String> = ArrayList()
    private var mCache: LruCache<String, Bitmap>? = null
    private var mWidth: Int = 0
    private var mHeight: Int = 0

    internal var pagerAdapter: PagerAdapter = object : PagerAdapter() {
        override fun getCount(): Int {
            return mPathList.size
        }

        override fun isViewFromObject(view: View, `object`: Any): Boolean {
            return view === `object`
        }

        override fun instantiateItem(container: ViewGroup, position: Int): Any {
            val photoView = PhotoView(true, this@BrowserImageActivity)
            photoView.scaleType = ImageView.ScaleType.CENTER_CROP
            photoView.tag = position
            val path = mPathList[position]
            if (path != null) {
                var bitmap: Bitmap? = mCache!!.get(path)
                if (bitmap != null) {
                    photoView.setImageBitmap(bitmap)
                } else {
                    val file = File(path)
                    if (file.exists()) {
                        bitmap = BitmapLoader.getBitmapFromFile(path, mWidth, mHeight)
                        if (bitmap != null) {
                            photoView.setImageBitmap(bitmap)
                            mCache!!.put(path, bitmap)
                        } else {
                            photoView.setImageResource(R.drawable.aurora_picture_not_found)
                        }
                    } else {
                        photoView.setImageResource(R.drawable.aurora_picture_not_found)
                    }
                }
            } else {
                photoView.setImageResource(R.drawable.aurora_picture_not_found)
            }
            container.addView(photoView, ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT)
            return photoView
        }

        override fun destroyItem(container: ViewGroup, position: Int, `object`: Any) {
            container.removeView(`object` as View)
        }

        override fun getItemPosition(`object`: Any): Int {
            val view = `object` as View
            val currentPage = mViewPager!!.currentItem
            return if (currentPage == view.tag as Int) {
                PagerAdapter.POSITION_NONE
            } else {
                PagerAdapter.POSITION_UNCHANGED
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_image_browser)
        mPathList = intent.getStringArrayListExtra("pathList")
        mMsgIdList = intent.getStringArrayListExtra("idList")
        mViewPager = findViewById(R.id.img_browser_viewpager)
        val dm = resources.displayMetrics
        mWidth = dm.widthPixels
        mHeight = dm.heightPixels

        val maxMemory = Runtime.getRuntime().maxMemory().toInt()
        val cacheSize = maxMemory / 4
        mCache = LruCache(cacheSize)
        mViewPager!!.adapter = pagerAdapter
        initCurrentItem()
        showContentView()
    }

    private fun initCurrentItem() {
        val photoView = PhotoView(true, this)
        val msgId = intent.getStringExtra("msgId")
        val position = mMsgIdList.indexOf(msgId)
        val path = mPathList[position]
        if (path != null) {
            var bitmap: Bitmap? = mCache!!.get(path)
            if (bitmap != null) {
                photoView.setImageBitmap(bitmap)
            } else {
                val file = File(path)
                if (file.exists()) {
                    bitmap = BitmapLoader.getBitmapFromFile(path, mWidth, mHeight)
                    if (bitmap != null) {
                        photoView.setImageBitmap(bitmap)
                        mCache!!.put(path, bitmap)
                    } else {
                        photoView.setImageResource(R.drawable.aurora_picture_not_found)
                    }
                } else {
                    photoView.setImageResource(R.drawable.aurora_picture_not_found)
                }
            }
        } else {
            photoView.setImageResource(R.drawable.aurora_picture_not_found)
        }
        mViewPager!!.currentItem = position
    }


}
