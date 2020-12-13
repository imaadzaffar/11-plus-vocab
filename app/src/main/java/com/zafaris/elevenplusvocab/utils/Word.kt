package com.zafaris.elevenplusvocab.utils

import com.zafaris.elevenplusvocab.ui.learn.Meaning

data class Word (
    var id: Int,
    var set: Int,
    //var number: Int,
    var word: String,
    var type: String,
    var meanings: List<Meaning>
)