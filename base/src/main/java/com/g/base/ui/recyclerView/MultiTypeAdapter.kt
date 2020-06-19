package com.g.base.ui.recyclerView

import android.databinding.ViewDataBinding
import android.support.v7.util.DiffUtil
import android.view.ViewGroup
import com.alibaba.android.vlayout.VirtualLayoutAdapter
import com.alibaba.android.vlayout.VirtualLayoutManager
import com.g.base.extend.inflateDataBinding
import com.g.base.help.ioResultToMainThread
import com.g.base.ui.recyclerView.item.BaseItem
import com.g.base.ui.recyclerView.item.LoadingItem
import io.reactivex.disposables.Disposable


open class MultiTypeAdapter(val layoutManager: VirtualLayoutManager) : VirtualLayoutAdapter<MultiTypeViewHolder<ViewDataBinding>>(layoutManager) {
    /**********************************数据**********************************/
    var renderItems: ArrayList<BaseItem<*>> = ArrayList()
    var diffItems: ArrayList<BaseItem<*>> = ArrayList()
    /**********************************数据**********************************/

    /**********************************加载更多**********************************/
    private val loadingItem by lazy { LoadingItem() }

    //设置加载更多监听 同时允许加载更多
    fun setOnLoadingListener(listener: () -> Unit) {
        //没有添加过Item的话就不通知添加 会引发异常
        if (itemCount > 0 && loadingItem.loadingListener == null)
            notifyItemInserted(itemCount - 1)
        loadingItem.loadingListener = listener
    }

    //移除加载更多监听 同时取消加载更多
    fun removeOnLoadingListener() {
        if (loadingItem.loadingListener != null) {
            loadingItem.loadingListener = null
            //没有添加过Item的话就不通知移除 会引发异常
            if (itemCount > 0)
                notifyItemRemoved(itemCount)
        }
    }

    //加载完成
    fun setLoadingSucceed() {
        if (loadingItem.loadingListener != null)
            loadingItem.setLoadingStatus(LoadingItem.LoadingStatus.LOADING_SUCCEED)
    }

    //加载失败
    fun setLoadingFailed() {
        if (loadingItem.loadingListener != null)
            loadingItem.setLoadingStatus(LoadingItem.LoadingStatus.LOADING_FAILED)
    }

    //没有更多
    fun setLoadingNoMore() {
        if (loadingItem.loadingListener != null)
            loadingItem.setLoadingStatus(LoadingItem.LoadingStatus.LOADING_NO_MORE)
    }
    /**********************************加载更多**********************************/

    /**********************************DiffUtil**********************************/
    private var diffCalcSubscribe: Disposable? = null

    fun calcDiffAndDispatchUpdate(
            /*子线程*/ areItemTheSame: (old: BaseItem<*>, oldPosition: Int, new: BaseItem<*>, newPosition: Int) -> Boolean, //item是否一致
            /*子线程*/ areContentsTheSame: (old: BaseItem<*>, oldPosition: Int, new: BaseItem<*>, newPosition: Int) -> Boolean, //内容是否一致
            /*主线程*/ dispatchFinishInMain: (() -> Unit)? = null, /*子线程*/ dispatchStartInIo: (() -> Unit)? = null) {
        //先取消之前的
        if (diffCalcSubscribe?.isDisposed == false) {
            diffCalcSubscribe?.dispose()
        }
        //diffItems清空
        val diffItems = this.diffItems
        this.diffItems = ArrayList()
        //计算新的
        diffCalcSubscribe = ioResultToMainThread(
                {
                    //前置操作 IO
                    dispatchStartInIo?.invoke()

                    val hasLoadMore = loadingItem.loadingListener != null
                    val oldItemSize = if (hasLoadMore) (if (renderItems.size > 0) renderItems.size + 1 else 0) else renderItems.size
                    val newItemSize = if (hasLoadMore) (if (diffItems.size > 0) diffItems.size + 1 else 0) else diffItems.size

                    DiffUtil.calculateDiff(object : DiffUtil.Callback() {
                        override fun getOldListSize(): Int = oldItemSize
                        override fun getNewListSize(): Int = newItemSize

                        override fun areItemsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
                            return when {
                                hasLoadMore && (oldItemPosition == oldItemSize - 1 && newItemPosition == newItemSize - 1) -> false
                                hasLoadMore && (oldItemPosition == oldItemSize - 1 || newItemPosition == newItemSize - 1) -> false
                                else -> areItemTheSame(renderItems[oldItemPosition], oldItemPosition, diffItems[newItemPosition], newItemPosition)
                            }
                        }

                        override fun areContentsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
                            return when {
                                hasLoadMore && (oldItemPosition == oldItemSize - 1 || newItemPosition == newItemSize - 1) -> false
                                else -> areContentsTheSame(renderItems[oldItemPosition], oldItemPosition, diffItems[newItemPosition], newItemPosition)
                            }
                        }
                    })
                },
                {
                    renderItems = diffItems
                    it.dispatchUpdatesTo(this)
                    dispatchFinishInMain?.invoke()
                }
        )
    }
    /**********************************DiffUtil**********************************/

    /**********************************必须重载**********************************/
    //如果用DiffUtil刷新数据 在计算完毕之前拿到的数据都是老数据
    override fun getItemCount(): Int {
        return if (loadingItem.loadingListener == null) renderItems.size else (if (renderItems.size > 0) renderItems.size + 1 else 0)
    }

    override fun getItemViewType(position: Int): Int {
        if (loadingItem.loadingListener != null && position == renderItems.size) {
            return loadingItem.layoutId
        }
        return renderItems[position].layoutId
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MultiTypeViewHolder<ViewDataBinding> =
            MultiTypeViewHolder(parent.inflateDataBinding(viewType))

    @Suppress("UNCHECKED_CAST")
    override fun onBindViewHolder(holder: MultiTypeViewHolder<ViewDataBinding>, position: Int) {
        if (position == renderItems.size)
            holder.bindTo(loadingItem as BaseItem<ViewDataBinding>)
        else
            holder.bindTo(renderItems[position] as BaseItem<ViewDataBinding>)
    }
    /**********************************必须重载**********************************/
}
