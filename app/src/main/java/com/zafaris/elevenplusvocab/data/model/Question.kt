package com.zafaris.elevenplusvocab.data.model

data class Question(
    var word: String,
    var type: String,
    var example: String,
    var questionType: Int, // 0 = synonym, 1 = antonym
    var option1: String,
    var option2: String,
    var option3: String,
    var option4: String,
    var answerNo: Int,
    var userAnswerNo: Int = 0,
    var isAnswered: Boolean = false
)