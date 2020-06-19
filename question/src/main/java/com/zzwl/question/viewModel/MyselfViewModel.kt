package com.zzwl.question.viewModel

import android.arch.lifecycle.MutableLiveData
import com.g.base.extend.switchMap
import com.g.base.viewModel.BaseViewModel
import com.zzwl.question.room.entity.remote.MyAnswerREntity
import com.zzwl.question.room.entity.remote.QuestionEntity
import com.zzwl.question.room.repository.MyselfRepository

/**
 * Created by qhn on 2018/1/9.
 */
class MyselfViewModel : BaseViewModel() {
    private val repository by lazy { MyselfRepository() }
    private val obsMyQuestion by lazy { MutableLiveData<MyQuestion>() }
    private val obsMyAnswer by lazy { MutableLiveData<MyQuestion>() }
    private val obsReplyList by lazy { MutableLiveData<Pair<Int, Int>>() }
    lateinit var questionList: ArrayList<QuestionEntity?>
    lateinit var answerList: ArrayList<MyAnswerREntity?>
    val myQuestionLimit = 10
    val myAnswerLimit = 10
    //我的问题
    fun operateQuestionLiveData(userId: Int, offset: Int) {
        obsMyQuestion.value = MyQuestion(userId, offset, myQuestionLimit)
    }

    //    我的回复
    fun setAnswerLiveData(userId: Int, offset: Int) {
        obsMyAnswer.value = MyQuestion(userId, offset, myAnswerLimit)
    }

    //    回复列表
    fun setReplyListLiveData(offset: Int, limit: Int = 10) {
        obsReplyList.value = Pair(offset, limit)
    }

    fun obsMyQuestion() = obsMyQuestion.switchMap {
        repository.getQuestionList(it)
    }

    fun obsMyAnswer() = obsMyAnswer.switchMap {
        repository.getAnswerList(it.userId, it.pageNum, it.pageSize)
    }

    fun obsReply() = obsReplyList.switchMap {
        repository.getReplyList(it.first, it.second)
    }

}

data class MyQuestion(val userId: Int, val pageNum: Int, val pageSize: Int)
