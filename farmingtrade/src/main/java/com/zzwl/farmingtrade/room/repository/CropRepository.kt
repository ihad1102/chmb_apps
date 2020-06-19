package com.zzwl.farmingtrade.room.repository

import com.g.base.common.apiProvider
import com.g.base.extend.runDataBaseTransition
import com.g.base.room.repository.BaseRepository
import com.g.base.room.repository.NetworkBoundResult
import com.zzwl.farmingtrade.api.CropApi
import com.zzwl.farmingtrade.room.database.AppDatabase

/**
 * Created by qhn on 2018/1/8.
 */
class CropRepository : BaseRepository() {
    fun getCrops(categoryId: Int, force: Boolean) =
            NetworkBoundResult(
                    {
                        apiProvider.create(CropApi::class.java)
                                .getCrop(categoryId)
                    }
            )

    fun getCropType() =
            NetworkBoundResult(
                    {
                        apiProvider.create(CropApi::class.java).getCropType()
                    }
            )


}
