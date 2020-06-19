package com.zzwl.bakeMedicine.ui

import android.os.Bundle
import com.g.base.ui.BaseActivity
import com.zzwl.bakeMedicine.R
import com.zzwl.bakeMedicine.databinding.LayoutSampleBinding

class SampleActivity : BaseActivity<LayoutSampleBinding>() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.layout_sample)
        initView()
    }

    private fun initView() {

    }
}