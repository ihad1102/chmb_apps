package com.g.base.room.repository

import com.g.base.api.ErrorCode
import com.g.base.api.ErrorException


/**
 * A generic class that holds a value with its loading status.
 * @param <T>
</T> */
class ResultWarp<out T>(val status: Status, val data: T, val error: ErrorException?) {
    override fun equals(other: Any?): Boolean {
        if (this === other) {
            return true
        }

        if (other == null || other !is ResultWarp<*>) {
            return false
        }

        //状态码不一样
        if (status !== other.status) {
            return false
        }

        //如果为错误
        if (status == Status.Error) {
            //错误是否相同
            return error == other.error && data == other.data
        }

        //内容一样
        return data == other.data
    }

    override fun hashCode(): Int {
        var result = status.hashCode()
        result = 31 * result + (data?.hashCode() ?: 0)
        return result
    }

    companion object {
        fun <T> loading(data: T? = null, error: ErrorException? = null): ResultWarp<T?> = ResultWarp(Status.Loading, data, error)

        fun <T> success(data: T? = null): ResultWarp<T?> = ResultWarp(Status.Content, data, null)

        fun <T> error(error: ErrorException? = null, data: T? = null) = ResultWarp(Status.Error, data, error)

        fun <T> oauth(): ResultWarp<T?> = ResultWarp(Status.Oauth, null, ErrorException(ErrorCode.TOKEN, "登录过期 请重新登录", null))
    }
}
