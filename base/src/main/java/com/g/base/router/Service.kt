package com.g.base.router

import android.content.Context
import android.widget.Toast
import com.alibaba.android.arouter.facade.Postcard
import com.alibaba.android.arouter.facade.annotation.Route
import com.alibaba.android.arouter.facade.service.DegradeService
import com.alibaba.android.arouter.facade.service.SerializationService
import com.g.base.help.d
import com.google.gson.Gson
import java.lang.reflect.Type

/**
 * Created by G on 2017/8/19 0019.
 */
@Route(path = RouterSever.DegradeService)
class GDegradeService : DegradeService {
    override fun init(context: Context?) {
    }

    override fun onLost(context: Context, postcard: Postcard) {
        Toast.makeText(context, "路径找不到 ${postcard.path}", Toast.LENGTH_SHORT).show()
        d("路径找不到 ${postcard.path}")
    }
}


@Route(path = RouterSever.JsonService)
class JsonService : SerializationService {
    val gson by lazy { Gson() }

    override fun init(context: Context?) {
    }

    override fun <T : Any?> parseObject(json: String?, clazz: Type?): T? {
        if (json == null) return null
        return gson.fromJson(json, clazz)
    }

    override fun <T : Any?> json2Object(json: String?, clazz: Class<T>?): T? {
        if (json == null) return null
        return gson.fromJson(json, clazz)
    }

    override fun object2Json(instance: Any?): String {
        if (instance == null) return ""
        return gson.toJson(instance)
    }

}