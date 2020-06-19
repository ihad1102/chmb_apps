package com.zzwl.question.ui.expert

import android.databinding.ObservableField
import android.os.Bundle
import com.alibaba.android.arouter.facade.annotation.Autowired
import com.alibaba.android.arouter.facade.annotation.Route
import com.alibaba.android.arouter.launcher.ARouter
import com.g.base.extend.setTimeout
import com.g.base.room.entity.TokenEntity
import com.g.base.router.RouteExtras
import com.g.base.ui.BaseActivity
import com.ogaclejapan.smarttablayout.utils.v4.FragmentPagerItemAdapter
import com.ogaclejapan.smarttablayout.utils.v4.FragmentPagerItems
import com.zzwl.question.R
import com.zzwl.question.databinding.QuestionViewSmartTabViewpagerBinding
import com.zzwl.question.event.ExpertIdEvent
import com.zzwl.question.router.RouterPage
import com.zzwl.question.ui.expert.fragment.InvitationFragment
import org.greenrobot.eventbus.EventBus

/**
 * Created by qhn on 2018/1/3.
 */
@Route(path = RouterPage.InvitationActivity, extras = RouteExtras.NeedOauth)
class InvitationActivity : BaseActivity<QuestionViewSmartTabViewpagerBinding>() {
    override var hasToolbar: Boolean = true
    lateinit var adapter: FragmentPagerItemAdapter
    val checkObserver = HashMap<String, ObservableField<Boolean>>()

    @JvmField
    @Autowired
    var checkData: HashMap<String, String>? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        ARouter.getInstance().inject(this)
        if (checkData == null) {
            checkData = HashMap()
        } else {
            checkData!!.keys.forEach {
                checkObserver[it] = ObservableField(true)
            }
        }
        setContentView(R.layout.question_view_smart_tab_viewpager)
        initView()
        showContentView()
    }

    override fun onTokenChange(data: TokenEntity?) {
        if (data == null) {
            showNeedOauth()
        } else {
            showContentView()
        }
    }

    private fun initView() {
        toolbar.title = "邀请专家"
        toolbar.inflateMenu(R.menu.confirm_text)
        toolbar.menu.getItem(0).title = "邀请"
        toolbar.setOnMenuItemClickListener {
            finish()
            setTimeout(100L, {
                EventBus.getDefault().post(ExpertIdEvent(checkData!!))
            })
            return@setOnMenuItemClickListener true
        }
        toolbar.setNavigationIcon(R.drawable.ic_arrow_back_toolbar_24dp)
        toolbar.setNavigationOnClickListener { finish() }
        adapter = FragmentPagerItemAdapter(
                supportFragmentManager, FragmentPagerItems.with(this)
                .add("我关注的", InvitationFragment::class.java, Bundle().apply { putBoolean("isFollow", true) })
                .add("全部专家", InvitationFragment::class.java, Bundle().apply { putBoolean("isFollow", false) })
                .create())

        contentView.viewPagerLayout.viewPager.adapter = adapter
        contentView.smartTabLayout.smartTabLayout.setViewPager(contentView.viewPagerLayout.viewPager)
    }

    fun changeMenu(type: Int) {//0,1:取消,邀请
        val temp = if (type == 1) "邀请" else "取消"
        if (temp != toolbar.menu.getItem(0).title)
            toolbar.menu.getItem(0).title = temp
    }
}