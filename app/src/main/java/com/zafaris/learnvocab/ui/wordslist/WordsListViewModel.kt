package com.zafaris.learnvocab.ui.wordslist

import android.app.Application
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import com.qonversion.android.sdk.dto.products.QProduct
import com.zafaris.learnvocab.data.database.WordBankDbAccess
import com.zafaris.learnvocab.data.model.Set
import com.zafaris.learnvocab.data.model.Word
import com.zafaris.learnvocab.util.NO_OF_FREE_SETS
import com.zafaris.learnvocab.util.NO_OF_TOTAL_SETS
import com.zafaris.learnvocab.util.SET_SIZE
import java.util.*

class WordsListViewModel(application: Application) : AndroidViewModel(application) {
	//	var currentPackage: Package? = null
	var mainProduct: QProduct? = null
	var itemsList: MutableList<Any> = ArrayList()
	var sets: MutableList<Set> = ArrayList()
	var clickedSetNo: Int = 0

	fun generateDummyList(): MutableList<Any> {
		itemsList = ArrayList()

		val db = WordBankDbAccess.getInstance(getApplication())
		db.open()

		val noOfSets = db.getNoOfSets(SET_SIZE)

		for (i in 1..NO_OF_TOTAL_SETS) {
			if (i <= NO_OF_FREE_SETS) {
				sets.add(Set(i))
				itemsList.add(Set(i))
			} else {
				sets.add(Set(i, isSetLocked = true))
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