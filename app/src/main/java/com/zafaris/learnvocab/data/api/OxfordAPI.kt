package com.zafaris.learnvocab.data.api

import com.zafaris.learnvocab.data.model.Word
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Path

interface OxfordAPI {

    @GET("{word_id}?fields=pronunciations&strictMatch=true")
    fun getWord(@Path("word_id") wordId: String) : Call<Word>

}