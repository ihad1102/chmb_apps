package com.zzwl.farmingtrade.widget


import android.app.Activity
import android.app.Dialog
import android.content.Context
import android.content.DialogInterface
import android.view.Gravity
import android.view.WindowManager
import chihane.jdaddressselector.AddressProvider
import chihane.jdaddressselector.AddressSelector
import chihane.jdaddressselector.OnAddressSelectedListener
import chihane.jdaddressselector.model.City
import chihane.jdaddressselector.model.County
import chihane.jdaddressselector.model.Province
import chihane.jdaddressselector.model.Street
import com.g.base.api.ErrorCode
import com.g.base.extend.*
import com.g.base.room.repository.Status
import com.g.base.ui.BaseActivity
import com.zzwl.farmingtrade.R
import com.zzwl.farmingtrade.room.entity.remote.AddressSuggestCEntity
import com.zzwl.farmingtrade.room.repository.AddressRepository

class AddressSelectDialog : Dialog {
    private lateinit var selector: AddressSelector
    private lateinit var activity: BaseActivity<*>
    private val addressRepository by lazy { AddressRepository() }
    private var pickPoint = false
    private var isPickCity = false
    private var addressSuggest: AddressSuggestCEntity? = null
    private var isSuggestionLocation: Boolean = true

    constructor(context: Context) : super(context, R.style.bottom_dialog) {
        init(context)
    }

    constructor(context: Context, themeResId: Int) : super(context, themeResId) {
        init(context)
    }

    constructor(context: Context, cancelable: Boolean, cancelListener: DialogInterface.OnCancelListener) : super(context, cancelable, cancelListener) {
        init(context)
    }

    private fun init(context: Context) {
        selector = AddressSelector(context)
        activity = context as BaseActivity<*>
        setContentView(selector.view)

        val window = window
        val params = window.attributes
        params.width = WindowManager.LayoutParams.MATCH_PARENT
        params.height = 256.dp()
        window.attributes = params

        window.setGravity(Gravity.BOTTOM)
    }

    fun setOnAddressSelectedListener(listener: OnAddressSelectedListener?) {
        if (listener == null) return
        this.selector.onAddressSelectedListener = listener

        selector.setAddressProvider(object : AddressProvider {
            override fun provideStreetsWith(p0: Int, p1: AddressProvider.AddressReceiver<Street>?) {
                if (!pickPoint) {
                    p1?.send(null)
                    return
                }
                addressRepository.getPickupPoint(p0.toString())
                        .resultNotNull()
                        .observeExOnce(activity) {
                            when (it.status) {
                                Status.Content -> {
                                    p1?.send(
                                            it.data!!.map {
                                                Street().apply {
                                                    id = it.id.toInt()
                                                    county_id = p0
                                                    name = "[${it.name}] 地址: ${it.address} 电话: ${it.phone}"
                                                }
                                            }
                                    )
                                }
                                Status.Error -> {
                                    if (it.error?.code == ErrorCode.EMPTY) {
                                        context.toast("该区域暂时没有自提点~")
                                    } else {
                                        context.toast(it.error?.message ?: "该区域暂时没有自提点~")
                                        p1?.send(null)
                                    }
                                }
                            }
                        }
            }

            override fun provideCountiesWith(p0: Int, p1: AddressProvider.AddressReceiver<County>?) {
                addressRepository.getRegion(3.toString(), p0.toString())
                        .apply {
                            observeExOnce(activity) {
                                if (it.status == Status.Content) {
                                    if (isPickCity) {
                                        p1?.send(null)
                                        return@observeExOnce
                                    }
                                    val arrayList = ArrayList<County>()
                                    it.data?.forEach {
                                        arrayList.add(County().apply { name = it.regionName; id = it.regionId.toInt(); city_id = p0 })
                                    }
                                    p1?.send(arrayList)
                                }
                            }
                        }
            }

            override fun provideCitiesWith(p0: Int, p1: AddressProvider.AddressReceiver<City>?) {
                addressRepository.getRegion(2.toString(), p0.toString())
                        .apply {
                            observeExOnce(activity) {
                                if (it.status == Status.Content) {
                                    val suggestAppoint = if (isSuggestionLocation) {
                                        addressSuggest
                                                ?: AddressSelectDialog.addressSuggest
                                    } else {
                                        null
                                    }
                                    var suggestPosition = -1
                                    val arrayList = ArrayList<City>()
                                    it.data?.forEachIndexed { index, data ->
                                        arrayList.add(City().apply { name = data.regionName; id = data.regionId.toInt(); province_id = p0 })
                                        if (suggestAppoint != null && data.regionId == suggestAppoint.city) {
                                            suggestPosition = index
                                        }
                                    }
                                    p1?.send(arrayList)
                                    if (suggestPosition != -1)
                                        setTimeout(100L) { selector.onItemClick(null, null, suggestPosition, 0) }
                                }
                            }
                        }
            }

            override fun provideProvinces(p0: AddressProvider.AddressReceiver<Province>?) {
                addressRepository.getRegion("1", "1")
                        .apply {
                            observeExOnce(activity) {
                                if (it.status == Status.Content) {
                                    val suggestAppoint = if (isSuggestionLocation) {
                                        addressSuggest ?: AddressSelectDialog.addressSuggest
                                    } else {
                                        null
                                    }

                                    var suggestPosition = -1
                                    val arrayList = ArrayList<Province>()
                                    it.data?.forEachIndexed { index, data ->
                                        arrayList.add(Province().apply { name = data.regionName; id = data.regionId.toInt() })
                                        if (suggestAppoint != null && data.regionId == suggestAppoint.province) {
                                            suggestPosition = index
                                        }
                                    }
                                    p0?.send(arrayList)
                                    if (suggestPosition != -1)
                                        setTimeout(100L) { selector.onItemClick(null, null, suggestPosition, 0) }
                                }
                            }
                        }
            }
        })
    }

    companion object {
        @JvmOverloads
        fun show(activity: Activity, listener: OnAddressSelectedListener? = null, pickPoint: Boolean = false, suggestAppoint: AddressSuggestCEntity? = null, isPickCity: Boolean = false, isSuggestionLocation: Boolean = true): AddressSelectDialog {
            val dialog = AddressSelectDialog(activity, R.style.bottom_dialog)
            dialog.pickPoint = pickPoint
            dialog.isPickCity = isPickCity
            dialog.addressSuggest = suggestAppoint
            dialog.setOnAddressSelectedListener(listener)
            dialog.isSuggestionLocation = isSuggestionLocation
            dialog.show()
            return dialog
        }

        var addressSuggest: AddressSuggestCEntity? = null
    }
}