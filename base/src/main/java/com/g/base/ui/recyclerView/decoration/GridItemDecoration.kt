package com.g.base.ui.recyclerView.decoration

import android.content.Context
import com.g.base.R
import com.yanyusong.y_divideritemdecoration.Y_Divider
import com.yanyusong.y_divideritemdecoration.Y_DividerBuilder
import com.yanyusong.y_divideritemdecoration.Y_DividerItemDecoration

/**
 * Created by G on 2017/8/19 0019.
 */
class GridItemDecoration(private val context: Context, private val size: Int) : Y_DividerItemDecoration(context) {
    override fun getDivider(itemPosition: Int): Y_Divider {
        val builder = Y_DividerBuilder()
        val color = context.resources.getColor(R.color.colorDivider)
        if (itemPosition % 2 == 0) {
            builder.setRightSideLine(true, color, 0.5f, 0f, 0f)
        }
        if (itemPosition + 2 < size) {
            builder.setBottomSideLine(true, color, 0.5f, 0f, 0f)
        }
        return builder.create()
    }

}