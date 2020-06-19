package com.zzwl.information.ui

import android.arch.lifecycle.ViewModelProviders
import android.databinding.ObservableField
import android.os.Bundle
import com.alibaba.android.arouter.facade.annotation.Autowired
import com.alibaba.android.arouter.facade.annotation.Route
import com.alibaba.android.arouter.launcher.ARouter
import com.alibaba.android.vlayout.LayoutHelper
import com.alibaba.android.vlayout.layout.LinearLayoutHelper
import com.g.base.extend.dp
import com.g.base.extend.observeExOnce
import com.g.base.extend.resultNotNull
import com.g.base.room.repository.ListStatus
import com.g.base.room.repository.Status
import com.g.base.ui.BaseActivity
import com.g.base.ui.recyclerView.setupRecyclerView
import com.zzwl.information.R
import com.zzwl.information.databinding.InfoActivityWikiDetailsBinding
import com.zzwl.information.ui.holder.TitleHolder
import com.zzwl.information.ui.holder.WikiDetailsHolder
import com.zzwl.information.room.entity.WikiDetailEntity
import com.zzwl.information.router.RouterPage
import com.zzwl.information.viewModel.WikiViewModel

@Route(path = RouterPage.WikiDetailsActivity)
class WikiDetailsActivity : BaseActivity<InfoActivityWikiDetailsBinding>() {
    @Autowired
    @JvmField
    var id: Int = -1
    private var listStatus = ListStatus.Content
    private val viewModel by lazy { ViewModelProviders.of(this).get(WikiViewModel::class.java) }
    private val adapter by lazy { setupRecyclerView(contentView.recyclerView) }
    val imgUrlObs by lazy { ObservableField<String>() }
    override var isFullScreen: Boolean = true
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        ARouter.getInstance().inject(this)
        setContentView(R.layout.info_activity_wiki_details)
        contentView.toolbar.setNavigationIcon(R.drawable.ic_arrow_back_toolbar_24dp)
        contentView.toolbar.setNavigationOnClickListener { finish() }
        getData()
    }

    private fun getData() {
        viewModel.getWikiDetail(id)
                .resultNotNull()
                .observeExOnce(this, {
                    when (it.status) {
                        Status.Loading -> {
                            if (listStatus == ListStatus.Content)
                                showLoading()
                        }
                        Status.Content -> {
                            imgUrlObs.set(it.data!!.imageUrl)
                            contentView.data = this
                            contentView.toolbar.title = it.data!!.title
                            applyData(it.data!!)
                            showContentView()
                            listStatus = ListStatus.Content
                        }
                        Status.Error -> {
                            when (listStatus) {
                                ListStatus.Content -> {
                                    showError(it.error?.message)
                                }
                            }
                            listStatus = ListStatus.Content
                        }
                    }

                })
    }

    private fun applyData(data: WikiDetailEntity) {
        val linearLayoutHelper = LinearLayoutHelper()
        linearLayoutHelper.itemCount = data.contentList.size + 1
        if (adapter.renderItems.isNotEmpty()) {
            adapter.renderItems.clear()
        }
        adapter.renderItems.add(TitleHolder(data.title, data.intro))
        data.contentList.forEach {
            adapter.renderItems.add(WikiDetailsHolder(it.head, it.detail))
        }
        linearLayoutHelper.setPadding(0, 0, 0, 16.dp())
        adapter.layoutHelpers = listOf(linearLayoutHelper as LayoutHelper)
        adapter.notifyDataSetChanged()
    }
}