package com.zafaris.elevenplusvocab.ui.wordslist

import android.app.Dialog
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.media.MediaPlayer
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageButton
import android.widget.TextView
import android.widget.Toast
import androidx.cardview.widget.CardView
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.zafaris.elevenplusvocab.R
import com.zafaris.elevenplusvocab.data.model.Set
import com.zafaris.elevenplusvocab.data.model.Word
import com.zafaris.elevenplusvocab.ui.home.HomeFragmentDirections
import com.zafaris.elevenplusvocab.util.NO_OF_FREE_SETS
import com.zafaris.elevenplusvocab.util.NO_OF_TOTAL_SETS
import com.zafaris.elevenplusvocab.util.SET_SIZE
import com.zafaris.elevenplusvocab.util.WordBankDbAccess
import java.util.ArrayList

class WordsListFragment : Fragment(), WordsListAdapter.OnItemClickListener {
	private lateinit var db: WordBankDbAccess

	private lateinit var recyclerView: RecyclerView
	private lateinit var manager: RecyclerView.LayoutManager
	private lateinit var adapter: WordsListAdapter
	private var clickedSetNo = 0

	private lateinit var itemsList: MutableList<Any>
	private lateinit var setDialog: Dialog
	private lateinit var wordDialog: Dialog

	private lateinit var mediaPlayer: MediaPlayer

	override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
		val view = inflater.inflate(R.layout.fragment_wordslist, container, false)

		recyclerView = view.findViewById(R.id.wordslist_recyclerView)
		setDialog = Dialog(requireContext())
		wordDialog = Dialog(requireContext())

