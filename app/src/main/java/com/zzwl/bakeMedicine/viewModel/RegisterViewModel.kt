package com.zzwl.bakeMedicine.viewModel

import android.annotation.SuppressLint
import android.databinding.ObservableField
import com.g.base.extend.mainIoSchedulers
import com.g.base.extend.progressSubscribe
import com.g.base.extend.toast
import com.g.base.viewModel.BaseViewModel
import com.youtu.Youtu
import com.zzwl.bakeMedicine.ui.user.Register2Fragment
import io.reactivex.Observable
import org.json.JSONObject

class RegisterViewModel : BaseViewModel() {
    var phoneNumber: String = ""
    var code: String = ""
    var password: String = ""
    private val youtuSdk: Youtu by lazy { Youtu("10138822", "AKIDxnTyHQKCnGZgbNsNabR9g1A8uUs4Jva5", "RhldJn7HetwoVlpjRyJE4iASaWHtKZcX", Youtu.API_YOUTU_END_POINT) }
    @SuppressLint("CheckResult")
    fun scanPic(fragment: Register2Fragment, idCardImgFront: String, nameObs: ObservableField<String>, idObs: ObservableField<String>) {
        Observable.create<JSONObject> {
            val idCardOcr: JSONObject = youtuSdk.IdCardOcr(idCardImgFront, 0)
            it.onNext(idCardOcr)
            it.onComplete()
        }
                .mainIoSchedulers()
                .progressSubscribe(
                        {
                            nameObs.set(it.getString("name"))
                            idObs.set(it.getString("id"))

                        },
                        {
                            fragment.toast("采集身份证信息失败 请手动输入")
                        }
                )
    }
}