package com.g.base.api

import android.content.Context
import android.net.ConnectivityManager
import com.g.base.appContent
import com.g.base.extend.runDataBaseTransition
import com.g.base.help.d
import com.g.base.help.e
import com.g.base.help.tryCatch
import com.g.base.room.database.BaseDatabase
import com.g.base.room.entity.TokenEntity
import com.g.base.token.TokenManage
import com.google.gson.Gson
import com.google.gson.GsonBuilder
import com.google.gson.JsonElement
import com.google.gson.JsonParser
import com.google.gson.reflect.TypeToken
import com.parkingwang.okhttp3.LogInterceptor.LogInterceptor
import okhttp3.*
import retrofit2.Converter
import retrofit2.Retrofit
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory
import java.lang.reflect.Type
import java.net.SocketTimeoutException
import java.util.*

var serverTime: Long = 0L
    get() = System.currentTimeMillis() - field
    set(value) {
        field = System.currentTimeMillis() - value
    }

//retrofit
fun createRetrofitService(url: String): Retrofit {
    //OkHttp配置
    fun createOkClient(): OkHttpClient {
        val builder = OkHttpClient.Builder()
        builder.addInterceptor(BaseInterceptor())
        builder.addInterceptor(LogInterceptor())
        return builder.build()
    }

    //Json转换器配置
    fun gsonFactory(): GsonConvert {
        val gson = GsonBuilder().create()
        return GsonConvert(gson)
    }

    return Retrofit.Builder().apply {
        baseUrl(url)
        addConverterFactory(gsonFactory())
        addCallAdapterFactory(RxJava2CallAdapterFactory.create())
        client(createOkClient())
    }.build()
}


//OkHttp interceptor
//网络请求 异常拦截 请求Log
class BaseInterceptor : Interceptor {
    override fun intercept(chain: Interceptor.Chain): Response {
        val cm = appContent.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        val info = cm.activeNetworkInfo
        if (info != null && info.isConnected) {
            try {
                //添加公共请求头 Token Uid
                val requestBuild = chain.request().newBuilder()
                val token = TokenManage.getToken()
                if (token != null) requestBuild.addHeader("app_token", "${token.token}")
                val request = requestBuild.build()
                val proceed = chain.proceed(request)
                d(request.body())
                tryCatch {
                    val date = Date(proceed.header("Date"))
                    serverTime = date.time
                }
                return proceed

            } catch (error: Exception) {
                if (error is SocketTimeoutException) {
                    throw ErrorException(ErrorCode.NET, "网络连接超时", null)
                }
                e(error.message)
                throw ErrorException(ErrorCode.UNKNOWN, "未知错误", null)
            }
        } else {
            throw ErrorException(ErrorCode.NET, "网络连接出现问题 请检查网络设置", null)
        }
    }
}

////////////////////////////////////////////////////
//RetrofitConvert
//Gson 转码类  主要用于编码转换UTF-8 错误的拦截 Gson解析错误 TOKEN过期的拦截
class GsonConvert(private val gson: Gson) : Converter.Factory() {

    override fun responseBodyConverter(type: Type?, annotations: Array<out Annotation>?, retrofit: Retrofit?): Converter<ResponseBody, *>? =
            GsonConvertResponseBody(gson, TypeToken.get(type))

    override fun requestBodyConverter(type: Type?, parameterAnnotations: Array<out Annotation>?, methodAnnotations: Array<out Annotation>?, retrofit: Retrofit?): Converter<*, RequestBody>? =
            GsonConvertRequestBody(TypeToken.get(type))
}

class GsonConvertRequestBody(private val type: TypeToken<*>) : Converter<Any, RequestBody> {
    override fun convert(value: Any?): RequestBody? {
        if (value != null && type.rawType == value::class.java) {
            return RequestBody.create(MediaType.parse("application/json; charset=UTF-8"), value.toString())
        }
        return null
    }
}

class GsonConvertResponseBody(private val gson: Gson, private val type: TypeToken<*>) : Converter<ResponseBody, Any> {
    override fun convert(value: ResponseBody): Any {
        val parse: JsonElement?
        val json = value.string()
        val code: Int?
        val msg: String?
        try {
            parse = JsonParser().parse(json)
            code = parse?.asJsonObject?.get("code")?.asInt
            msg = parse?.asJsonObject?.get("message")?.asString
        } catch (e: Exception) {
            throw ErrorException(ErrorCode.JSON, "数据格式异常 解析失败了", null)
        }
        if (code == null || code == ErrorCode.OK) {
            try {
                //解析全部
                return gson.fromJson<Any>(parse, type.type)
            } catch (e: Exception) {
                //Json解析错误
                throw ErrorException(ErrorCode.JSON, "数据格式异常 解析失败了", null)
            }
        } else if (code == ErrorCode.TOKEN) {
            //Token过期或者不存在
            BaseDatabase.instant.runDataBaseTransition {
                getTokenDao().inset(TokenEntity())
                getTokenDao().nukeTable()
            }
            throw ErrorException(code, msg ?: "登录过期 请重新登录", null)
        } else {
            d(json)
            //其他错误
            throw ErrorException(code, msg ?: "其他错误", json)
        }
    }
}

// 返回格式
data class BaseResult<T>(val code: Int, val message: String, val content: T?)

// 错误异常格式
data class ErrorException(val code: Int, override val message: String, val responseContent: String?) : Exception()


sealed class ErrorCode {
    companion object {
        const val OK = 1000 //没有问题
        const val NET = -100 //网络异常
        const val JSON = 101 //Json解析错误
        const val UNKNOWN = -111 //未知错误
        const val TOKEN = 2002 //TOKEN 过期或者无效

        const val EMPTY = 2001 //没有数据
        const val CART_EMPTY = 1204 //购物车空
        const val POST_ERROR = 1607 //包含敏感内容不能发布
    }
}