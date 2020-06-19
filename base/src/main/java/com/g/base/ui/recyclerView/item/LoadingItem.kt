package com.g.base.ui.recyclerView.item

import android.databinding.ObservableField
import android.view.View
import com.g.base.R
import com.g.base.databinding.ItemLoadingBinding

/**
 * Created by G on 2017/11/24 0024.
 */
class LoadingItem : BaseItem<ItemLoadingBinding>() {
    private var status = LoadingStatus.LOADING_SUCCEED
    private var lastLoadingTime = 0L
    override val layoutId: Int = R.layout.item_loading

    var loadingListener: (() -> Unit)? = null
    //加载中可见性
    var progressVisible = ObservableField<Boolean>(false)
    //文本描述
    var loadingText = ObservableField<String>("")

    //没有更多数据
    //加载中
    //加载成功
    //加载失败
    fun setLoadingStatus(status: LoadingStatus) {
        this.status = status
        when (status) {
            LoadingStatus.LOADING_NO_MORE -> {
                progressVisible.set(false)
                loadingText.set("没有更多了")
            }
            LoadingStatus.LOADING_ING -> {
                val currentTimeMillis = System.currentTimeMillis()
                if (currentTimeMillis - lastLoadingTime > 1000L) {
                    progressVisible.set(true)
                    loadingText.set("加载中...")
                    loadingListener?.invoke()
                    lastLoadingTime = currentTimeMillis
                } else {
                    this.status = LoadingStatus.LOADING_FAILED
                    progressVisible.set(false)
                    loadingText.set("加载失败 请点击重试")
                }
            }
            LoadingStatus.LOADING_FAILED -> {
                progressVisible.set(false)
                loadingText.set("加载失败 请点击重试")
            }
            LoadingStatus.LOADING_SUCCEED -> {
                progressVisible.set(false)
                loadingText.set("")
            }
        }
    }

    override fun onClick(view: View) {
        if (status == LoadingStatus.LOADING_FAILED)
            setLoadingStatus(LoadingItem.LoadingStatus.LOADING_ING)
    }

    override fun onBind(binding: ItemLoadingBinding) {
        if (status == LoadingStatus.LOADING_SUCCEED) {
            setLoadingStatus(LoadingStatus.LOADING_ING)
        }
    }

    enum class LoadingStatus {
        LOADING_NO_MORE,
        LOADING_ING,
        LOADING_SUCCEED,
        LOADING_FAILED
    }
}
