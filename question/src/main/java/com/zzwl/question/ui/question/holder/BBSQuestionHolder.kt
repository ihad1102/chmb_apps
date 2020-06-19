package com.zzwl.question.ui.question.holder

import android.databinding.ObservableField
import android.view.View
import com.alibaba.android.arouter.launcher.ARouter
import com.alibaba.android.vlayout.LayoutHelper
import com.alibaba.android.vlayout.layout.GridLayoutHelper
import com.g.base.extend.dp
import com.g.base.extend.setMargin
import com.g.base.extend.transformTime
import com.g.base.ui.recyclerView.item.BaseItem
import com.g.base.ui.recyclerView.setupRecyclerView
import com.zzwl.question.R
import com.zzwl.question.databinding.QuestionItemRcyQuestionBinding
import com.zzwl.question.room.entity.remote.QuestionEntity
import com.zzwl.question.router.RouterPage


/**
 * Created by qhn on 2017/12/29.
 */
class BBSQuestionHolder(val questionEntity: QuestionEntity) : BaseItem<QuestionItemRcyQuestionBinding>() {
    override val layoutId: Int = R.layout.question_item_rcy_question
    val errorImg = ObservableField(R.drawable.question_ic_default_head_portrait_man)
    val time = (questionEntity.postTime ?: "").transformTime()
    val commentCountObs by lazy { ObservableField<Int>(questionEntity.replyCount ?: 0) }
    override fun onBind(binding: QuestionItemRcyQuestionBinding) {
        bindRecycleView(binding)
    }

    private fun bindRecycleView(binding: QuestionItemRcyQuestionBinding) {
        val imgList = questionEntity.imageUrls?.split(",")
        if ((imgList?.size ?: 0) > 0 && imgList?.get(0)?.isNotEmpty() == true) {
            binding.recyclerView.visibility = View.VISIBLE
        } else {
            binding.recyclerView.visibility = View.GONE
            binding.tvContent.setMargin(8.dp())
            return
        }

        val adapter = setupRecyclerView(binding.recyclerView)
        val gridLayoutHelper = GridLayoutHelper(3, imgList.size)
        gridLayoutHelper.hGap = 6.dp()
        gridLayoutHelper.setPadding(16.dp(), 8.dp(), 16.dp(), 8.dp())
        gridLayoutHelper.setAutoExpand(false)
        adapter.layoutHelpers = arrayListOf(gridLayoutHelper as LayoutHelper)
        imgList.forEachIndexed { index, imgUrl ->
            if (imgUrl.isNotEmpty()) {
                adapter.renderItems.add(PicHolder(imgUrl, clickable = true)
                        .apply {
                            setOnClickListener {
                                ARouter.getInstance().build(RouterPage.ImagePreviewActivity)
                                        .withInt("index", index)
                                        .withString("image", questionEntity.imageUrls).navigation(binding.root.context)
                            }
                        })
            }
        }
        adapter.notifyDataSetChanged()
    }

}