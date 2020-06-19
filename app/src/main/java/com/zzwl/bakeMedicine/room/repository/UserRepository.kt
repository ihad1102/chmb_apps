package com.zzwl.bakeMedicine.room.repository

import com.g.base.common.apiProvider
import com.g.base.room.entity.TokenEntity
import com.g.base.room.repository.BaseRepository
import com.g.base.room.repository.NetworkBoundResult
import com.g.base.token.TokenManage
import com.zzwl.bakeMedicine.api.UserApi

class UserRepository : BaseRepository() {
    fun login(telephone: String, password: String) = NetworkBoundResult(
            {
                apiProvider.create(UserApi::class.java).login(telephone, password)
            },
            saveRemoteResult = {
                it?.apply {
                    TokenManage.saveToken(TokenEntity(it.userId, it.token, System.currentTimeMillis(), it.groupIdList))
                }
            },
            loadFormDb = {
                TokenManage.getTokenLive()
            },
            shouldRemote = { true }
    )

    fun getUserInfo() = NetworkBoundResult({
        apiProvider.create(UserApi::class.java).getUserInfo()
    })

    fun logout() = NetworkBoundResult({
        apiProvider.create(UserApi::class.java).logout()
    })

    fun register(tel: String,
                 code: String,
                 password: String,
                 faceCardId: String,
                 backCardId: String,
                 idcardInHandId: String,
                 idcard: String,
                 realName: String) = NetworkBoundResult(
            {
                apiProvider.create(UserApi::class.java).register(mapOf(
                        Pair("tel", tel),
                        Pair("code", code),
                        Pair("password", password),
                        Pair("faceCardId", faceCardId),
                        Pair("backCardId", backCardId),
                        Pair("idcardInHandId", idcardInHandId),
                        Pair("idcard", idcard),
                        Pair("realName", realName)
                ))
            },
            saveRemoteResult = {
                it?.apply {
                    TokenManage.saveToken(TokenEntity(it.userId, it.token, System.currentTimeMillis(), it.groupIdList))
                }
            },
            loadFormDb = {
                TokenManage.getTokenLive()
            },
            shouldRemote = { true })

}