package com.zafaris.elevenplusvocab.ui.test

import android.app.Application
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import com.zafaris.elevenplusvocab.data.database.WordBankDbAccess
import com.zafaris.elevenplusvocab.data.model.Meaning
import com.zafaris.elevenplusvocab.data.model.Question
import com.zafaris.elevenplusvocab.data.model.Word
import com.zafaris.elevenplusvocab.util.NO_OF_OPTIONS
import com.zafaris.elevenplusvocab.util.NO_OF_QUESTIONS
import com.zafaris.elevenplusvocab.util.SET_SIZE

class TestViewModel(application: Application) : AndroidViewModel(application) {
	var setNo = 0
	var wordsList: List<Word> = ArrayList()

	var score = 0
	var questionNo = 0
	var answerWord = ""
	var answeredState = false
	var completedState = false
	var answerIndexList: MutableList<Int> = ArrayList()
	var questionsList: MutableList<Question> = ArrayList()
	lateinit var randomWord: Word
	lateinit var randomMeaning: Meaning

	fun getWords() {
		val db = WordBankDbAccess.getInstance(getApplication())
		db.open()
		wordsList = db.getWordsList(setNo)
		db.close()
	}

	private fun generateAnswerIndexList(): MutableList<Int> { // end = 25, size = 10
		val tmpList: MutableList<Int> = ArrayList()
		for (i in 0 until SET_SIZE) {
			tmpList.add(i)
		}
		tmpList.shuffle()
		return tmpList.subList(0, NO_OF_QUESTIONS)
	}

	private fun generateQuestionTypeList(): List<Int> {
		val randomList: MutableList<Int> = ArrayList()
		for (i in 0 until NO_OF_QUESTIONS) {
			randomList.add((0..1).random())
		}
		return randomList
	}

	fun generateAllQuestions() {
		questionsList = ArrayList()

		var tmpQuestionNo = 1
		answerIndexList = generateAnswerIndexList()
		val questionTypeList = generateQuestionTypeList()

		// for each word index in random list of indexes, generate a question
		for (answerIndex in answerIndexList) {
			val questionType = questionTypeList[tmpQuestionNo - 1]
			Log.d("Test - tmpQuestionNo", tmpQuestionNo.toString())
			val answerNo = 1 + (Math.random() * NO_OF_OPTIONS).toInt()
			Log.d("Test - answerNo", answerNo.toString())

			// generate correct answer
			answerWord = generateAnswer(tmpQuestionNo, questionType)
			Log.d("Test - word", randomWord.word)
			Log.d("Test - type", randomWord.type)
			Log.d("Test - example", randomMeaning.example)
			Log.d("Test - questionType", questionType.toString())
			val optionsList = generateOptionsList(answerIndex, answerNo, questionType)
			Log.d("Test - option1", optionsList[0])
			Log.d("Test - option2", optionsList[1])
			Log.d("Test - option3", optionsList[2])
			Log.d("Test - option4", optionsList[3])

			//TODO: save indices to a list and generate unique words
			val tmpQuestion = Question(
					randomWord.word,
					randomWord.type,
					randomMeaning.example,
					questionType,
					optionsList[0],
					optionsList[1],
					optionsList[2],
					optionsList[3],
					answerNo
			)
			questionsList.add(tmpQuestion)
			tmpQuestionNo++
		}
	}

	private fun generateAnswer(questionNo: Int, questionType: Int): String {
		var answer = "N/A"
		var answerIndex = answerIndexList[questionNo - 1]
		var emptyWord = true
		while (emptyWord) {
			val tmpWord = wordsList[answerIndex]
			val tmpMeaningList = tmpWord.meanings
			var tmpMeaning: Meaning
			tmpMeaning = if (tmpMeaningList.size > 1) {
				val tmpMeaningIndex = (Math.random() * tmpMeaningList.size).toInt()
				tmpMeaningList[tmpMeaningIndex]
			} else {
				tmpMeaningList[0]
			}

			answer = if (questionType == 0) {
				val synonymsList: Array<String> = tmpMeaning.synonyms.split(", ").toTypedArray()
				val tmpSynonymIndex = (Math.random() * synonymsList.size).toInt()
				synonymsList[tmpSynonymIndex]
			} else {
				val antonymsList: Array<String> = tmpMeaning.antonyms.split(", ").toTypedArray()
				val tmpAntonymIndex = (Math.random() * antonymsList.size).toInt()
				antonymsList[tmpAntonymIndex]
			}
			Log.d("Test - answer", answer)

			// check if answer
			if (answer == "N/A") {
				do {
					answerIndex = (Math.random() * SET_SIZE).toInt()
					Log.d("Test - answerIndex loop", answerIndexList.contains(answerIndex).toString())
				} while (answerIndexList.contains(answerIndex))
				answerIndexList[questionNo - 1] = answerIndex
			} else {
				randomWord = tmpWord
				randomMeaning = tmpMeaning
				emptyWord = false
			}
		}
		Log.d("Test - answer escaped", answer)
		return answer
	}

	private fun generateOptionsList(answerIndex: Int, answerNo: Int, questionType: Int): List<String> {
		val tmpWordsList: MutableList<String> = ArrayList()
		var word = "N/A"
		for (i in 1..NO_OF_OPTIONS) {
			var uniqueWord = false

			while (!uniqueWord) {

				// answer number
				if (i == answerNo) {
					word = answerWord
					uniqueWord = true
					Log.d("Test - correct word", uniqueWord.toString())
				} else {
					word = generateOption(answerIndex, questionType)

					// checks if word is unique, breaks while loop
					if (!tmpWordsList.contains(word)) {
						uniqueWord = true
					} else {
						word = "N/A"
					}
					Log.d("Test - generateOption", uniqueWord.toString())
				}
			}

			Log.d("Test - Word", word)
			tmpWordsList.add(word)
		}
		return tmpWordsList
	}

	private fun generateOption(answerIndex: Int, questionType: Int): String {
		var option = "N/A"
		var tmpList: Array<String>
		var emptyWord = true
		while (emptyWord) {
			var tmpWordIndex: Int

			// generate random index, that is not the same as the answer index
			do {
				tmpWordIndex = (Math.random() * SET_SIZE).toInt()
			} while (tmpWordIndex == answerIndex)
			val tmpMeanings = wordsList[tmpWordIndex].meanings
			var tmpMeaning: Meaning

			tmpMeaning = if (tmpMeanings.size > 1) {
				val tmpMeaningIndex = (Math.random() * tmpMeanings.size).toInt()
				tmpMeanings[tmpMeaningIndex]
			} else {
				tmpMeanings[0]
			}

			tmpList =
				// synonym
				if (questionType == 0) {
					tmpMeaning.synonyms.split(", ").toTypedArray()
					// antonym
				} else {
					tmpMeaning.antonyms.split(", ").toTypedArray()
				}
			val tmpOptionIndex = (Math.random() * tmpList.size).toInt()
			option = tmpList[tmpOptionIndex]
			if (option != "N/A") {
				emptyWord = false
			}
		}
		return option
	}
}