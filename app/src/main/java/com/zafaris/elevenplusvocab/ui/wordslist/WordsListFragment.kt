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
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.zafaris.elevenplusvocab.HomeGraphDirections
import com.zafaris.elevenplusvocab.R
import com.zafaris.elevenplusvocab.data.database.WordBankDbAccess
import com.zafaris.elevenplusvocab.data.model.Set
import com.zafaris.elevenplusvocab.data.model.Word
import com.zafaris.elevenplusvocab.databinding.FragmentWordslistBinding
import com.zafaris.elevenplusvocab.databinding.HomeDialogSetLockedBinding
import com.zafaris.elevenplusvocab.databinding.HomeDialogSetUnlockedBinding
import com.zafaris.elevenplusvocab.databinding.WordslistDialogWordBinding
import com.zafaris.elevenplusvocab.ui.home.HomeFragmentDirections
import com.zafaris.elevenplusvocab.util.NO_OF_FREE_SETS
import com.zafaris.elevenplusvocab.util.NO_OF_TOTAL_SETS
import com.zafaris.elevenplusvocab.util.SET_SIZE
import java.util.*

class WordsListFragment : Fragment(), WordsListAdapter.OnItemClickListener {
	private var _binding: FragmentWordslistBinding? = null
	private var _setUnlockedBinding: HomeDialogSetUnlockedBinding? = null
	private var _setLockedBinding: HomeDialogSetLockedBinding? = null
	private var _wordDialogBinding: WordslistDialogWordBinding? = null
	private val binding get() = _binding!!
	private val setUnlockedBinding get() = _setUnlockedBinding!!
	private val setLockedBinding get() = _setLockedBinding!!
	private val wordDialogBinding get() = _wordDialogBinding!!

	private lateinit var db: WordBankDbAccess
	private var clickedSetNo = 0

	private lateinit var setDialog: Dialog
	private lateinit var wordDialog: Dialog
	private lateinit var mediaPlayer: MediaPlayer
	
	private lateinit var itemsList: MutableList<Any>
	private lateinit var adapter: WordsListAdapter
	private lateinit var manager: LinearLayoutManager

	override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
		_binding = FragmentWordslistBinding.inflate(inflater, container, false)
		val view = binding.root

		_setUnlockedBinding = HomeDialogSetUnlockedBinding.inflate(inflater)
		_setLockedBinding = HomeDialogSetLockedBinding.inflate(inflater)
		_wordDialogBinding = WordslistDialogWordBinding.inflate(inflater)

		setDialog = Dialog(requireContext())
		wordDialog = Dialog(requireContext())

		return view
	}

	override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
		generateDummyList()
		buildRv()
	}

	override fun onDestroyView() {
		super.onDestroyView()
		_binding = null
		_setUnlockedBinding = null
		_setLockedBinding = null
		_wordDialogBinding = null
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
		binding.rvItems.layoutManager = manager
		binding.rvItems.adapter = adapter
	}

	override fun onItemWordClick(word: Word, position: Int) {
		playMenuClickSound()
		showWordDialog(word)
	}

	private fun showWordDialog(word: Word) {
		wordDialog.setContentView(wordDialogBinding.root)

		wordDialogBinding.textId.text = word.id.toString()
		wordDialogBinding.textWord.text = word.word
		wordDialogBinding.textType.text = word.type

		wordDialogBinding.buttonAudio.setOnClickListener { v ->
			//TODO: Add audio sound for word
			Toast.makeText(v.context, "Play audio for word", Toast.LENGTH_SHORT).show()
		}

		wordDialogBinding.textDefinition.text = word.meanings[0].definition
		wordDialogBinding.textExample.text = word.meanings[0].example

		val synonyms = word.meanings[0].synonyms
		if (synonyms == "N/A") {
			wordDialogBinding.cardSynonyms.visibility = View.INVISIBLE
		} else {
			val size = synonyms.split(", ").size
			wordDialogBinding.cardSynonyms.visibility = View.VISIBLE
			wordDialogBinding.titleSynonyms.text = "Synonyms ($size):"
			wordDialogBinding.textSyononyms.text = word.meanings[0].synonyms
		}
		val antonyms = word.meanings[0].antonyms
		if (antonyms == "N/A") {
			wordDialogBinding.cardAntonyms.visibility = View.INVISIBLE
		} else {
			val size = antonyms.split(", ").size
			wordDialogBinding.cardAntonyms.visibility = View.VISIBLE
			wordDialogBinding.titleAntonyms.text = "Antonyms ($size):"
			wordDialogBinding.textAntonyms.text = word.meanings[0].antonyms
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
				showLockedDialog()
			}
			else -> {
				showUnlockedDialog()
			}
		}
	}

	private fun showUnlockedDialog() {
		setDialog.setContentView(setUnlockedBinding.root)
		setUnlockedBinding.titleDialog.text = "Set $clickedSetNo"
		setUnlockedBinding.buttonLearn.setOnClickListener { navigateAction("learn") }
		setUnlockedBinding.buttonTest.setOnClickListener { navigateAction("test") }
		setUnlockedBinding.buttonStats.setOnClickListener { navigateAction("stats") }
		setDialog.window!!.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
		setDialog.show()
	}

	private fun showLockedDialog() {
		setDialog.setContentView(setLockedBinding.root)
		setLockedBinding.titleDialog.text = "Set $clickedSetNo locked"
		setLockedBinding.buttonUnlock.setOnClickListener {
			Toast.makeText(context, "Unlock sets", Toast.LENGTH_SHORT).show()
			//TODO: Add payment
		}
		setLockedBinding.buttonDismiss.setOnClickListener {
			setDialog.dismiss()
		}
		setDialog.window!!.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
		setDialog.show()
	}

	private fun navigateAction(destination: String) {
		playMenuClickSound()
		manager.smoothScrollToPosition(binding.rvItems, null, 0)
		setDialog.dismiss()

		val setNo = clickedSetNo
		val action = when (destination) {
			"learn" -> HomeGraphDirections.actionGlobalLearnFragment(setNo)
			"test" -> HomeFragmentDirections.actionGlobalTestFragment(setNo)
			"stats" -> HomeFragmentDirections.actionGlobalStatsFragment(setNo)
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