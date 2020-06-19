package com.zzwl.bakeMedicine.weidget

import android.os.Build
import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v4.app.FragmentPagerAdapter
import android.support.v4.view.ViewPager
import android.view.WindowManager
import com.alibaba.android.arouter.facade.annotation.Autowired
import com.alibaba.android.arouter.facade.annotation.Route
import com.alibaba.android.arouter.launcher.ARouter
import com.g.base.ui.BaseActivity
import com.zzwl.bakeMedicine.R
import com.zzwl.bakeMedicine.databinding.ActivityImagePreviewBinding
import com.zzwl.bakeMedicine.router.RouterPage
import com.zzwl.bakeMedicine.ui.home.fragment.ViewPagerFragment
import org.greenrobot.eventbus.EventBus

/**
 * Created by G on 2017/10/18 0018.
 */
@Route(path = RouterPage.ImagePreviewActivity)
class ImagePreviewActivity : BaseActivity<ActivityImagePreviewBinding>() {
    override var isFullScreen: Boolean = true
    override var hasToolbar: Boolean = true
    override var isHideStatusBar: Boolean = true

    @JvmField
    @Autowired
    var image: String = ""

    @JvmField
    @Autowired
    var needRemove: Boolean = false

    @JvmField
    @Autowired
    var index: Int = 0

    @JvmField
    @Autowired
    var imageHost: String? = ""


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        ARouter.getInstance().inject(this)
        setContentView(R.layout.activity_image_preview)
        hideStatusBar()
        val imageList = image.split(",")
        val fragmentList = ArrayList<Fragment>()
        imageList.forEach { url ->
            fragmentList.add(ViewPagerFragment().apply {
                arguments = Bundle().apply {
                    val bannerUrl = if (imageHost?.isNotEmpty() == true) imageHost + url else url
                    putString("bannerUrl", bannerUrl)
                    putInt("ScaleType", 3)
                }
            })
        }


        contentView.data = this
        toolbar.title = "${index + 1}/${imageList.size}"
        toolbar.background = null
        toolbar.setNavigationIcon(R.drawable.ic_arrow_back_toolbar_24dp)
        toolbar.setNavigationOnClickListener { finish() }
        if (needRemove) {
            toolbar.inflateMenu(R.menu.action_remove)
            toolbar.setOnMenuItemClickListener {
                EventBus.getDefault().post(OnPhotoRemoveEvent(image))
                finish()
                true
            }
        }



        contentView.viewPager.adapter = object : FragmentPagerAdapter(supportFragmentManager) {
            override fun getItem(position: Int): Fragment {
                return fragmentList[position]
            }

            override fun getCount(): Int {
                return imageList.size
            }
        }
        contentView.viewPager.currentItem = index

        contentView.viewPager.addOnPageChangeListener(object : ViewPager.OnPageChangeListener {
            override fun onPageScrollStateChanged(state: Int) {
            }

            override fun onPageScrolled(position: Int, positionOffset: Float, positionOffsetPixels: Int) {

            }

            override fun onPageSelected(position: Int) {
                toolbar.title = "${position + 1}/${imageList.size}"
            }
        })
        showContentView()
    }

    private fun isStrangePhone(): Boolean {
        return (Build.DEVICE.equals("mx5", ignoreCase = true)
                || Build.DEVICE.equals("Redmi Note2", ignoreCase = true)
                || Build.DEVICE.equals("Z00A_1", ignoreCase = true)
                || Build.DEVICE.equals("hwH60-L02", ignoreCase = true)
                || Build.DEVICE.equals("hermes", ignoreCase = true)
                || Build.DEVICE.equals("V4", ignoreCase = true) && Build.MANUFACTURER.equals("Meitu", ignoreCase = true)
                || Build.DEVICE.equals("m1metal", ignoreCase = true) && Build.MANUFACTURER.equals("Meizu", ignoreCase = true))

    }

    private fun hideStatusBar() {
        if (!isStrangePhone()) {
            this.window.setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN)
        }

    }

}

class OnPhotoRemoveEvent(val url: String)