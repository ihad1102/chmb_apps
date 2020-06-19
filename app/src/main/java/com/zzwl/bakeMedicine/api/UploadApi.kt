package com.zzwl.bakeMedicine.api

import com.g.base.api.BaseResult
import com.zzwl.bakeMedicine.room.entity.remote.ImageEntity
import io.reactivex.Observable
import okhttp3.MultipartBody
import retrofit2.http.Multipart
import retrofit2.http.POST
import retrofit2.http.Part

interface UploadApi {

    @Multipart
    @POST("file/image_upload")
    fun uploadImg(@Part files: MultipartBody.Part): Observable<BaseResult<ImageEntity?>>
}
