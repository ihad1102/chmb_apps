package com.zzwl.question.ui.postQuestion

import android.annotation.SuppressLint
import android.content.Context
import android.databinding.ObservableField
import android.graphics.Color
import android.graphics.drawable.BitmapDrawable
import android.location.LocationListener
import android.location.LocationManager
import android.os.Bundle
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.WindowManager
import android.widget.PopupWindow
import android.widget.RelativeLayout
import com.alibaba.android.arouter.facade.annotation.Route
import com.alibaba.android.arouter.launcher.ARouter
import com.alibaba.android.vlayout.LayoutHelper
import com.alibaba.android.vlayout.layout.GridLayoutHelper
import com.g.base.common.apiProvider
import com.g.base.extend.*
import com.g.base.help.ProgressDialog
import com.g.base.help.TakePhotoHelp
import com.g.base.room.entity.TokenEntity
import com.g.base.room.repository.Status
import com.g.base.router.RouteExtras
import com.g.base.ui.BaseActivity
import com.g.base.ui.recyclerView.setupRecyclerView
import com.jph.takephoto.app.TakePhoto
import com.jph.takephoto.model.TResult
import com.zzwl.question.R
import com.zzwl.question.api.QuestionApi
import com.zzwl.question.databinding.QuestionActivityPostQuestionBinding
import com.zzwl.question.event.ExpertIdEvent
import com.zzwl.question.event.RefreshCommentAmount
import com.zzwl.question.event.RefreshHomeQuestion
import com.zzwl.question.event.RefreshQuestionDetailEvent
import com.zzwl.question.room.repository.LocationRepository
import com.zzwl.question.room.repository.QuestionRepository
import com.zzwl.question.router.RouterPage
import com.zzwl.question.ui.question.holder.PicHolder
import okhttp3.MediaType
import okhttp3.MultipartBody
import okhttp3.RequestBody
import org.greenrobot.eventbus.EventBus
import org.greenrobot.eventbus.Subscribe
import org.greenrobot.eventbus.ThreadMode
import java.io.File
import java.util.*
import java.util.concurrent.TimeUnit

/**
 * Created by qhn on 2018/1/6.
 * 解答,发布问题界面
 *   TYPE_SOLUTION = 0 //解答界面
 *   TYPE_POST_QUESTION = 1//发布问题界面
 */
@Route(path = RouterPage.PostQuestionActivity, extras = RouteExtras.NeedOauth)
class PostQuestionActivity : BaseActivity<QuestionActivityPostQuestionBinding>(), PopupWindow.OnDismissListener {
    companion object {
        const val MIN_CLICK_DELAY_TIME = 1000
        const val TYPE_POST_QUESTION = 1//发布问题界面
        const val TYPE_SOLUTION = 0 //解答界面
    }

