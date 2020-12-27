package com.zafaris.elevenplusvocab.ui.learn

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import com.zafaris.elevenplusvocab.data.database.WordBankDbAccess
import com.zafaris.elevenplusvocab.data.model.Word

class LearnViewModel(application: Application) : AndroidViewModel(application) {
	var wordsList: List<Word> = ArrayList()
	var setNo = 0

	fun getWords(): List<Word> {
		// open database and get wordsList
		val db = WordBankDbAccess.getInstance(getApplication())
		db.open()
		wordsList = db.getWordsList(setNo)
		db.close()
		return wordsList
	}

	fun getWordsListSize(): Int = wordsList.size

}