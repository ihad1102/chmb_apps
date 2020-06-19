package com.g.base.ui.recyclerView.decoration

import android.content.Context
import com.g.base.R
import com.yanyusong.y_divideritemdecoration.Y_Divider
import com.yanyusong.y_divideritemdecoration.Y_DividerBuilder
import com.yanyusong.y_divideritemdecoration.Y_DividerItemDecoration

/**
 * Created by G on 2017/8/21 0021.
 */
class LinearItemDecoration(private val context:Context, private var startWith : Int = 0, private var endWith : Int = Int.MAX_VALUE) : Y_DividerItemDecoration(context){

    override fun getDivider(itemPosition: Int): Y_Divider {
        val color = context.resources.getColor(R.color.colorDivider)
        val dividerBuilder = Y_DividerBuilder()
        if(itemPosition in startWith..endWith){
            dividerBuilder.setBottomSideLine(true, color,0.5f,0f,0f)
        }
        return  dividerBuilder.create()
    }
}