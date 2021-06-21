package com.zafaris.learnvocab.ui.wordslist

import android.app.Dialog
import android.content.ActivityNotFoundException
import android.content.Intent
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.media.MediaPlayer
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.*
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.widget.SearchView
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import com.qonversion.android.sdk.*
import com.qonversion.android.sdk.dto.QPermission
import com.qonversion.android.sdk.dto.offerings.QOfferings
import com.zafaris.learnvocab.R
import com.zafaris.learnvocab.data.model.Set
import com.zafaris.learnvocab.data.model.Word
import com.zafaris.learnvocab.databinding.FragmentWordslistBinding
import com.zafaris.learnvocab.databinding.HomeDialogSetLockedBinding
import com.zafaris.learnvocab.databinding.HomeDialogSetUnlockedBinding
import com.zafaris.learnvocab.databinding.WordslistDialogWordBinding
import com.zafaris.learnvocab.extensions.buildError
import com.zafaris.learnvocab.ui.home.HomeFragmentDirections
import com.zafaris.learnvocab.ui.settings.SettingsActivity
import com.zafaris.learnvocab.util.NO_OF_FREE_SETS
import com.zafaris.learnvocab.util.PERMISSION_ID

class WordsListFragment : Fragment(), WordsListAdapter.OnItemClickListener {
	private var _binding: FragmentWordslistBinding? = null
	private var _setUnlockedBinding: HomeDialogSetUnlockedBinding? = null
	private var _setLockedBinding: HomeDialogSetLockedBinding? = null
	private var _wordDialogBinding: WordslistDialogWordBinding? = null
	private val binding get() = _binding!!
	private val setUnlockedBinding get() = _setUnlockedBinding!!
	private val setLockedBinding get() = _setLockedBinding!!
	private val wordDialogBinding get() = _wordDialogBinding!!

	val model: WordsListViewModel by activityViewModels()

	private lateinit var setDialog: Dialog
	private lateinit var wordDialog: Dialog
	private lateinit var mediaPlayer: MediaPlayer

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
		buildRv()

		setHasOptionsMenu(true)

