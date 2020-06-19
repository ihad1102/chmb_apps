package com.zzwl.question.viewModel

import android.arch.lifecycle.MutableLiveData
import com.g.base.extend.switchMap
import com.g.base.viewModel.BaseViewModel
import com.zzwl.question.room.repository.QuestionRepository


/**
 * Created by qhn on 2018/1/5.
 */
class QuestionDetailViewModel : BaseViewModel() {
    private val questionRepository by lazy { QuestionRepository() }
    private val obsQuestionDetail by lazy { MutableLiveData<Pair<String?, Int>>() }
    private val commentListLiveData by lazy { MutableLiveData<Pair<Int, Int>>() }

    fun getCommentList() = questionRepository.getCommentList()
    fun operateQuestionDetailObs(currentUserId: String?, id: Int = 10) {
        obsQuestionDetail.value = Pair(currentUserId, id)
    }


    fun getQuestionDetail() =
            obsQuestionDetail.switchMap { questionRepository.getQuestionDetail(it.second,it.first) }

}