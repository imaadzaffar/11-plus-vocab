package com.zafaris.learnvocab.mvvm.model

import androidx.lifecycle.MutableLiveData
import com.google.gson.GsonBuilder
import com.zafaris.`11plusvocab`.utils.Word
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object RetrofitService {

    val currentWord: MutableLiveData<Word> = MutableLiveData()

    fun getOxfordAPI(): OxfordAPI {
        val deserializer = MyDeserializer()
        val gson = GsonBuilder().registerTypeAdapter(Word::class.java, deserializer).create()

        val retrofit = Retrofit.Builder()
                .baseUrl("https://od-api.oxforddictionaries.com:443/api/v2/entries/en-gb/")
                .addConverterFactory(GsonConverterFactory.create(gson))
                .build()

        return retrofit.create(OxfordAPI::class.java)
    }

}