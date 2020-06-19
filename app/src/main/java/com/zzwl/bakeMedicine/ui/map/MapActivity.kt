package com.zzwl.bakeMedicine.ui.map

import android.annotation.SuppressLint
import android.arch.lifecycle.ViewModelProviders
import android.content.Context
import android.databinding.DataBindingUtil
import android.os.Bundle
import android.support.v4.content.ContextCompat
import android.view.LayoutInflater
import com.alibaba.android.arouter.facade.annotation.Autowired
import com.alibaba.android.arouter.facade.annotation.Route
import com.alibaba.android.arouter.launcher.ARouter
import com.baidu.mapapi.map.*
import com.baidu.mapapi.model.LatLng
import com.g.base.extend.dp
import com.g.base.extend.observeEx
import com.g.base.extend.resultNotNull
import com.g.base.help.tryCatch
import com.g.base.room.repository.Status
import com.g.base.router.RouteExtras
import com.g.base.token.TokenManage
import com.g.base.ui.BaseActivity
import com.zzwl.bakeMedicine.R
import com.zzwl.bakeMedicine.databinding.ActivityMapBinding
import com.zzwl.bakeMedicine.databinding.ViewMapMarkBinding
import com.zzwl.bakeMedicine.event.RefreshHomeEvent
import com.zzwl.bakeMedicine.room.entity.remote.MapEntity
import com.zzwl.bakeMedicine.router.RouterPage
import com.zzwl.bakeMedicine.viewModel.MapViewModel
import org.greenrobot.eventbus.EventBus

@Route(path = RouterPage.MapActivity)
class MapActivity : BaseActivity<ActivityMapBinding>() {
    @Autowired
    @JvmField
    var id = 0
    override var hasToolbar: Boolean = true
    private val mapViewModel by lazy { ViewModelProviders.of(this).get(MapViewModel::class.java) }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        ARouter.getInstance().inject(this)
        setContentView(R.layout.activity_map)
        initView()
        initMap()
    }

    private fun initView() {
        toolbar.setNavigationIcon(R.drawable.ic_arrow_back_toolbar_24dp)
        toolbar.title = "烤房分布"
        toolbar.setNavigationOnClickListener {
            finish()
        }
    }

    private fun initMap() {
        //获取数据后执行
        getData()


    }

    private fun getData() {
        mapViewModel.getMapList(TokenManage.getToken()?.userId?.toInt()
                ?: 0).resultNotNull().observeEx(this, {
            when (it.status) {
                Status.Loading -> {
                    showLoading()
                }
                Status.Content -> {
                    tryCatch {
                        setUpMap(it.data!!)
                    }
                    showContentView()
                }
                Status.Error -> {
                    setDefaultMap()
                    showContentView()
                }
            }
        })
    }

    private fun setDefaultMap() {
        val centerPoint = LatLng(34.2769, 108.953364)
        val zoomLevel = 10.0f
        val update = MapStatusUpdateFactory.zoomTo(zoomLevel)
        val newCenter = MapStatusUpdateFactory.newLatLng(centerPoint)
        contentView.bmapView.map.animateMapStatus(update)
        contentView.bmapView.map.setMapStatus(newCenter)
    }

    @SuppressLint("SetTextI18n")
    private fun setUpMap(data: List<MapEntity>) {
        mapViewModel.mapDataList.clear()
        val centerPoint = LatLng(data[0].addressX, data[0].addressY)
        val zoomLevel = 10.0f
        val update = MapStatusUpdateFactory.zoomTo(zoomLevel)
        val newCenter = MapStatusUpdateFactory.newLatLng(centerPoint)
        contentView.bmapView.map.animateMapStatus(update)
        contentView.bmapView.map.setMapStatus(newCenter)

        data.forEach {
            val point = LatLng(it.addressX, it.addressY)
            val bitmap = BitmapDescriptorFactory.fromResource(R.drawable.icon_gcoding)
            val option = MarkerOptions().position(point).icon(bitmap)
            option.animateType(MarkerOptions.MarkerAnimateType.grow)
            mapViewModel.mapDataList.add(MapDataSet(it, mark = contentView.bmapView.map.addOverlay(option)))
        }
        contentView.bmapView.map.setOnMarkerClickListener { marker ->
            mapViewModel.mapDataList.forEach { mapData ->
                if (mapData.mark == marker) {
                    val temp =when (mapData.mapEntity.fuelType){
                        1->"生物质"
                        2->"热泵"
                        else->"燃煤"
                    }
                    val inflateDataBinding = DataBindingUtil.inflate<ViewMapMarkBinding>(LayoutInflater.from(this), R.layout.view_map_mark, null, false)
                    val textView = inflateDataBinding.tvTobaccoInfo
                    textView.text = "烤房群名称：${mapData.mapEntity.name}\r\n" +
                            "烤房类型：$temp\r\n" +
                            "烤房总数：${mapData.mapEntity.normalCount + mapData.mapEntity.alarmCount + mapData.mapEntity.stopCount}\r\n" +
                            "正常的烤房数：${mapData.mapEntity.normalCount}\r\n" +
                            "告警烤房数：${mapData.mapEntity.alarmCount}\r\n" +
                            "停用烤房数：${mapData.mapEntity.stopCount}\r\n" +
                            "烤房地址：${mapData.mapEntity.regionAddress}\r\n${mapData.mapEntity.addressDetail}\r\n"
                    textView.setPadding(16.dp(), 16.dp(), 16.dp(), 0)
                    textView.setTextColor(ContextCompat.getColor(this, R.color.colorTextWhite))
                    inflateDataBinding.tvDetail.setOnClickListener {
                        getSharedPreferences("groupId", Context.MODE_PRIVATE).edit().putInt("groupId", mapData.mapEntity.id).apply()
                        EventBus.getDefault().post(RefreshHomeEvent(true, mapData.mapEntity.id.toString()))
                        finish()
                    }
                    val infoWindow = InfoWindow(inflateDataBinding.root, marker.position, -56)
                    contentView.bmapView.map.showInfoWindow(infoWindow)
                }
            }
            return@setOnMarkerClickListener true
        }
        contentView.bmapView.map.setOnMapClickListener(object : BaiduMap.OnMapClickListener {
            override fun onMapClick(p0: LatLng?) {
                contentView.bmapView.map.hideInfoWindow()

            }

            override fun onMapPoiClick(p0: MapPoi?): Boolean {
                return true
            }
        })
    }

    override fun onDestroy() {
        super.onDestroy()
        contentView.bmapView.onDestroy()
    }

    override fun onResume() {
        super.onResume()
        contentView.bmapView.onResume()
    }

    override fun onPause() {
        super.onPause()
        contentView.bmapView.onPause()
    }
}

data class MapDataSet(val mapEntity: MapEntity, val mark: Overlay)