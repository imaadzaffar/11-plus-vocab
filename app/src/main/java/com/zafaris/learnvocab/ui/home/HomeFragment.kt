package com.zafaris.learnvocab.ui.home

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
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.GridLayoutManager
import com.qonversion.android.sdk.*
import com.qonversion.android.sdk.dto.QPermission
import com.qonversion.android.sdk.dto.offerings.QOfferings
import com.zafaris.learnvocab.R
import com.zafaris.learnvocab.data.model.Set
import com.zafaris.learnvocab.databinding.FragmentHomeBinding
import com.zafaris.learnvocab.databinding.HomeDialogSetLockedBinding
import com.zafaris.learnvocab.databinding.HomeDialogSetUnlockedBinding
import com.zafaris.learnvocab.extensions.buildError
import com.zafaris.learnvocab.ui.settings.SettingsActivity
import com.zafaris.learnvocab.util.PERMISSION_ID

class HomeFragment : Fragment(), SetAdapter.OnItemClickListener {
    private var _binding: FragmentHomeBinding? = null
    private var _setUnlockedBinding: HomeDialogSetUnlockedBinding? = null
    private var _setLockedBinding: HomeDialogSetLockedBinding? = null
    private val binding get() = _binding!!
    private val setUnlockedBinding get() = _setUnlockedBinding!!
    private val setLockedBinding get() = _setLockedBinding!!

    private val model: HomeViewModel by activityViewModels()

    private lateinit var setDialog: Dialog
    private lateinit var mediaPlayer: MediaPlayer

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
        buildSetRv()

        setHasOptionsMenu(true)

        fetchOfferings()
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

    private fun buildSetRv() {
        adapter = SetAdapter(model.generateDummySets(), this)
        manager = GridLayoutManager(requireContext(), 2)

        binding.rvSets.addItemDecoration(SpacingItemDecoration(resources.getDimensionPixelSize(R.dimen.set_spacing), 2, true))
        binding.rvSets.layoutManager = manager
        binding.rvSets.adapter = adapter
    }

    override fun onItemSetClick(set: Set) {
        model.clickedSetNo = set.setNo
        //TODO: Check if user has premium, then show correct dialog
        /*Purchases.sharedInstance.getPurchaserInfoWith({
            buildError(context, it.message)
        }, {
            if (it.entitlements[ENTITLEMENT_ID]?.isActive == true) {
                Toast.makeText(context, "Entitled to do action", Toast.LENGTH_LONG).show()
            } else {
                showLockedDialog()
            }
        })*/
        when {
            set.isSetLocked -> {
                playSound(R.raw.sfx_locked)
                showLockedDialog()
            }
            else -> {
                playSound(R.raw.sfx_click_set)
                showUnlockedDialog()
            }
        }
    }

    private fun showUnlockedDialog() {
        setDialog.setContentView(setUnlockedBinding.root)
        setUnlockedBinding.titleDialog.text = "Set ${model.clickedSetNo}"
        setUnlockedBinding.buttonLearn.setOnClickListener { navigateAction("learn") }
        setUnlockedBinding.buttonTest.setOnClickListener { navigateAction("test") }
//        setUnlockedBinding.buttonStats.setOnClickListener { navigateAction("stats") }
        setDialog.window!!.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        setDialog.show()
    }

    private fun showLockedDialog() {
        setDialog.setContentView(setLockedBinding.root)
        setLockedBinding.titleDialog.text = "Set ${model.clickedSetNo} locked"
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
        manager.smoothScrollToPosition(binding.rvSets, null, 0)
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
                        // handle active permission here
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