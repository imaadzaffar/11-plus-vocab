package com.zafaris.elevenplusvocab.ui.home

import android.app.Dialog
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.media.MediaPlayer
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.GridLayoutManager
import com.zafaris.elevenplusvocab.R
import com.zafaris.elevenplusvocab.data.database.WordBankDbAccess
import com.zafaris.elevenplusvocab.data.model.Set
import com.zafaris.elevenplusvocab.databinding.FragmentHomeBinding
import com.zafaris.elevenplusvocab.databinding.HomeDialogSetLockedBinding
import com.zafaris.elevenplusvocab.databinding.HomeDialogSetUnlockedBinding
import com.zafaris.elevenplusvocab.util.NO_OF_FREE_SETS
import com.zafaris.elevenplusvocab.util.NO_OF_TOTAL_SETS
import com.zafaris.elevenplusvocab.util.SET_SIZE
import java.util.*

class HomeFragment : Fragment(), SetAdapter.OnItemClickListener {
    private var _binding: FragmentHomeBinding? = null
    private var _setUnlockedBinding: HomeDialogSetUnlockedBinding? = null
    private var _setLockedBinding: HomeDialogSetLockedBinding? = null
    private val binding get() = _binding!!
    private val setUnlockedBinding get() = _setUnlockedBinding!!
    private val setLockedBinding get() = _setLockedBinding!!

    private lateinit var db: WordBankDbAccess
    private var clickedSetNo = 0

    private lateinit var setDialog: Dialog
    private lateinit var mediaPlayer: MediaPlayer

    private lateinit var sets: ArrayList<Set>
    private lateinit var adapter: SetAdapter
    private lateinit var manager: GridLayoutManager

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        _binding = FragmentHomeBinding.inflate(inflater, container, false)
        val view = binding.root

        _setUnlockedBinding = HomeDialogSetUnlockedBinding.inflate(inflater)
        _setLockedBinding = HomeDialogSetLockedBinding.inflate(inflater)

        setDialog = Dialog(requireContext())

        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        generateDummySets()
        buildSetRv()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
        _setUnlockedBinding = null
        _setLockedBinding = null
    }

    private fun generateDummySets() {
        db = WordBankDbAccess.getInstance(requireContext())
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
    }

    private fun buildSetRv() {
        adapter = SetAdapter(sets, this)
        manager = GridLayoutManager(requireContext(), 2)
        binding.rvSets.layoutManager = manager
        binding.rvSets.adapter = adapter
    }

    override fun onItemSetClick(set: Set) {
        playMenuClickSound()
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
        manager.smoothScrollToPosition(binding.rvSets, null, 0)
        setDialog.dismiss()

        val setNo = clickedSetNo
        val action = when (destination) {
            "learn" -> HomeFragmentDirections.actionHomeFragmentToLearnFragment(setNo)
            "test" -> HomeFragmentDirections.actionHomeFragmentToTestFragment(setNo)
            "stats" -> HomeFragmentDirections.actionHomeFragmentToStatsFragment(setNo)
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