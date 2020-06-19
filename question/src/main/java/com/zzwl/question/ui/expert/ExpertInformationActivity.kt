package com.zzwl.question.ui.expert

import android.arch.lifecycle.ViewModelProviders
import android.os.Bundle
import com.alibaba.android.arouter.facade.annotation.Autowired
import com.alibaba.android.arouter.facade.annotation.Route
import com.alibaba.android.arouter.launcher.ARouter
import com.bumptech.glide.Glide
import com.g.base.extend.observeEx
import com.g.base.extend.resultNotNull
import com.g.base.extend.setSpace
import com.g.base.extend.toast
import com.g.base.help.glide.GlideBlurTransformation
import com.g.base.room.entity.TokenEntity
import com.g.base.room.repository.Status
import com.g.base.router.RouteExtras
import com.g.base.ui.BaseActivity
import com.zzwl.question.R
import com.zzwl.question.databinding.QuestionActivityExpertDetailBinding
import com.zzwl.question.event.RefreshExpertEvent
import com.zzwl.question.other.extend.progressOauthDialog
import com.zzwl.question.room.entity.common.ExpertInfoCEntity
import com.zzwl.question.router.RouterPage
import com.zzwl.question.ui.myself.MyselfFragment
import com.zzwl.question.viewModel.BasicInfoViewModel
import com.zzwl.question.viewModel.ExpertInfoViewModel
import org.greenrobot.eventbus.EventBus

@Route(path = RouterPage.ExpertInformationActivity, extras = RouteExtras.NeedOauth)
class ExpertInformationActivity : BaseActivity<QuestionActivityExpertDetailBinding>() {
    override var hasToolbar: Boolean = false
    override var isFullScreen: Boolean = true

    private val expertInfoViewModel by lazy { ViewModelProviders.of(this).get(ExpertInfoViewModel::class.java) }
    private val userInfoViewModel by lazy { ViewModelProviders.of(this).get(BasicInfoViewModel::class.java) }
    var headerImage: Any? = null
    lateinit var expectInfoData: ExpertInfoCEntity
    var userName = ""
    var userAvatar = ""

    @Autowired
    @JvmField
    var id: String = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        ARouter.getInstance().inject(this)
        setContentView(R.layout.question_activity_expert_detail)
        getUserInfo()
        setupView()
        obsData()
    }

    private fun getUserInfo() {
        userInfoViewModel.obsUserInfo()
                .resultNotNull()
                .observeEx(this) {
                    when (it.status) {
                        Status.Content -> {
                            userName = it.data!!.username ?: ""
                            userAvatar = it.data!!.headImage.url
                        }
                        Status.Error -> {
                            toast("获取用户信息失败")
                        }
                    }
                }

        userInfoViewModel.operateUserInfo(false)
    }

    fun setupView() {
        val toolbarReal = contentView.toolbarLayout!!.toolbar
        toolbarReal.background = null
        toolbarReal.setNavigationIcon(R.drawable.ic_arrow_back_toolbar_24dp)
        toolbarReal.setNavigationOnClickListener { finish() }
        contentView.collapsing.contentScrim = resources.getDrawable(R.drawable.shape_gradient_primary)

        var scrollRange = 0
        contentView.appbar.addOnOffsetChangedListener { appbar, verticalOffset ->
            if (scrollRange == 0) {
                scrollRange = appbar.totalScrollRange
            }
            if (-verticalOffset >= scrollRange) {
                toolbarReal.getChildAt(1).alpha = 1f
            } else {
                toolbarReal.getChildAt(1).alpha = 0f
            }
        }

    }

    private fun followAction(isFollow: Boolean) {
        val titleToolbar = contentView.toolbarLayout!!.toolbar
        titleToolbar.menu.findItem(R.id.actionFollow).isVisible = !isFollow
        titleToolbar.menu.findItem(R.id.actionUnFollow).isVisible = isFollow
        titleToolbar.setOnMenuItemClickListener {
            var fansCount: Int = expectInfoData.fansCount.toIntOrNull() ?: 0
            fansCount = if (expectInfoData.isFollow) --fansCount else ++fansCount
            val data = expectInfoData.copy(isFollow = !expectInfoData.isFollow, fansCount = fansCount.toString())
            expertInfoViewModel.followExpert(data)
                    .progressOauthDialog({ it.dismiss() })
                    .observeEx(this) {
                        EventBus.getDefault().post(RefreshExpertEvent(true))
                    }
            true
        }
    }

    override fun onTokenChange(data: TokenEntity?) {
        if (data == null) {
            showNeedOauth()
        } else {
            onReload()
        }
    }

    override fun onReload() {
        expertInfoViewModel.operateExpertInfo(id)
    }

    private fun obsData() {
        onReload()
        expertInfoViewModel.obsExpertInfo()
                .resultNotNull()
                .observeEx(this) {
                    when (it.status) {
                        Status.Loading -> showLoading()
                        Status.Content -> {
                            expectInfoData = it.data!!
                            applyData()
                            showContentView()
                            setExpertImageHeight()
                        }
                        Status.Error -> showError(it.error?.message)
                    }
                }
    }


    private fun setExpertImageHeight() {
        contentView.expertDetail!!.root.post {
            contentView.appbar.setSpace(contentView.expertDetail!!.root.height)
        }
    }

    private fun applyData() {
        if (headerImage == null) {
            headerImage = if (expectInfoData.headImg.url?.isEmpty() != false)
                R.drawable.question_ic_default_head_portrait_man
            else {
                expectInfoData.headImg.url
            }
            val titleToolbar = contentView.toolbarLayout!!.toolbar
            titleToolbar.title = expectInfoData.userName
            titleToolbar.inflateMenu(R.menu.action_follow)
            Glide.with(this)
                    .load(headerImage)
                    .asBitmap()
                    .transform(GlideBlurTransformation(this, 25f, 4))
                    .into(contentView.expertDetail!!.blurBackground)
            val myselfFragment = ARouter.getInstance().build(RouterPage.MyselfFragment).withInt("type", 1).withInt("userId", id.toIntOrNull()
                    ?: 0).navigation(this) as MyselfFragment
            supportFragmentManager.beginTransaction().replace(R.id.frameLayout, myselfFragment).commitAllowingStateLoss()
        }
        followAction(expectInfoData.isFollow)
        contentView.data = this
    }


}




