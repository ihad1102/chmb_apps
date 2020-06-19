package com.zzwl.bakeMedicine.ui.home.fragment


import android.databinding.DataBindingUtil
import android.databinding.ObservableField
import android.os.Bundle
import android.support.v4.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import com.zzwl.bakeMedicine.R
import com.zzwl.bakeMedicine.databinding.FragmentBannerBinding


/**
 * Created by qhn on 2017/12/29.
 */
class ViewPagerFragment : Fragment() {
     var picture: Any? = null
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val binding = DataBindingUtil.inflate<FragmentBannerBinding>(inflater, R.layout.fragment_banner, container, false)
        picture = when (arguments!!.getInt("picture",-1)) {
            0 -> R.drawable.ic_banner
            1 -> R.drawable.ic_banner1
            2 -> R.drawable.ic_banner2
            3 -> R.drawable.ic_banner3
            else -> arguments!!.getString("bannerUrl")
        }
        binding.data = this
        binding.imgPic.scaleType = when (arguments!!.getInt("ScaleType", 1)) {
            1 -> ImageView.ScaleType.FIT_XY
            3 -> ImageView.ScaleType.FIT_CENTER
            6 -> ImageView.ScaleType.CENTER_CROP
            else -> ImageView.ScaleType.FIT_XY
        }
        return binding.root
    }
}