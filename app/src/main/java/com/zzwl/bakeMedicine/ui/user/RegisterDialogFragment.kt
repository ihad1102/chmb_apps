package com.zzwl.bakeMedicine.ui.user

import android.databinding.DataBindingUtil
import android.os.Bundle
import android.support.v4.app.DialogFragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import com.ogaclejapan.smarttablayout.utils.v4.FragmentPagerItem
import com.ogaclejapan.smarttablayout.utils.v4.FragmentPagerItemAdapter
import com.ogaclejapan.smarttablayout.utils.v4.FragmentPagerItems
import com.zzwl.bakeMedicine.R
import com.zzwl.bakeMedicine.databinding.FragmentRegisterDialogBinding
import com.zzwl.bakeMedicine.event.NextEvent
import com.g.base.utils.getScreenHeight
import org.greenrobot.eventbus.EventBus
import org.greenrobot.eventbus.Subscribe
import org.greenrobot.eventbus.ThreadMode


class RegisterDialogFragment : DialogFragment() {
    lateinit var inflateDataBinding: FragmentRegisterDialogBinding
    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        EventBus.getDefault().register(this)
        val attributes = dialog.window!!.attributes
        attributes.width = WindowManager.LayoutParams.WRAP_CONTENT
        attributes.height = (getScreenHeight(context!!) * 0.75).toInt()
        dialog.window!!.setBackgroundDrawableResource(R.drawable.shape_tobacco_white_round_solid_6)
        dialog.window!!.attributes = attributes
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        inflateDataBinding = DataBindingUtil.inflate(LayoutInflater.from(context), R.layout.fragment_register_dialog, container, false)
        initView()
        return inflateDataBinding.root
    }


    private fun initView() {
        val pages = FragmentPagerItems(context)
        pages.add(FragmentPagerItem.of("page", RegisterFragment::class.java))
        pages.add(FragmentPagerItem.of("page2", Register2Fragment::class.java))
        val adapter = FragmentPagerItemAdapter(childFragmentManager, pages)
        inflateDataBinding.viewPager.adapter = adapter
        inflateDataBinding.viewpagertab.setViewPager(inflateDataBinding.viewPager)
    }

    override fun onDestroy() {
        if (EventBus.getDefault().isRegistered(this))
            EventBus.getDefault().unregister(this)
        super.onDestroy()
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    fun onEvent(nextEvent: NextEvent) {
        if (nextEvent.isRefresh) {
            inflateDataBinding.viewPager.currentItem = 1
        }
    }
}