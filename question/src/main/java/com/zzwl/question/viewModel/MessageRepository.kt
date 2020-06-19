package com.zzwl.question.viewModel

import com.g.base.common.apiProvider
import com.g.base.help.postSimpleNotify
import com.g.base.room.repository.BaseRepository
import com.g.base.room.repository.MockDbLiveData
import com.g.base.room.repository.NetworkBoundResult
import com.zzwl.question.JpushNotifyHelp
import com.zzwl.question.api.MessageApi
import com.zzwl.question.api.NotifyURl
import com.zzwl.question.room.entity.remote.MessageREntity

/**
 * Created by G on 2017/11/13 0013.
 */
class MessageRepository : BaseRepository() {
    companion object {
        val messageData by lazy { HashMap<String, MockDbLiveData<List<MessageREntity>>>() }
    }

    fun getMessageList(map: Map<String, String>) =
            NetworkBoundResult(
                    {
                        apiProvider.create(MessageApi::class.java)
                                .getMessageList(map)
                    },
                    {
                        messageData.getOrPut(map["type"]!!, { MockDbLiveData() })
                    },
                    {
                        true
                    },
                    {
                        messageData[map["type"]!!]?.postValue(it)
                    }
            )

    fun getMessageCount() =
            NetworkBoundResult(
                    {
                        apiProvider.create(MessageApi::class.java)
                                .getMessageCount()
                    }
            )

    fun markMessageRead(id: String, type: String) =
            NetworkBoundResult(
                    {
                        apiProvider.create(MessageApi::class.java)
                                .markMessageRead(id)
                    },
                    onRemoteSucceed = {
                        val mockDbLiveData = messageData[type]
                        mockDbLiveData?.postValue(
                                mockDbLiveData.value?.map {
                                    var result = it
                                    if (it.id == id || it.msgId == id) {
                                        result = it.copy()
                                        result.read = 1
                                        JpushNotifyHelp.cleanMessageNotify(result.msgId)
                                    }
                                    result
                                })
                        postSimpleNotify(NotifyURl.MessageCount)
                    }
            )

    fun markAllMessageRead(type: String) =
            NetworkBoundResult(
                    {
                        apiProvider.create(MessageApi::class.java)
                                .markAllMessageRead(type)
                    },
                    onRemoteSucceed = {
                        val mockDbLiveData = messageData[type]
                        mockDbLiveData?.postValue(
                                mockDbLiveData.value?.map {
                                    val result = it.copy()
                                    result.read = 1
//                                    JpushNotifyHelp.cleanMessageNotify(result.msgId)todo
                                    result
                                })
                        postSimpleNotify(NotifyURl.MessageCount)
                    }
            )

    fun delMessage(id: String, type: String) =
            NetworkBoundResult(
                    {
                        apiProvider.create(MessageApi::class.java).delMessage(id)
                    },
                    onRemoteSucceed = {
                        val mockDbLiveData = messageData[type]
                        mockDbLiveData?.postValue(mockDbLiveData.value?.filter { id != it.id })
                        postSimpleNotify(NotifyURl.MessageCount)
                    }
            )
}