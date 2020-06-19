package com.zzwl.farmingtrade.room.repository

import com.g.base.common.apiProvider
import com.g.base.room.repository.NetworkBoundResult
import com.zzwl.farmingtrade.api.PurchaseApi
import com.zzwl.farmingtrade.api.SupplierApi
import okhttp3.MultipartBody


class PurchaseRepository {
    fun getPurchaseEntity(pageNum: String, pageSize: String, orderBy: String, keyword: String?, cropId: String) =
            NetworkBoundResult(
                    {
                        apiProvider.create(PurchaseApi::class.java)
                                .getBuyHallList(
                                        mapOf(
                                                Pair("pageNum", pageNum),
                                                Pair("pageSize", pageSize),
                                                Pair("orderBy", orderBy),
                                                Pair("cropId", cropId)
                                        ),
                                        keyword
                                )
                    }
            )

    fun getHotSearchWord() = NetworkBoundResult(
            {
                apiProvider.create(PurchaseApi::class.java)
                        .getHotSearchWord()
            })

    fun getPurchaseDetail(id: Int) = NetworkBoundResult(
            {
                apiProvider.create(PurchaseApi::class.java)
                        .getPurchaseDetail(id)
            }
    )

    fun postPurchase(cropId: String, title: String, regions: String, quantity: String, price: String?, packType: String, specification: String, otherInfo: String) =
            NetworkBoundResult({
                apiProvider.create(PurchaseApi::class.java)
                        .postPurchase(mapOf(
                                Pair("cropId", cropId),
                                Pair("title", title),
                                Pair("regions", regions),
                                Pair("quantity", quantity),
                                Pair("packType", packType),
                                Pair("specification", specification),
                                Pair("otherInfo", otherInfo)
                        ), price)
            })

    fun createSupplier(array: Array<MultipartBody.Part>) = NetworkBoundResult(
            {
                apiProvider.create(SupplierApi::class.java)
                        .createSupplier(array)
            }
    )

    fun getMyPurchase(pageNum: String, pageSize: String) = NetworkBoundResult({
        apiProvider.create(PurchaseApi::class.java)
                .getMyPurchase(pageNum, pageSize)
    })

    fun getMyTrading(pageNum: String, pageSize: String) = NetworkBoundResult({
        apiProvider.create(PurchaseApi::class.java)
                .getMyTrading(pageNum, pageSize)
    })

    fun getPurchase(id: String) = NetworkBoundResult(
            {
                apiProvider.create(PurchaseApi::class.java)
                        .getPurchase(id)
            }
    )

    fun upDatePurchase(id: String,
                       cropId: String,
                       title: String,
                       regions: String,
                       quantity: String,
                       price: String?,
                       packType: String,
                       specification: String,
                       doing: Boolean,
                       userId: String,
                       otherInfo: String) =
            NetworkBoundResult({
                apiProvider.create(PurchaseApi::class.java)
                        .updatePurchase(mapOf(
                                Pair("id", id),
                                Pair("cropId", cropId),
                                Pair("title", title),
                                Pair("regions", regions),
                                Pair("quantity", quantity),
                                Pair("packType", packType),
                                Pair("specification", specification),
                                Pair("otherInfo", otherInfo),
                                Pair("userId", userId)
                        ), price, doing)
            })
}