		fetchOfferings()
	}

	override fun onDestroyView() {
		super.onDestroyView()
		_binding = null
		_setUnlockedBinding = null
		_setLockedBinding = null
		_wordDialogBinding = null
	}

	override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
		super.onCreateOptionsMenu(menu, inflater)
		inflater.inflate(R.menu.wordslist_menu, menu)

		val searchItem = menu.findItem(R.id.menu_search)
		val searchView = searchItem.actionView as SearchView

		searchView.queryHint = "Search for word"
		val searchText = searchView.findViewById<EditText>(androidx.appcompat.R.id.search_src_text)
		searchText.setTextColor(ContextCompat.getColor(requireContext(), R.color.textOnLight))
		searchText.setHintTextColor(ContextCompat.getColor(requireContext(), R.color.colorLightGrey))

		searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
			override fun onQueryTextSubmit(query: String?): Boolean {
				return false
			}

			override fun onQueryTextChange(newText: String?): Boolean {
				adapter.filter.filter(newText)
				return true
			}
		})
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

	private fun buildRv() {
		adapter = WordsListAdapter(model.generateDummyList(), this)
		manager = LinearLayoutManager(context)

		binding.rvItems.addItemDecoration(DividerItemDecoration(context, LinearLayoutManager.VERTICAL))
		binding.rvItems.layoutManager = manager
		binding.rvItems.adapter = adapter
	}

	override fun onItemWordClick(word: Word, position: Int) {
		val setNo = word.set
		if (setNo <= NO_OF_FREE_SETS) {
			playSound(R.raw.sfx_click_button_2)
			showWordDialog(word)
		} else {
			playSound(R.raw.sfx_locked)
			showLockedDialog(setNo)
		}
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
		model.clickedSetNo = set.setNo
		showSetDialog(set)
	}
	
	private fun showSetDialog(set: Set) {
		when {
			set.setNo <= NO_OF_FREE_SETS -> {
				playSound(R.raw.sfx_click_set)
				showUnlockedDialog()
			}
			else -> {
				playSound(R.raw.sfx_locked)
				showLockedDialog(set.setNo)
			}
		}
	}

	private fun showUnlockedDialog() {
		setDialog.setContentView(setUnlockedBinding.root)
		setUnlockedBinding.titleDialog.text = "Set ${model.clickedSetNo}"
		setUnlockedBinding.buttonLearn.setOnClickListener { navigateAction("learn") }
		setUnlockedBinding.buttonTest.setOnClickListener { navigateAction("test") }
//		setUnlockedBinding.buttonStats.setOnClickListener { navigateAction("stats") }
		setDialog.window!!.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
		setDialog.show()
	}

	private fun showLockedDialog(setNo: Int) {
		setDialog.setContentView(setLockedBinding.root)
		setLockedBinding.titleDialog.text = "Set ${setNo} locked"
		setLockedBinding.buttonUnlock.setOnClickListener {
			purchasePackage()
		}
		setLockedBinding.buttonDismiss.setOnClickListener {
			setDialog.dismiss()
		}
		setDialog.window!!.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
		setDialog.show()
	}

	private fun navigateAction(destination: String) {
		playSound(R.raw.sfx_click_button)
		manager.smoothScrollToPosition(binding.rvItems, null, 0)
		setDialog.dismiss()

		val action = when (destination) {
			"learn" -> HomeFragmentDirections.actionGlobalLearn(model.clickedSetNo)
			"test" -> HomeFragmentDirections.actionGlobalTest(model.clickedSetNo)
			"stats" -> HomeFragmentDirections.actionGlobalStats(model.clickedSetNo)
			else -> throw IllegalArgumentException("Invalid destination")
		}
		findNavController().navigate(action)
	}

	private fun playSound(resourceId: Int) {
		mediaPlayer = MediaPlayer.create(context, resourceId)
		if (mediaPlayer.isPlaying) {
			mediaPlayer.release()
		}
		mediaPlayer.start()
	}

	private fun fetchOfferings() {
		Qonversion.offerings(object: QonversionOfferingsCallback {
			override fun onSuccess(offerings: QOfferings) {
				val mainOffering = offerings.main
				if (mainOffering != null && mainOffering.products.isNotEmpty()) {
					model.mainProduct = mainOffering?.products[0]
				}
			}

			override fun onError(error: QonversionError) {
				// handle error here
				Log.e("QonversionError", error.description)
				buildError(context, error.description)
			}
		})
	}

	private fun purchasePackage() {
		if (model.mainProduct != null) {
			Qonversion.purchase(requireActivity(), model.mainProduct!!.qonversionID, callback = object : QonversionPermissionsCallback {
				override fun onSuccess(permissions: Map<String, QPermission>) {
					val premiumPermission = permissions[PERMISSION_ID]
					if (premiumPermission != null && premiumPermission.isActive()) {
						//TODO: handle active permission here
						Toast.makeText(context, "Premium purchase successful", Toast.LENGTH_LONG).show()
					}
				}

				override fun onError(error: QonversionError) {
					// handle error here
					if (error.code != QonversionErrorCode.CanceledPurchase) {
						Log.e("QonversionError", error.description)
						buildError(context, error.description)
					}
				}
			})
		}
	}

	/* Revenue Cat
	private fun fetchOfferings() {
        Purchases.sharedInstance.getOfferingsWith(
            onError = { error ->
                buildError(context, error.message)
            },
            onSuccess = { offerings ->
                model.currentPackage = offerings.current?.availablePackages?.get(0)
            }
        )
    }

	private fun purchasePackage() {
        if (model.currentPackage != null) {
            Purchases.sharedInstance.purchasePackageWith(
                requireActivity(),
                model.currentPackage!!,
                onError = { error, userCancelled -> (
                    if (!userCancelled) {
                        buildError(context, error.message)
                    }
                )},
                onSuccess = { product, purchaserInfo ->
                    if (purchaserInfo.entitlements[ENTITLEMENT_ID]?.isActive == true) {
                        // Unlock that great "pro" content
                        Toast.makeText(context, "Premium purchase successful", Toast.LENGTH_LONG).show()
                    }
                }
            )
        }
    }
	*/

}