package com.zzwl.farmingtrade.ui.search

import android.annotation.SuppressLint
import android.content.Context
import java.text.SimpleDateFormat
import java.util.*

/**
 *  search_history_pesticide :          农药搜索记录      type:1
 *  search_history_seed :               种子搜索记录      type:2
 *  search_history_fertilizer :         化肥搜索记录      type:3
 *  search_history_goods :              商品搜索记录      type:4
 *  search_history_skill :              技术搜索记录      type:5
 *  search_history_question :           问题搜索记录      type:6
 *  search_history_expert :             专家搜索记录      type:7
 *  search_history_farming_trade :      采购搜索记录      type:8
 *  search_history_farming_product :    农产品搜索记录    type:9
 */
@SuppressLint("SimpleDateFormat")
class SearchHistoryHelper {
    constructor(context: Context, type: Int) {
        mContext = context
        mType = type
        tag = when (type) {
            1 -> "search_history_pesticide"
            2 -> "search_history_seed"
            3 -> "search_history_fertilizer"
            4 -> "search_history_goods"
            5 -> "search_history_skill"
            6 -> "search_history_question"
            7 -> "search_history_expert"
            8 -> "search_history_farming_trade"
            9 -> "search_history_farming_product"
            else -> ""
        }
    }

    private val mFormat by lazy { SimpleDateFormat("yyyyMMddHHmmss") }
    private var mContext: Context
    private var mType: Int = 0
    private var tag = ""
    @SuppressLint("CommitPrefEdits")
    fun removeAll() {
        val sp = mContext.getSharedPreferences(tag, Context.MODE_PRIVATE)
        val edit = sp.edit()
        edit.clear()
        edit.apply()
    }

    @SuppressLint("CommitPrefEdits")
    fun remove(key: String) {
        val sp = mContext.getSharedPreferences(tag, Context.MODE_PRIVATE)
        val edit = sp.edit()
        edit.remove(key)
        edit.apply()
    }

    fun save(value: String) {
        val historyMaps = getAll() as Map<String, String>
        for ((key, value1) in historyMaps) {
            if (value == value1) {
                remove(key)
            }
        }
        put(mFormat.format(Date()), value)
    }

    fun getAll(): Map<String, *> {
        val sp = mContext.getSharedPreferences(tag,
                Context.MODE_PRIVATE)
        return sp.all
    }

    fun put(key: String, value: String) {
        val sp = mContext.getSharedPreferences(tag,
                Context.MODE_PRIVATE)
        val editor = sp.edit()
        editor.putString(key, value)
        editor.apply()
    }
}