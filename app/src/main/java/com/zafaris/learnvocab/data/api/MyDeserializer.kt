package com.zafaris.learnvocab.data.api

import android.util.Log
import com.google.gson.JsonDeserializationContext
import com.google.gson.JsonDeserializer
import com.google.gson.JsonElement
import com.zafaris.learnvocab.data.model.Meaning
import com.zafaris.learnvocab.data.model.Word
import org.json.JSONException
import java.lang.reflect.Type

class MyDeserializer : JsonDeserializer<Word> {

    @Throws(JSONException::class)
    override fun deserialize(json: JsonElement?, typeOfT: Type?, context: JsonDeserializationContext?): Word {
        Log.i("Deserializer", json.toString())

        val jsonObject = json?.asJsonObject

        val meaningsList: MutableList<Meaning> = ArrayList()
        val word = Word(1, 1, "ace", "noun", meaningsList)

        return word
    }
}