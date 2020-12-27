package com.zafaris.elevenplusvocab.ui.home

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import com.zafaris.elevenplusvocab.data.database.WordBankDbAccess
import com.zafaris.elevenplusvocab.data.model.Set
import com.zafaris.elevenplusvocab.data.model.Word
import com.zafaris.elevenplusvocab.util.NO_OF_FREE_SETS
import com.zafaris.elevenplusvocab.util.NO_OF_TOTAL_SETS
import com.zafaris.elevenplusvocab.util.SET_SIZE
import java.util.ArrayList

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