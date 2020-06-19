package com.zzwl.farmingtrade.room.repository

import com.g.base.common.apiProvider
import com.g.base.room.repository.BaseRepository
import com.g.base.room.repository.NetworkBoundResult
import com.zzwl.farmingtrade.api.PurchaseApi
import com.zzwl.farmingtrade.api.SupplierApi
import okhttp3.MultipartBody

class SupplierRepository : BaseRepository() {
    fun postFramingProduct(multipartBodys: Array<MultipartBody.Part>) = NetworkBoundResult({
        apiProvider.create(SupplierApi::class.java)
                .postFramingProduct(multipartBodys)
    })

    fun farmingCropList() = NetworkBoundResult(
            {
                apiProvider.create(SupplierApi::class.java)
                        .farmingCropList()
            }
    )

    fun getFarmingProductList(pageNum: String, pageSize: String, keyword: String? = null, orderBy: String, cropId: String) = NetworkBoundResult(
            {
                apiProvider.create(SupplierApi::class.java)
                        .getFarmingProduct(mapOf(
                                Pair("pageNum", pageNum),
                                Pair("pageSize", pageSize),
                                Pair("orderBy", orderBy),
                                Pair("cropId", cropId)
                        ), keyword)
            }
    )

    fun getHotWord() = NetworkBoundResult(
            {
                apiProvider.create(SupplierApi::class.java)
                        .getHotSearchWord()
            }
    )

    fun getFarmingProductDetails(id: String) = NetworkBoundResult(
            {
                apiProvider.create(SupplierApi::class.java)
                        .getFarmingProductDetails(id)
            }
    )


    //买家发起,从农产品页面
    fun createPurchase(array: Array<MultipartBody.Part>) = NetworkBoundResult({
        //        apiProvider.create(PurchaseApi::class.java)
//                .createPurchase(
//                        mapOf(Pair("seller", seller),
//                                Pair("cropId", cropId),
//                                Pair("cropProductId", cropProductId),
//                                Pair("weight", weight),
//                                Pair("totalPrice", totalPrice)))
        apiProvider.create(PurchaseApi::class.java)
                .createPurchase(array)
    })

    fun getSubsidiesRules(id: Int) = NetworkBoundResult({
        apiProvider.create(SupplierApi::class.java)
                .getSubsidiesRules(id)
    })

    fun getMySupplier(pageNum: String, pageSize: String) = NetworkBoundResult({
        apiProvider.create(SupplierApi::class.java)
                .getMySupplier(pageNum, pageSize)
    })

    fun getSupplier(id: String) = NetworkBoundResult({
        apiProvider.create(SupplierApi::class.java)
                .getSupplier(id)
    })

    fun updateProduct(multipartBodys: Array<MultipartBody.Part>) = NetworkBoundResult({
        apiProvider.create(SupplierApi::class.java)
                .updateProduct(multipartBodys)
    })

}