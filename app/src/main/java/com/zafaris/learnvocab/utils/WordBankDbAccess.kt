package com.zafaris.`11plusvocab`.utils

import android.content.Context
import android.database.Cursor
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import android.util.Log
import com.zafaris.`11plusvocab`.ui.learn.Meaning
import com.zafaris.learnvocab.utils.WordBankContract.WordBank
import java.util.*

class WordBankDbAccess private constructor(context: Context) {
    private val dbHelper: SQLiteOpenHelper
    private lateinit var db: SQLiteDatabase
    private lateinit var c: Cursor

    // to open the database
    fun open() {
        db = dbHelper.writableDatabase
    }

    // to close the database connection
    fun close() {
        db.close()
    }

    // query for word info by passing word number
    fun getWordsList(setNumber: Int): List<Word> {
        val wordsList: MutableList<Word> = ArrayList()
        c = db.rawQuery("SELECT * FROM " + WordBank.TABLE_NAME +
                " WHERE " + WordBank.COLUMN_SET + " = " + setNumber, null) //TODO: Change for different setSize
        c.moveToFirst()
        do {
            val id = c.getInt(c.getColumnIndex(WordBank.COLUMN_ID))
            val word = c.getString(c.getColumnIndex(WordBank.COLUMN_WORD)) // set word
            val type = c.getString(c.getColumnIndex(WordBank.COLUMN_TYPE)) // set type

            Log.i("DbAccess - Word", word)
            Log.i("DbAccess - Type", type)
            val definitions = c.getString(c.getColumnIndex(WordBank.COLUMN_DEFINITION))
            val definitionsList: List<String> = ArrayList(listOf(*definitions.split(" \\+ ").toTypedArray())) // split definitions string to list
            Log.i("DbAccess - Definitions", definitionsList.toString())
            val examples = c.getString(c.getColumnIndex(WordBank.COLUMN_EXAMPLE))
            val examplesList: List<String> = ArrayList(listOf(*examples.split(" \\+ ").toTypedArray())) // split examples string to list
            Log.i("DbAccess - Examples", examplesList.toString())
            val synonyms = c.getString(c.getColumnIndex(WordBank.COLUMN_SYNONYMS))
            val synonymsList: List<String> = ArrayList(listOf(*synonyms.split(" \\+ ").toTypedArray())) // split synonyms string to list
            Log.i("DbAccess - Synonyms", synonymsList.toString())
            val antonyms = c.getString(c.getColumnIndex(WordBank.COLUMN_ANTONYMS))
            val antonymsList: List<String> = ArrayList(listOf(*antonyms.split(" \\+ ").toTypedArray())) // split antonyms string to list
            Log.i("DbAccess - Antonyms", antonymsList.toString())

            val meaningsList: MutableList<Meaning> = ArrayList() // list of meanings
            for (i in definitionsList.indices) { // add meanings to list
                val tmpMeaning = Meaning(
                        definitionsList[i],
                        examplesList[i],
                        synonymsList[i],
                        antonymsList[i]
                )
                meaningsList.add(tmpMeaning)
            }

            val tmpWord = Word(
                    id = id,
                    set = setNumber,
                    word = word,
                    type = type,
                    meanings = meaningsList
            )

            wordsList.add(tmpWord)
        } while (c.moveToNext())
        return wordsList
    }

    fun getNoOfSets(setSize: Int): Int {
        c = db.rawQuery("SELECT * FROM " + WordBank.TABLE_NAME, null)
        c.moveToLast()
        val lastWordNo = c.getInt(c.getColumnIndex(WordBank.COLUMN_NUMBER))
        Log.i("setSize", setSize.toString())
        Log.i("lastWordNo", lastWordNo.toString())
        return lastWordNo / setSize
    }

    companion object {
        private lateinit var instance: WordBankDbAccess

        // to return the single instance of the database
        fun getInstance(context: Context): WordBankDbAccess {
            instance = WordBankDbAccess(context)
            return instance
        }
    }

    // private constructor so that object creation from outside the class is avoided
    init {
        dbHelper = WordBankDbHelper(context)
    }
}