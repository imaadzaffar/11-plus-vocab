package com.zafaris.learnvocab.ui.home

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import com.zafaris.learnvocab.data.database.WordBankDbAccess
import com.zafaris.learnvocab.data.model.Set
import com.zafaris.learnvocab.util.NO_OF_FREE_SETS
import com.zafaris.learnvocab.util.NO_OF_TOTAL_SETS
import com.zafaris.learnvocab.util.SET_SIZE
import java.util.*

class HomeViewModel(application: Application) : AndroidViewModel(application) {
	var sets: MutableList<Set> = ArrayList()
	var clickedSetNo: Int = 0

	fun generateDummySets(): MutableList<Set> {
		val db = WordBankDbAccess.getInstance(getApplication())
		db.open()
		val noOfSets = db.getNoOfSets(SET_SIZE)
		db.close()
		sets = ArrayList()

		for (i in 1..NO_OF_TOTAL_SETS) {
			if (i <= NO_OF_FREE_SETS) {
				sets.add(Set(setNo = i))
			} else {
				sets.add(Set(setNo = i, isSetLocked = true))
			}
		}
		return sets
	}
}