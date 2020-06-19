package com.zzwl.bakeMedicine.ui.user

import android.arch.lifecycle.ViewModelProviders
import android.databinding.ObservableField
import android.os.CountDownTimer
import android.text.Editable
import android.text.InputFilter
import android.text.TextWatcher
import android.widget.EditText
import com.g.base.common.apiProvider
import com.g.base.extend.mainIoSchedulers
import com.g.base.extend.toast
import com.g.base.ui.BaseFragment
import com.g.base.utils.isPhoneNumber
import com.zzwl.bakeMedicine.R
import com.zzwl.bakeMedicine.api.UserApi
import com.zzwl.bakeMedicine.databinding.FragmentRegisterBinding
import com.zzwl.bakeMedicine.event.NextEvent
import com.zzwl.bakeMedicine.viewModel.RegisterViewModel
import org.greenrobot.eventbus.EventBus
import java.util.regex.Pattern

class RegisterFragment : BaseFragment<FragmentRegisterBinding>() {
    override fun setContentView(): Int = R.layout.fragment_register
    val phoneErrorObs by lazy { ObservableField("") }
    val passwordErrorObs by lazy { ObservableField("") }
    val countDownObs by lazy { ObservableField("获取验证码") }
    val enableObs by lazy { ObservableField(false) }
    val nextEnableObs by lazy { ObservableField(false) }
    private val viewModel by lazy { ViewModelProviders.of(activity!!).get(RegisterViewModel::class.java) }
    override fun onLazyLoadOnce() {
        showContentView()
        initView()
    }

    private fun initView() {
        contentView.data = this
        contentView.edtPhone.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {
                if (!isPhoneNumber(contentView.edtPhone.text.toString())) {
                    phoneErrorObs.set("手机号码格式不正确")
                    enableObs.set(false)
                } else {
                    enableObs.set(true)
                    phoneErrorObs.set("")
                }
            }

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
            }
        })
        contentView.edtPassword.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {
                when {
                    s.toString().length < 6 -> passwordErrorObs.set("密码不能小于六位")
                    contentView.edtPassword.text.toString().length > 16 -> passwordErrorObs.set("密码不能超过十六位")
                    else -> {
                        passwordErrorObs.set("")
                        nextEnableObs.set(passwordErrorObs.get()!!.isEmpty() && phoneErrorObs.get()!!.isEmpty())
                    }
                }
            }

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
            }
        })
        contentView.tvCode.setOnClickListener {
            apiProvider.create(UserApi::class.java).getCode(contentView.edtPhone.text.toString(), 0)
                    .mainIoSchedulers()
                    .subscribe({}, {
                        toast(it.message ?: "发送短信失败")
                    })
            tick(enableObs, countDownObs)

        }
        setEditTextInputSpeChat(contentView.edtPassword)

        contentView.tvNext.setOnClickListener {
            viewModel.phoneNumber = contentView.edtPhone.text.toString()
            viewModel.code = contentView.edtCode.text.toString()
            viewModel.password = contentView.edtPassword.text.toString()
            EventBus.getDefault().post(NextEvent(true))
        }
    }

    private fun setEditTextInputSpeChat(editText: EditText) {
        val filter = InputFilter { source, _, _, _, _, _ ->
            val speChat = "[`~!@#$%^&*()+=|{}':;',\\[\\].<>/?~！@#￥%……&*（）——+|{}【】‘；：”“’。，、？]"
            val pattern = Pattern.compile(speChat)
            val matcher = pattern.matcher(source.toString())
            if (matcher.find()) {
                ""
            } else {
                null
            }
        }
        editText.filters = arrayOf(filter)
    }

    private fun tick(enableObs: ObservableField<Boolean>, desObs: ObservableField<String>) {
        var total = 60
        val timer = object : CountDownTimer(1000L * 60L, 1000L) {
            override fun onFinish() {
                enableObs.set(true)
                desObs.set("获取验证码")
            }

            override fun onTick(p0: Long) {
                desObs.set("$total 秒后重试")
                enableObs.set(false)
                total--
            }
        }
        timer.start()
    }

}