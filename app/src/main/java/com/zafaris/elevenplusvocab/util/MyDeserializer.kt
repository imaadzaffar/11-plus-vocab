package com.zafaris.elevenplusvocab.util

import android.util.Log
import com.google.gson.*
import com.zafaris.elevenplusvocab.data.model.Word
import com.zafaris.elevenplusvocab.data.model.Meaning
import org.json.JSONException
import java.lang.reflect.Type
import kotlin.jvm.Throws

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