		return view
	}

	override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
		generateDummyList()
		buildRv()
	}

	private fun generateDummyList() {
		itemsList = ArrayList()

		db = WordBankDbAccess.getInstance(requireContext())
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
	}

	private fun buildRv() {
		adapter = WordsListAdapter(itemsList, this)
		manager = LinearLayoutManager(context)
		recyclerView.layoutManager = manager
		recyclerView.adapter = adapter
	}

	override fun onItemWordClick(word: Word, position: Int) {
		playMenuClickSound()
		showWordDialog(word)
	}

	private fun showWordDialog(word: Word) {
		wordDialog.setContentView(R.layout.wordslist_dialog_word)

		val id: TextView = wordDialog.findViewById(R.id.card_idText)
		val wordText: TextView = wordDialog.findViewById(R.id.card_wordText)
		val type: TextView = wordDialog.findViewById(R.id.card_typeText)
		val audioButton: ImageButton = wordDialog.findViewById(R.id.card_audioButton)
		val definition: TextView = wordDialog.findViewById(R.id.card_definitionText)
		val example: TextView = wordDialog.findViewById(R.id.card_exampleText)
		val synonymsCard: CardView = wordDialog.findViewById(R.id.card_synonymsCard)
		val synonymsTitle: TextView = wordDialog.findViewById(R.id.card_synonymsTitle)
		val synonymsText: TextView = wordDialog.findViewById(R.id.card_synonymsText)
		val antonymsCard: CardView = wordDialog.findViewById(R.id.card_antonymsCard)
		val antonymsTitle: TextView = wordDialog.findViewById(R.id.card_antonymsTitle)
		val antonymsText: TextView = wordDialog.findViewById(R.id.card_antonymsText)

		id.text = word.id.toString()
		wordText.text = word.word
		type.text = word.type

		audioButton.setOnClickListener { v ->
			//TODO: Add audio sound for word
			Toast.makeText(v.context, "Play audio for word", Toast.LENGTH_SHORT).show()
		}

		definition.text = word.meanings[0].definition
		example.text = word.meanings[0].example

		val synonyms = word.meanings[0].synonyms
		if (synonyms == "N/A") {
			synonymsCard.visibility = View.INVISIBLE
		} else {
			val size = synonyms.split(", ").size
			synonymsCard.visibility = View.VISIBLE
			synonymsTitle.text = "Synonyms ($size):"
			synonymsText.text = word.meanings[0].synonyms
		}
		val antonyms = word.meanings[0].antonyms
		if (antonyms == "N/A") {
			antonymsCard.visibility = View.INVISIBLE
		} else {
			val size = antonyms.split(", ").size
			antonymsCard.visibility = View.VISIBLE
			antonymsTitle.text = "Antonyms ($size):"
			antonymsText.text = word.meanings[0].antonyms
		}

		wordDialog.window!!.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
		wordDialog.show()
	}

	override fun onItemSetClick(set: Set, position: Int) {
		playMenuClickSound()
		showSetDialog(set)
	}
	
	private fun showSetDialog(set: Set) {
		clickedSetNo = set.setNo
		when {
			set.isSetLocked -> {
				showPopupLocked()
			}
			set.isSetCompleted -> {
				showPopupCompleted()
			}
			else -> {
				showPopupPlay()
			}
		}
	}

	private fun showPopupLocked() {
		setDialog.setContentView(R.layout.home_dialog_set_locked)
		val popupTitle = setDialog.findViewById<TextView>(R.id.popupTitle)
		val unlockButton = setDialog.findViewById<Button>(R.id.unlockButton)
		val title = "Set $clickedSetNo locked"
		popupTitle.text = title
		unlockButton.setOnClickListener {
			Toast.makeText(context, "Unlock sets", Toast.LENGTH_SHORT).show()
			//TODO: Add payment
		}
		val noThanksButton = setDialog.findViewById<TextView>(R.id.noThanksButton)
		noThanksButton.setOnClickListener {
			setDialog.dismiss()
		}
		setDialog.window!!.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
		setDialog.show()
	}

	private fun showPopupCompleted() {
		setDialog.setContentView(R.layout.home_dialog_set_completed)
		val popupTitle = setDialog.findViewById<TextView>(R.id.popupTitle)
		val learnButton = setDialog.findViewById<Button>(R.id.learnButton)
		val testButton = setDialog.findViewById<Button>(R.id.testButton)
		val statsButton = setDialog.findViewById<Button>(R.id.statsButton)
		val title = "Set $clickedSetNo completed"
		popupTitle.text = title
		learnButton.setOnClickListener { navigateAction("learn") }
		testButton.setOnClickListener { navigateAction("test") }
		statsButton.setOnClickListener { navigateAction("stats") }
		setDialog.window!!.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
		setDialog.show()
	}

	private fun showPopupPlay() {
		setDialog.setContentView(R.layout.home_dialog_set_play)
		val popupTitle = setDialog.findViewById<TextView>(R.id.popupTitle)
		val learnButton = setDialog.findViewById<Button>(R.id.learnButton)
		val testButton = setDialog.findViewById<Button>(R.id.testButton)
		val title = "Set $clickedSetNo"
		popupTitle.text = title
		learnButton.setOnClickListener { navigateAction("learn") }
		testButton.setOnClickListener { navigateAction("test") }
		setDialog.window!!.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
		setDialog.show()
	}

	private fun navigateAction(destination: String) {
		playMenuClickSound()
		setDialog.dismiss()

		val setNo = clickedSetNo
		val action = when (destination) {
			"learn" -> WordsListFragmentDirections.actionWordsListFragmentToLearnFragment(setNo)
			"test" -> WordsListFragmentDirections.actionWordsListFragmentToTestFragment(setNo)
			"stats" -> WordsListFragmentDirections.actionWordsListFragmentToStatsFragment(setNo)
			else -> throw IllegalArgumentException("Invalid destination")
		}
		findNavController().navigate(action)
	}

	private fun playMenuClickSound() {
		mediaPlayer = MediaPlayer.create(context, R.raw.sfx_menu_click)
		if (mediaPlayer.isPlaying) {
			mediaPlayer.release()
		}
		mediaPlayer.start()
	}

}