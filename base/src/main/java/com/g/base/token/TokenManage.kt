package com.g.base.token

import com.g.base.extend.runDataBaseTransition
import com.g.base.room.database.BaseDatabase
import com.g.base.room.entity.TokenEntity

/**
 * Created by G on 2017/8/11 0011.
 */
class TokenManage {
    companion object {
        fun cleanToken() {
            BaseDatabase.instant.runDataBaseTransition {
                getTokenDao().nukeTable()
            }
        }

        fun saveToken(token: TokenEntity) {
            BaseDatabase.instant.runDataBaseTransition {
                getTokenDao().nukeTable()
                getTokenDao().inset(token)
            }
        }

        fun getToken() = BaseDatabase.instant.getTokenDao().getToken()

        fun getTokenLive() = BaseDatabase.instant.getTokenDao().getTokenLive()

        fun isOauth() = getToken()?.let { it.token.isNotEmpty() && it.userId.isNotEmpty() } ?: false
    }
}



