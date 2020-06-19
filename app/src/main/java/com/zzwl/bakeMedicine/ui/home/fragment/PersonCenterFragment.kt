package com.zzwl.bakeMedicine.ui.home.fragment

import android.annotation.SuppressLint
import android.app.AlertDialog
import android.arch.lifecycle.ViewModelProviders
import android.content.Context
import android.databinding.ObservableField
import com.alibaba.android.arouter.launcher.ARouter
import com.bumptech.glide.Glide
import com.g.base.appContent
import com.g.base.extend.observeExOnce
import com.g.base.extend.progressDialog
import com.g.base.extend.resultNotNull
import com.g.base.help.ProgressDialog
import com.g.base.help.ioResultToMainThread
import com.g.base.room.entity.TokenEntity
import com.g.base.room.repository.Status
import com.g.base.token.TokenManage
import com.g.base.ui.BaseFragment
import com.g.base.utils.DialogUtils
import com.zzwl.bakeMedicine.R
import com.zzwl.bakeMedicine.databinding.FragmentPersonCenterBinding
import com.zzwl.bakeMedicine.room.repository.UserRepository
import com.zzwl.bakeMedicine.viewModel.PersonCenterViewModel
import com.zzwl.question.router.RouterPage

class PersonCenterFragment : BaseFragment<FragmentPersonCenterBinding>() {
    override var hasToolbar: Boolean = true
    override var hasStatusBar: Boolean = true
    private val viewModel by lazy { ViewModelProviders.of(this).get(PersonCenterViewModel::class.java) }
    private val userRepository by lazy { UserRepository() }
    override fun setContentView(): Int = R.layout.fragment_person_center
    val nameObs by lazy { ObservableField<String>() }
    val ageObs by lazy { ObservableField<String>() }
    val telObs by lazy { ObservableField<String>() }
    val addressObs by lazy { ObservableField<String>() }
    val sexObs by lazy { ObservableField<String>() }
    val nickName by lazy { ObservableField<String>() }
    val placeholder = ObservableField<Int>(R.color.colorGreenDarkWeek)
    var imgUrl = ""
    val closeMessageObs by lazy { ObservableField("关闭消息") }
    override fun onLazyLoadOnce() {
        initView()
        onReload()
    }

    @SuppressLint("CommitPrefEdits")
    private fun initView() {
        toolbar.title = "精准烘烤"
        contentView.tvCleanCache.setOnClickListener {
            val progressDialog = ProgressDialog()
            progressDialog.setStart("正在清理中~")
            ioResultToMainThread(
                    {
                        Glide.get(appContent).clearDiskCache()
                    },
                    {
                        progressDialog.setSucceed("清理缓存成功") { it.dismiss() }
                    }
            )

        }
        contentView.tvVersion.setOnClickListener {
            ARouter.getInstance().build(com.zzwl.question.router.RouterPage.AboutActivity).navigation(context)
        }
        contentView.tvLogout.setOnClickListener {
            val builder = AlertDialog.Builder(context)
            builder.setMessage("是否退出登录")
            builder.setNegativeButton("取消", { dialog, _ ->
                dialog.dismiss()
            })
            builder.setPositiveButton("确定", { dialog, _ ->
                dialog.dismiss()
                userRepository.logout().progressDialog()
                        .observeExOnce(this@PersonCenterFragment,
                                {
                                    if (it.status == Status.Content) {
                                        TokenManage.cleanToken()
                                        context!!.getSharedPreferences("groupId", Context.MODE_PRIVATE).edit().clear().apply()
                                    }
                                }
                        )
            })
            builder.create().show()
        }
        contentView.tvCloseMessage.setOnClickListener {
            DialogUtils.build(activity!!, msg = "是否${closeMessageObs.get()}", onPositive = {
                val temp = if (closeMessageObs.get() == "关闭消息") "开启消息" else "关闭消息"
                closeMessageObs.set(temp)
            })
        }
    }

    override fun onReload() {
        getData()
    }

    override fun onTokenChange(data: TokenEntity?) {
        if (data != null)
            onReload()
        else
            showNeedOauth()
    }

    fun getData() {
        viewModel.getUserInfo()
                .resultNotNull()
                .observeExOnce(this, {
                    when (it.status) {
                        Status.Loading -> {
                            showLoading()
                        }
                        Status.Content -> {
                            viewModel.userInfoEntity = it.data!!
                            nameObs.set("真实姓名：" + viewModel.userInfoEntity.realName)
                            ageObs.set("年龄：" + viewModel.userInfoEntity.age)
                            telObs.set("联系电话：" + viewModel.userInfoEntity.tel)
                            val tempSex = if (viewModel.userInfoEntity.sex) "男" else "女"
                            sexObs.set("性别：$tempSex")
                            addressObs.set(viewModel.userInfoEntity.regionAddress + viewModel.userInfoEntity.addressDetail)
                            imgUrl = viewModel.userInfoEntity.headImage.url
                            contentView.data = this

                            showContentView()
                        }
                        Status.Error -> {
                            showError(it.error?.message)
                        }

                    }

                })
    }

    fun onClickMessage() {
        ARouter.getInstance().build(RouterPage.MessageActivity).navigation(context)
    }

    fun onClickQuestion() {
        ARouter.getInstance().build(RouterPage.MyselfActivity).withInt("type", 0).navigation(context)
    }

    fun onClickNotice() {
        ARouter.getInstance().build(com.zzwl.question.router.RouterPage.NoticeActivity).navigation(context)
    }

    fun onClickFollow() {
        ARouter.getInstance().build(RouterPage.MyFollowedActivity).navigation(context)
    }
}