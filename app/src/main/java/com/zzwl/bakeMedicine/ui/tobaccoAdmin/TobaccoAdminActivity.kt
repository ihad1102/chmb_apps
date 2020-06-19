package com.zzwl.bakeMedicine.ui.tobaccoAdmin

import android.arch.lifecycle.ViewModelProviders
import android.content.Context
import android.os.Bundle
import com.alibaba.android.arouter.facade.annotation.Route
import com.alibaba.android.vlayout.LayoutHelper
import com.alibaba.android.vlayout.layout.LinearLayoutHelper
import com.g.base.extend.dp
import com.g.base.extend.observeExOnce
import com.g.base.extend.resultNotNull
import com.g.base.room.repository.Status
import com.g.base.router.RouteExtras
import com.g.base.ui.BaseActivity
import com.g.base.ui.recyclerView.setupRecyclerView
import com.zzwl.bakeMedicine.R
import com.zzwl.bakeMedicine.databinding.ViewRecyclerviewBinding
import com.zzwl.bakeMedicine.router.RouterPage
import com.zzwl.bakeMedicine.ui.tobaccoAdmin.holder.TobaccoAdminContentHolder
import com.zzwl.bakeMedicine.ui.tobaccoAdmin.holder.TobaccoAdminTitleHolder
import com.zzwl.bakeMedicine.viewModel.AdminInfoViewModel

@Route(path = RouterPage.TobaccoAdminActivity, extras = RouteExtras.NeedOauth)
class TobaccoAdminActivity : BaseActivity<ViewRecyclerviewBinding>() {
    override var hasToolbar: Boolean = true
    private val adapter by lazy { setupRecyclerView(contentView.recyclerView) }
    private val adminInfoViewModel by lazy { ViewModelProviders.of(this).get(AdminInfoViewModel::class.java) }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.view_recyclerview)
        initView()
        onReload()
    }

    private fun initView() {
        toolbar.title = "烤房信息"
        toolbar.setNavigationOnClickListener {
            finish()
        }
        toolbar.setNavigationIcon(R.drawable.ic_arrow_back_toolbar_24dp)
    }

    override fun onReload() {
        adminInfoViewModel.getAdminInfo(getSharedPreferences("groupId", Context.MODE_PRIVATE).getInt("groupId", 0))
                .resultNotNull()
                .observeExOnce(this, {
                    when (it.status) {
                        Status.Loading -> {
                            showLoading()
                        }
                        Status.Content -> {
                            adminInfoViewModel.adminInfoEntity = it.data!!
                            it.data!!.roleList.forEach {
                                if (it.type == 1)
                                    adminInfoViewModel.adminList.add(it)
                                else
                                    adminInfoViewModel.leaderList.add(it)
                            }
                            initRecyclerView()
                            showContentView()

                        }
                        Status.Error -> {
                            showError(it.error?.message)
                        }

                    }

                })
    }

    private fun initRecyclerView() {
        val helperList = ArrayList<LayoutHelper>()
        val linearLayoutHelper = LinearLayoutHelper()
        val linearLayoutHelper1 = LinearLayoutHelper()
        val linearLayoutHelper2 = LinearLayoutHelper()
        val linearLayoutHelper3 = LinearLayoutHelper()
        linearLayoutHelper.itemCount = 2
        linearLayoutHelper1.itemCount = 2
        linearLayoutHelper2.itemCount = adminInfoViewModel.adminList.size + 1
        linearLayoutHelper3.itemCount = adminInfoViewModel.leaderList.size + 1
        linearLayoutHelper1.setMargin(0, 8.dp(), 0, 0)
        linearLayoutHelper2.setMargin(0, 8.dp(), 0, 0)
        linearLayoutHelper3.setMargin(0, 8.dp(), 0, 0)
        helperList.add(linearLayoutHelper as LayoutHelper)
        helperList.add(linearLayoutHelper1 as LayoutHelper)
        helperList.add(linearLayoutHelper2 as LayoutHelper)
        helperList.add(linearLayoutHelper3 as LayoutHelper)


        adapter.layoutHelpers = helperList

        adapter.renderItems.add(TobaccoAdminTitleHolder("烤房信息"))
        adapter.renderItems.add(TobaccoAdminContentHolder(adminInfoViewModel.adminInfoEntity.name, type = 0))
        adapter.renderItems.add(TobaccoAdminTitleHolder("烤房地址"))
        adapter.renderItems.add(TobaccoAdminContentHolder(adminInfoViewModel.adminInfoEntity.regionAddress
                + adminInfoViewModel.adminInfoEntity.addressDetail,
                "(北纬:${adminInfoViewModel.adminInfoEntity.addressX}," +
                        "东经:${adminInfoViewModel.adminInfoEntity.addressY})", 1))
        adapter.renderItems.add(TobaccoAdminTitleHolder("管理员"))
        adminInfoViewModel.adminList.forEach {
            adapter.renderItems.add(TobaccoAdminContentHolder(it.name, it.tel, 2))
        }
        adapter.renderItems.add(TobaccoAdminTitleHolder("负责人"))
        adminInfoViewModel.leaderList.forEach {
            adapter.renderItems.add(TobaccoAdminContentHolder(it.name, it.tel, 2))
        }
        adapter.notifyDataSetChanged()
    }
}