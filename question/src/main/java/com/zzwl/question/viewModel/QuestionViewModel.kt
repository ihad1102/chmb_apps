package com.zzwl.question.viewModel

import android.arch.lifecycle.LifecycleOwner
import android.arch.lifecycle.MutableLiveData
import com.g.base.extend.resultNotNull
import com.g.base.extend.switchMap
import com.g.base.extend.toObservable
import com.g.base.room.repository.ResultWarp
import com.g.base.viewModel.BaseViewModel
import com.zzwl.question.room.entity.remote.BannerREntity
import com.zzwl.question.room.entity.remote.QuestionEntity
import com.zzwl.question.room.repository.QuestionRepository
import io.reactivex.Observable
import io.reactivex.functions.BiFunction

/**
 * Created by qhn on 2018/1/2.
 */
class QuestionViewModel : BaseViewModel() {
    private val questionRepository by lazy { QuestionRepository() }
    private val operateQuestionList by lazy { MutableLiveData<QuestionData>() }
    val limit = 10
    val questionList = ArrayList<QuestionEntity?>()
    fun firstLoad(owner: LifecycleOwner): Observable<QuestionViewModelAllData> {
        operateQuestionList(1)
        val getBanner = getBanner().resultNotNull().toObservable(owner, true)
        val obsQuestionList = obsQuestionList().resultNotNull().toObservable(owner)
        return Observable.combineLatest(getBanner, obsQuestionList,
                BiFunction { resultWarp1: ResultWarp<List<BannerREntity?>?>,
                             resultWarp2: ResultWarp<List<QuestionEntity?>?> ->
                    QuestionViewModelAllData(resultWarp1, resultWarp2)
                })
    }


    fun getBanner() = questionRepository.getBanners()

    fun operateQuestionList(offset: Int, isSolved: Boolean? = null) {
        operateQuestionList.value = QuestionData(offset, limit, isSolved)
    }

    fun obsQuestionList() = operateQuestionList.switchMap { questionRepository.getQuestions(it.pageNum, it.pageSize, it.isSolved) }

}

data class QuestionData(val pageNum: Int, val pageSize: Int, val isSolved: Boolean?)
data class QuestionViewModelAllData(var banner: ResultWarp<List<BannerREntity?>?>, var questionList: ResultWarp<List<QuestionEntity?>?>)