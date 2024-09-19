package com.app.tensquare.ui.questionbank

import com.app.tensquare.base.ComingSoon

data class QuestionBankResponse(
    val `data`: TestQuestionList,
    val message: String,
    val status: String
)

data class TestQuestionList(
    val duration: String,
    val questionList: List<Question>,
    val comingSoon: ComingSoon
)

data class Question(
    val _id: String,
    val difficultyLevel: String,
    val name: String,
    val option1: String,
    val option2: String,
    val option3: String,
    val option4: String,

)

data class TestAnswerSheet(
    val _id: String,
    val answerDetails: String,
    val correct: String,
    val name: String,
    val option1: String,
    val option2: String,
    val option3: String,
    val option4: String,

    val userChooseOption: String
)

/*
data class Question(
    val __v: Int,
    val _id: String,
    val answerDetails: String,
    val answerType: String,
    val chapterId: String,
    val chapterName: String,
    val classId: String,
    val className: String,
    val correct: String,
    val createdAt: String,
    val difficultyLevel: String,
    val duration: String,
    val isDeleted: Int,
    val languageId: String,
    val languageName: String,
    val name: String,
    val option1: String,
    val option2: String,
    val option3: String,
    val option4: String,
    val questionType: String,
    val status: Int,
    val subjectId: String,
    val subjectName: String,
    val topicId: String,
    val topicName: String,
    val updatedAt: String
)*/
