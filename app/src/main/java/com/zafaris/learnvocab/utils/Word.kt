package com.zafaris.learnvocab.utils

import com.zafaris.learnvocab.ui.learn.Meaning

data class Word (
    var id: Int,
    var set: Int,
    //var number: Int,
    var word: String,
    var type: String,
    var meanings: List<Meaning>
)