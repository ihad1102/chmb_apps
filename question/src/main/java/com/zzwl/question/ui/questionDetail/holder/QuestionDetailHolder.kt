package com.zzwl.question.ui.questionDetail.holder

import android.databinding.ObservableField
import android.view.View
import com.alibaba.android.arouter.launcher.ARouter
import com.alibaba.android.vlayout.LayoutHelper
import com.alibaba.android.vlayout.VirtualLayoutManager
import com.alibaba.android.vlayout.layout.GridLayoutHelper
import com.g.base.extend.dp
import com.g.base.ui.recyclerView.MultiTypeAdapter
import com.g.base.ui.recyclerView.item.BaseItem
import com.g.base.ui.recyclerView.setupRecyclerView
import com.zzwl.question.R
import com.zzwl.question.databinding.QuestionItemRcyQuestionDetailBinding
import com.zzwl.question.router.RouterPage
import com.zzwl.question.ui.question.holder.PicHolder

/**
 * Created by qhn on 2018/1/5.
 */
class QuestionDetailHolder(private val adoptReplyId: Int?,
                           val subImages: String?,
                           val imageHost: String?,
                           val headImg: String?,
                           val userName: String?,
                           val postTime: String?,
                           val content: String?

) : BaseItem<QuestionItemRcyQuestionDetailBinding>() {
    override val layoutId: Int = R.layout.question_item_rcy_question_detail
    val errorImg = ObservableField(R.drawable.question_ic_default_head_portrait_man)
    private var imgUrl: List<String>? = null
    val tvAdopt = ObservableField<String>()
    override fun onBind(binding: QuestionItemRcyQuestionDetailBinding) {
        super.onBind(binding)
        initView(binding)
    }

    private fun initView(binding: QuestionItemRcyQuestionDetailBinding) {
        tvAdopt.set(if (adoptReplyId == 0) "未采纳" else "已采纳")
        val adapter = setupRecyclerView(binding.recyclerView, VirtualLayoutManager.VERTICAL)
        imgUrl = subImages?.split(",")?.filter { it != "" }
        if (imgUrl?.size ?: 0 > 0) binding.recyclerView.visibility = View.VISIBLE
        bindRecycleView(adapter, binding)
    }

    private fun bindRecycleView(adapter: MultiTypeAdapter, binding: QuestionItemRcyQuestionDetailBinding) {
        val gridLayoutHelper = GridLayoutHelper(3, imgUrl?.size ?: 0)
        gridLayoutHelper.hGap = 6.dp()
        gridLayoutHelper.setPadding(16.dp(), 0, 16.dp(), 8.dp())
        gridLayoutHelper.setAutoExpand(false)
        adapter.layoutHelpers = arrayListOf(gridLayoutHelper as LayoutHelper)
        imgUrl?.forEachIndexed { index, imgUrl ->
            adapter.renderItems.add(PicHolder(imageHost + imgUrl, clickable = true)
                    .apply {
                        setOnClickListener {
                            ARouter.getInstance().build(RouterPage.ImagePreviewActivity)
                                    .withInt("index", index)
                                    .withString("image", subImages).navigation(binding.root.context)
                        }
                    })
        }
    }

}