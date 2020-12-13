package com.zafaris.`11plusvocab`.mvvm.model

import android.util.Log
import com.google.gson.*
import com.zafaris.`11plusvocab`.ui.learn.Meaning
import com.zafaris.learnvocab.utils.Word
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