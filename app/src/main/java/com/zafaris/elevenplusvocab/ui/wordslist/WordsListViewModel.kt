package com.zafaris.elevenplusvocab.ui.wordslist

import android.app.Application
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import com.zafaris.elevenplusvocab.data.database.WordBankDbAccess
import com.zafaris.elevenplusvocab.data.model.Set
import com.zafaris.elevenplusvocab.data.model.Word
import com.zafaris.elevenplusvocab.util.NO_OF_FREE_SETS
import com.zafaris.elevenplusvocab.util.NO_OF_TOTAL_SETS
import com.zafaris.elevenplusvocab.util.SET_SIZE
import java.util.ArrayList

class WordsListViewModel(application: Application) : AndroidViewModel(application) {
	var itemsList: MutableList<Any> = ArrayList()
	var clickedSetNo: Int = 0

	fun generateDummyList(): MutableList<Any> {
		itemsList = ArrayList()

		val db = WordBankDbAccess.getInstance(getApplication())
		db.open()

		val noOfSets = db.getNoOfSets(SET_SIZE)

		for (i in 1..NO_OF_TOTAL_SETS) {
			if (i <= NO_OF_FREE_SETS) {
				itemsList.add(Set(i))
			} else {
				itemsList.add(Set(i, isSetLocked = true))
			}
			try {
				val wordsList = db.getWordsList(i)
				itemsList.addAll(wordsList)
			} catch (e: Exception) {
				val wordsList = MutableList(SET_SIZE) { index ->
					Word(
							(i - 1) * SET_SIZE + (index + 1),
							i,
							"example",
							"",
							emptyList()
					)
				}
				itemsList.addAll(wordsList)
			}
			Log.d("listRecyclerView", itemsList.toString())
		}

		db.close()

		return itemsList
	}
}