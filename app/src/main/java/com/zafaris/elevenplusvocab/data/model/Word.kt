package com.zafaris.elevenplusvocab.data.model

data class Word (
    var id: Int,
    var set: Int,
    var word: String,
    var type: String,
    var meanings: List<Meaning>
)

data class Meaning (
	var definition: String,
	var example: String,
	var synonyms: String,
	var antonyms: String
)