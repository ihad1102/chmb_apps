package com.zzwl.bakeMedicine.ui.tobaccoAdmin.holder

import com.g.base.ui.recyclerView.item.BaseItem
import com.zzwl.bakeMedicine.R
import com.zzwl.bakeMedicine.databinding.ItemAdminContentBinding

/**
 * type:0,1,2  :  烤房信息 烤房地址,管理员or负责人
 *
 */
class TobaccoAdminContentHolder(val content1: String, val content2: String = "", val type: Int) : BaseItem<ItemAdminContentBinding>() {
    override val layoutId: Int = R.layout.item_admin_content
}