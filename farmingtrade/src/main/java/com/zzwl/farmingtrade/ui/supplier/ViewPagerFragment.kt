package com.zzwl.farmingtrade.ui.supplier


import android.databinding.DataBindingUtil
import android.databinding.ObservableField
import android.os.Bundle
import android.support.v4.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import com.zzwl.farmingtrade.R
import com.zzwl.farmingtrade.databinding.FarmingFragmentBannerBinding


/**
 * Created by qhn on 2017/12/29.
 */
class ViewPagerFragment : Fragment() {
    val obsImgUrl by lazy { ObservableField<String>() }
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val binding = DataBindingUtil.inflate<FarmingFragmentBannerBinding>(inflater, R.layout.farming_fragment_banner, container, false)
        obsImgUrl.set(arguments!!.getString("bannerUrl"))

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