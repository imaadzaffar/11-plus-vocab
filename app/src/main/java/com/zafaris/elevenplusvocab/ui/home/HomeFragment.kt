package com.zafaris.elevenplusvocab.ui.home

import android.app.Dialog
import android.content.ActivityNotFoundException
import android.content.Intent
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.media.MediaPlayer
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.GridLayoutManager
import com.zafaris.elevenplusvocab.HomeGraphDirections
import com.zafaris.elevenplusvocab.R
import com.zafaris.elevenplusvocab.data.database.WordBankDbAccess
import com.zafaris.elevenplusvocab.data.model.Set
import com.zafaris.elevenplusvocab.databinding.FragmentHomeBinding
import com.zafaris.elevenplusvocab.databinding.HomeDialogSetLockedBinding
import com.zafaris.elevenplusvocab.databinding.HomeDialogSetUnlockedBinding
import com.zafaris.elevenplusvocab.ui.settings.SettingsActivity
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

        setHasOptionsMenu(true)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
        _setUnlockedBinding = null
        _setLockedBinding = null
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        super.onCreateOptionsMenu(menu, inflater)
        inflater.inflate(R.menu.home_menu, menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.menu_share -> {
                val sendIntent = Intent(Intent.ACTION_SEND)
                val appPackageName = activity?.packageName //TODO: getPackageName();
                sendIntent.putExtra(Intent.EXTRA_TEXT, "Download the 11+ Learn Vocab app at https://play.google.com/store/apps/details?id=$appPackageName")
                sendIntent.type = "text/plain"

                // intent to Share
                val shareIntent = Intent.createChooser(sendIntent, "Share using")
                startActivity(shareIntent)

                true
            }
            R.id.menu_rate -> {
                val appPackageName = activity?.packageName

                // intent to Google Play Store
                try {
                    val intent = Intent(Intent.ACTION_VIEW, Uri.parse("market://details?id=$appPackageName"))
                    startActivity(intent)
                } catch (e: ActivityNotFoundException) {
                    val intent = Intent(Intent.ACTION_VIEW, Uri.parse("https://play.google.com/store/apps/details?id=$appPackageName"))
                    startActivity(intent)
                }

                true
            }
            R.id.menu_settings -> {
                // intent to Settings Activity
                val intent = Intent(requireContext(), SettingsActivity::class.java)
                startActivity(intent)

                true
            }
            else -> super.onOptionsItemSelected(item)
        }
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
        binding.rvSets.addItemDecoration(SpacingItemDecoration(resources.getDimensionPixelSize(R.dimen.set_spacing), 2, true))
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