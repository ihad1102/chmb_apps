package com.zzwl.question.event

import com.zzwl.question.room.entity.remote.QuestionEntity


data class RefreshHomeQuestion(val questionEntity: QuestionEntity)