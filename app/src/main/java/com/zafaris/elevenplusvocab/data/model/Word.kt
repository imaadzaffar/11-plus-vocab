package com.zafaris.elevenplusvocab.data.model

import com.zafaris.elevenplusvocab.ui.learn.Meaning

data class Word (
    var id: Int,
    var set: Int,
    var word: String,
    var type: String,
    var meanings: List<Meaning>
)