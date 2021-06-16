package com.zafaris.learnvocab.data.model

data class Set(
    val setNo: Int,
    var isSetCompleted: Boolean = false,
    var isSetLocked: Boolean = false
)