    val adapter by lazy { setupRecyclerView(contentView.recyclerView) }
    override var hasToolbar: Boolean = true
    private lateinit var popupWindow: PopupWindow
    private var list = ArrayList<String>()//图片数据集
    private var isShow: Boolean = false//recyclerview显示状态
    private var expertId: String = ""
    private var expertName: String = ""
    private val locationRepository by lazy { LocationRepository() }
    private val questionRepository by lazy { QuestionRepository() }
    private val locationManager by lazy { (getSystemService(Context.LOCATION_SERVICE)) as LocationManager }
    private var listener: LocationListener? = null
    private var pictureId = ""
    private val progressDialog by lazy { ProgressDialog() }
    private var lastClickTime: Long = 0
    private var location = ""
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.question_activity_post_question)
        contentView.data = this
        showContentView()
        initView()
    }

    private fun initView() {
        //发表解答没有4个view
        if (TYPE_SOLUTION == intent.getIntExtra("type", 0)) {
            contentView.tvInvoiceTitle.visibility = View.GONE
            contentView.tvInvite.visibility = View.GONE
        } else if (TYPE_POST_QUESTION == intent.getIntExtra("type", 0)) {
            contentView.tvInvoiceTitle.visibility = View.VISIBLE
            contentView.tvInvite.visibility = View.VISIBLE
            contentView.tvAnswer.text = "添加问题具体描述（必填），请尽量简洁明了的描述问题，为了给大家提供更好的问答体验，禁止无意义的灌水和敏感信息。"
            getLocation()
        }

        toolbar.inflateMenu(R.menu.confirm_text)
        toolbar.title = "输入内容"
        toolbar.menu.getItem(0).title = "发布"
        toolbar.setOnMenuItemClickListener {
            val currentTime = Calendar.getInstance().timeInMillis
            if (currentTime - lastClickTime > MIN_CLICK_DELAY_TIME) {
                lastClickTime = currentTime

                if (contentView.tvHint.text.toString().trim().isEmpty()) {
                    toast("内容不能为空")
                    return@setOnMenuItemClickListener true
                } else if (contentView.tvHint.text.toString().trim().length < 5) {
                    toast("内容不能少于5个字")
                    return@setOnMenuItemClickListener true
                }
                if (TYPE_SOLUTION == intent.getIntExtra("type", 0)) {
                    if (list.isNotEmpty())
                        uploadImg(false)
                    else
                        postComment()

                } else if (TYPE_POST_QUESTION == intent.getIntExtra("type", 0)) {
                    if (list.isNotEmpty())
                        uploadImg(true)
                    else
                        postQuestion()
                }
            }
            return@setOnMenuItemClickListener true
        }
        toolbar.setTitleTextColor(Color.WHITE)
        toolbar.setNavigationIcon(R.drawable.ic_arrow_back_toolbar_24dp)
        toolbar.setNavigationOnClickListener {
            finish()
        }
        inhibitingInput(contentView.tvHint)

    }


    fun openPopupWindow() {
        //设置PopupWindow
        val view1: View = LayoutInflater.from(this).inflate(R.layout.question_item_popup_window2, null)
        popupWindow = PopupWindow(view1, RelativeLayout.LayoutParams.MATCH_PARENT, RelativeLayout.LayoutParams.WRAP_CONTENT)
        popupWindow.softInputMode = WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE
        popupWindow.setBackgroundDrawable(BitmapDrawable())
        popupWindow.isFocusable = true
        popupWindow.isOutsideTouchable = true
        popupWindow.animationStyle = R.style.PopupWindow
        popupWindow.showAtLocation(view1, Gravity.BOTTOM, 0, 0)
        popupWindow.setOnDismissListener(this)
        backgroundAlpha(0.7f)
    }

    // 解答
    @SuppressLint("CheckResult")
    private fun postComment() {
        val params = arrayListOf<MultipartBody.Part>()

        params.add(MultipartBody.Part.createFormData("questionId", intent.getIntExtra("id", 0).toString()))
        params.add(MultipartBody.Part.createFormData("content", contentView.tvHint.text.toString()))
        if (list.isNotEmpty())
            params.add(MultipartBody.Part.createFormData("imageIdList", pictureId))

        apiProvider.create(QuestionApi::class.java)
                .uploadComment(params.toTypedArray())
                .throttleFirst(1L, TimeUnit.SECONDS)
                .mainIoSchedulers()
                .subscribe(
                        {
                            EventBus.getDefault().post(RefreshQuestionDetailEvent(true))
                            EventBus.getDefault().post(RefreshCommentAmount(intent.getIntExtra("id", 0)))
                            finish()
                        }
                        ,
                        {
                            progressDialog.setFiled(it.message
                                    ?: "发布解答失败", { progressDialog.dismiss() })
                        })
    }

    //上传图片到文件服务器
    @SuppressLint("CheckResult")
    private fun uploadImg(isPostQuestion: Boolean) {
        progressDialog.setStart("正在发布问题,请稍候~~")
        val params = list.map {
            MultipartBody.Part.createFormData("fileArray", it, RequestBody.create(MediaType.parse("multipart/form-data"), File(it)))
        } as ArrayList
        apiProvider.create(QuestionApi::class.java)
                .uploadImg(params.toTypedArray())
                .mainIoSchedulers()
                .subscribe(
                        {
                            pictureId = it.content?.map { it.id }?.joinToString(",") ?: ""
                            if (isPostQuestion)
                                postQuestion()
                            else
                                postComment()
                        },
                        {
                            progressDialog.setFiled(it.message
                                    ?: "上传图片失败", { progressDialog.dismiss() })
                        })
    }

    //    发布问题
    @SuppressLint("CheckResult")
    private fun postQuestion() {
        val params = arrayListOf<MultipartBody.Part>()
        if (expertId.isNotEmpty()) {
            params.add(MultipartBody.Part.createFormData("expertIdList", expertId))
        }
        params.add(MultipartBody.Part.createFormData("content", contentView.tvHint.text.toString()))
        params.add(MultipartBody.Part.createFormData("location", location))
        if (list.isNotEmpty())
            params.add(MultipartBody.Part.createFormData("imageIdList", pictureId))
        questionRepository.postQuestion(params.toTypedArray())
                .resultNotNull()
                .observeEx(this, {
                    when (it.status) {
                        Status.Content -> {
                            progressDialog.dismiss()
                            EventBus.getDefault().post(RefreshHomeQuestion(it.data!!))
                            finish()
                        }
                        Status.Error -> {
                            progressDialog.setFiled(it.error?.message ?: "发布问题失败") { it.dismiss() }
                        }
                        Status.Oauth -> {
                            progressDialog.dismiss()
                            showNeedOauth()
                        }
                    }
                })

    }


    fun startInvitationActivity() {
        ARouter.getInstance().build(RouterPage.InvitationActivity).withObject("checkData", beforeCheckData).navigation(this)
    }

    //更新recyclerview
    private fun bindRecycleView() {
        adapter.renderItems.clear()
        adapter.notifyDataSetChanged()
        val gridLayoutHelper = GridLayoutHelper(4, (list.size) + 1)
        gridLayoutHelper.hGap = 4.dp()
        gridLayoutHelper.setAutoExpand(false)
        adapter.layoutHelpers = arrayListOf(gridLayoutHelper as LayoutHelper)
        list.forEachIndexed { index, s ->
            adapter.renderItems.add(PicHolder(s, true, isPostPic = true).apply {
                setOnClickListener { view ->
                    when (view.id) {
                        R.id.imgCancel -> {
                            list.removeAt(index)
                            bindRecycleView()
                            if (adapter.renderItems.size == 1) {
                                removeAddImg(0)
                                if (isShow)
                                    changeView(false)
                            }
                        }
                    }
                }
            })
        }
        if ((list.size) < 3)
            adapter.renderItems.add(PicHolder(null,
                    drawable = resources.getDrawable(R.drawable.question_add_img),
                    clickable = true
                    , isPostPic = true)
                    .apply {
                        setOnClickListener {
                            when (it.id) {
                                R.id.imgPic -> {
                                    openPopupWindow()
                                }
                            }
                        }
                    })
    }

    override fun onDismiss() {
        backgroundAlpha(1f)
    }

    fun onPopupViewClick(view: View) {
        when (view.id) {
            R.id.tvCamera -> {
                startCamera()
                popupWindow.dismiss()
            }
            R.id.tvGallery -> {
                startGallery()
                popupWindow.dismiss()
            }
            R.id.tvCancel -> {
                popupWindow.dismiss()
            }
        }
    }

    /**
     *  @param isShow true=显示recyclerview
     */
    private fun changeView(isShow: Boolean) {
        if (isShow) {
            this.isShow = true
            contentView.viewAddImg.visibility = View.GONE
            contentView.tvAddImg.visibility = View.GONE
            contentView.recyclerView.visibility = View.VISIBLE
        } else {
            this.isShow = false
            contentView.viewAddImg.visibility = View.VISIBLE
            contentView.tvAddImg.visibility = View.VISIBLE
            contentView.recyclerView.visibility = View.GONE
        }
    }


    var beforeCheckData: HashMap<String, String>? = null
    @Subscribe(threadMode = ThreadMode.MAIN)
    fun onExpertEvent(expertIdEvent: ExpertIdEvent) {
        expertId = expertIdEvent.hashMap?.keys?.joinToString(",") ?: ""
        expertName = expertIdEvent.hashMap?.values?.joinToString(",") ?: ""
        beforeCheckData = expertIdEvent.hashMap
        if (expertId.isNotEmpty()) {
            contentView.tvInvite.visibility = View.GONE
            contentView.tvExperts.visibility = View.VISIBLE
            contentView.tvUpdateInvite.visibility = View.VISIBLE
            contentView.tvExperts.text = expertName
        } else {
            contentView.tvInvite.visibility = View.VISIBLE
            contentView.tvExperts.visibility = View.GONE
            contentView.tvUpdateInvite.visibility = View.GONE
        }
    }

    //删除选了的图片
    private fun removeAddImg(index: Int) {
        adapter.renderItems.removeAt(index)
        adapter.notifyItemRemoved(index)
    }

    private fun startCamera() {
        TakePhotoHelp.getInstant(this, object : TakePhoto.TakeResultListener {
            override fun takeSuccess(result: TResult) {
                if (!isShow)
                    changeView(true)
                if (list.size >= 3) return
                list.add(result.images[0].compressPath)
                bindRecycleView()
            }

            override fun takeCancel() {
            }

            override fun takeFail(result: TResult?, msg: String?) {
                toast("获取图片失败..")
            }

        }, ObservableField(1))
                .startCamera()
    }

    private fun startGallery() {
        val maxSelect: ObservableField<Int> = ObservableField(3)
        val instant = TakePhotoHelp.getInstant(this, object : TakePhoto.TakeResultListener {
            override fun takeCancel() {
            }

            override fun takeFail(result: TResult?, msg: String?) {
                toast("出现异常,请重新选取头像")

            }

            override fun takeSuccess(result: TResult) {
                if (!isShow)
                    changeView(true)
                result.images.map {
                    if (list.size >= 3) return@map
                    list.add(it.compressPath)
                }
                bindRecycleView()

            }
        }, maxSelect)
        instant.startGallery()
    }

    private fun getLocation() {
        locationRepository.getGpsLocation(this) {
            if (it == null) return@getGpsLocation
            locationRepository.getLocationAddressPegging(it.latitude, it.longitude) { city: String, province: String, district: String ->
                location = province + city + district

            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        if (listener != null)
            locationManager.removeUpdates(listener)
    }

    private fun backgroundAlpha(bgAlpha: Float) {
        val layoutParams = this.window.attributes
        layoutParams.alpha = bgAlpha
        this.window.attributes = layoutParams
    }

    override fun onTokenChange(data: TokenEntity?) {
        if (data != null)
            showContentView()
        else
            showNeedOauth()
    }
}