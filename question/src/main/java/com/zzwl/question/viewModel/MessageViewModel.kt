package com.zzwl.question.viewModel

import android.arch.lifecycle.MutableLiveData
import com.g.base.extend.map
import com.g.base.extend.switchMap
import com.g.base.room.repository.Status
import com.g.base.viewModel.BaseViewModel

/**
 * Created by G on 2017/11/26 0026.
 */
class MessageViewModel : BaseViewModel() {
    private val messageRepository by lazy { MessageRepository() }

    private val operateMessageList by lazy { MutableLiveData<Map<String, String>>() }
    fun obsMessageList() = operateMessageList.switchMap {
        messageRepository.getMessageList(it)
                .map {
                    if (it.status == Status.Content) {
                        it.data?.forEach {
                            it.navParams.node = it.node
                            it.navParams.detail = it.detail
                        }
                    }
                    it
                }
    }

    fun operateMessageList(type: String, offset: Int = 0) {
        operateMessageList.value = mapOf(Pair("pageNum", ((offset + 29) / 30 + 1).toString()), Pair("pageSize", "30"), Pair("type", type))
    }

    fun markMessageRead(id: String, type: String) = messageRepository.markMessageRead(id, type)

    fun markAllMessageRead(type: String) = messageRepository.markAllMessageRead(type)

    fun delMessage(id: String, type: String) = messageRepository.delMessage(id, type)

    private val operateMessageCount by lazy { MutableLiveData<Boolean>() }
    fun operateMessageCount() {
        operateMessageCount.value = true
    }

    fun obsMessageCount() = operateMessageCount.switchMap {
        messageRepository.getMessageCount()
    }

}