package com.zzwl.bakeMedicine.ui.user

import android.arch.lifecycle.ViewModelProviders
import android.content.Context
import android.graphics.Color
import android.os.Bundle
import com.alibaba.android.arouter.facade.annotation.Route
import com.alibaba.android.arouter.launcher.ARouter
import com.g.base.extend.observeExOnce
import com.g.base.extend.resultNotNull
import com.g.base.extend.toast
import com.g.base.help.ProgressDialog
import com.g.base.room.repository.Status
import com.g.base.router.BasePage
import com.g.base.ui.BaseActivity
import com.zzwl.bakeMedicine.R
import com.zzwl.bakeMedicine.databinding.ActivityLoginBinding
import com.zzwl.bakeMedicine.router.RouterPage
import com.zzwl.bakeMedicine.viewModel.UserViewModel


@Route(path = BasePage.Oauth)
class OauthActivity : BaseActivity<ActivityLoginBinding>() {
    override var isFullScreen: Boolean = true

    val viewModel by lazy { ViewModelProviders.of(this).get(UserViewModel::class.java) }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)
        initView()
        showContentView()


    }

    private val progressDialog by lazy { ProgressDialog() }
    private fun initView() {
        contentView.edtPassword.setHintTextColor(Color.WHITE)
        contentView.edtUserName.setHintTextColor(Color.WHITE)
        contentView.tvLogin.setOnClickListener {
            when {
                contentView.edtUserName.text.toString().trim().isEmpty() -> toast("用户名不能为空")
                contentView.edtPassword.text.toString().trim().isEmpty() -> toast("密码不能为空")
                else -> login()
            }
        }
        contentView.tvRegister.setOnClickListener {
            openDialog()
        }
    }

    private fun login() {
        viewModel.repository.login(contentView.edtUserName.text.toString(),
                contentView.edtPassword.text.toString())
                .resultNotNull()
                .observeExOnce(this, {
                    when (it.status) {
                        Status.Loading -> {
                            progressDialog.setStart("正在努力的加载中..")
                        }
                        Status.Content -> {

                            if (it.data!!.groupIdList?.isEmpty() != false)
                                getSharedPreferences("groupId", Context.MODE_PRIVATE).edit().putInt("groupId", -1).apply()
                            else {
                                getSharedPreferences("groupId", Context.MODE_PRIVATE).edit().putInt("groupId", it.data!!.groupIdList!![0]).apply()
                                if (it.data!!.groupIdList!!.size > 1) {
                                    ARouter.getInstance()
                                            .build(RouterPage.MapActivity)
                                            .navigation(this)
                                }
                            }
                            progressDialog.dismiss()
                            finish()
                        }
                        else -> {
                            progressDialog.setFiled(it.error?.message
                                    ?: "出现了一些错误..") { it.dismiss() }
                        }
                    }

                })
    }


    private fun openDialog() {
        val registerFragment = RegisterDialogFragment()
        registerFragment.show(supportFragmentManager, "RegisterDialogFragment")

    }


}