package com.zafaris.elevenplusvocab.ui.home

import android.app.Dialog
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.media.MediaPlayer
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.zafaris.elevenplusvocab.R
import com.zafaris.elevenplusvocab.data.model.Set
import com.zafaris.elevenplusvocab.util.NO_OF_FREE_SETS
import com.zafaris.elevenplusvocab.util.NO_OF_TOTAL_SETS
import com.zafaris.elevenplusvocab.util.SET_SIZE
import com.zafaris.elevenplusvocab.util.WordBankDbAccess
import java.util.*

class HomeFragment : Fragment(), SetAdapter.OnItemClickListener {
    private lateinit var db: WordBankDbAccess

    private lateinit var setRv: RecyclerView
    private lateinit var setLayoutManager: GridLayoutManager
    private var clickedSetNo = 0

    private lateinit var popupDialog: Dialog
    private lateinit var popupTitle: TextView
    private lateinit var learnButton: Button
    private lateinit var testButton: Button
    private lateinit var statsButton: Button
    private lateinit var unlockButton: Button

    private lateinit var mediaPlayer: MediaPlayer

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.fragment_home, container, false)

        setRv = view.findViewById(R.id.setRv)
        popupDialog = Dialog(requireContext())

        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        generateDummySets()
        buildSetRv()
    }

    private fun generateDummySets() {
        db = WordBankDbAccess.getInstance(requireContext())
        db.open()
        val noOfSets = db.getNoOfSets(SET_SIZE)
        db.close()
        setList = ArrayList()

        for (i in 1..NO_OF_TOTAL_SETS) {
            if (i <= NO_OF_FREE_SETS) {
                setList.add(Set(setNo = i))
            } else {
                setList.add(Set(setNo = i, isSetLocked = true))
            }
        }
    }

    private fun buildSetRv() {
        setAdapter = SetAdapter(setList, this)
        setLayoutManager = GridLayoutManager(requireContext(), 2)
        setRv.layoutManager = setLayoutManager
        setRv.adapter = setAdapter
    }

    override fun onItemSetClick(set: Set) {
        playMenuClickSound()
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
        popupDialog.setContentView(R.layout.home_dialog_set_locked)
        popupTitle = popupDialog.findViewById(R.id.popupTitle)
        unlockButton = popupDialog.findViewById(R.id.unlockButton)
        val title = "Set $clickedSetNo locked"
        popupTitle.text = title
        unlockButton.setOnClickListener {
            Toast.makeText(context, "Unlock sets", Toast.LENGTH_SHORT).show()
            //TODO: Add payment
        }
        val noThanksButton = popupDialog.findViewById<TextView>(R.id.noThanksButton)
        noThanksButton.setOnClickListener {
            popupDialog.dismiss()
        }
        popupDialog.window!!.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        popupDialog.show()
    }

    private fun showPopupCompleted() {
        popupDialog.setContentView(R.layout.home_dialog_set_completed)
        popupTitle = popupDialog.findViewById(R.id.popupTitle)
        learnButton = popupDialog.findViewById(R.id.learnButton)
        testButton = popupDialog.findViewById(R.id.testButton)
        statsButton = popupDialog.findViewById(R.id.statsButton)
        val title = "Set $clickedSetNo completed"
        popupTitle.text = title
        learnButton.setOnClickListener { navigateAction("learn") }
        testButton.setOnClickListener { navigateAction("test") }
        statsButton.setOnClickListener { navigateAction("stats") }
        popupDialog.window!!.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        popupDialog.show()
    }

    private fun showPopupPlay() {
        popupDialog.setContentView(R.layout.home_dialog_set_play)
        popupTitle = popupDialog.findViewById(R.id.popupTitle)
        learnButton = popupDialog.findViewById(R.id.learnButton)
        testButton = popupDialog.findViewById(R.id.testButton)
        val title = "Set $clickedSetNo"
        popupTitle.text = title
        learnButton.setOnClickListener { navigateAction("learn") }
        testButton.setOnClickListener { navigateAction("test") }
        popupDialog.window!!.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        popupDialog.show()
    }

    private fun navigateAction(destination: String) {
        playMenuClickSound()
        setLayoutManager.smoothScrollToPosition(setRv, null, 0)
        popupDialog.dismiss()

        val action = when (destination) {
            "learn" -> HomeFragmentDirections.actionHomeFragmentToLearnFragment(clickedSetNo)
            "test" -> HomeFragmentDirections.actionHomeFragmentToTestFragment(clickedSetNo)
            "stats" -> HomeFragmentDirections.actionHomeFragmentToStatsFragment(clickedSetNo)
            else -> HomeFragmentDirections.actionHomeFragmentToLearnFragment(clickedSetNo)
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

    companion object {
        lateinit var setList: ArrayList<Set>
        lateinit var setAdapter: SetAdapter
    }
}