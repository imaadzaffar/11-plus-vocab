package com.zafaris.elevenplusvocab.ui.main

import android.app.Activity
import android.app.Dialog
import android.content.ActivityNotFoundException
import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.media.MediaPlayer
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.zafaris.elevenplusvocab.R
import com.zafaris.elevenplusvocab.ui.learn.LearnActivity
import com.zafaris.elevenplusvocab.ui.settings.SettingsActivity
import com.zafaris.elevenplusvocab.ui.test.TestActivity
import com.zafaris.elevenplusvocab.utils.NO_OF_FREE_SETS
import com.zafaris.elevenplusvocab.utils.NO_OF_TOTAL_SETS
import com.zafaris.elevenplusvocab.utils.SET_SIZE
import com.zafaris.elevenplusvocab.utils.WordBankDbAccess
import java.util.*

class MainActivity : AppCompatActivity() {

    private lateinit var popupDialog: Dialog
    private lateinit var popupTitle: TextView
    private lateinit var learnButton: Button
    private lateinit var testButton: Button
    private lateinit var statsButton: Button
    private lateinit var unlockButton: Button
    private lateinit var setRv: RecyclerView

    private var clickedSet = 0
    private lateinit var mediaPlayer: MediaPlayer

    private lateinit var setLayoutManager: GridLayoutManager

    private lateinit var db: WordBankDbAccess

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        val toolbar = findViewById<Toolbar>(R.id.toolbar)
        setSupportActionBar(toolbar)
        supportActionBar?.title = "Home"

        db = WordBankDbAccess.getInstance(applicationContext)
        db.open()
        val noOfSets = db.getNoOfSets(SET_SIZE)
        db.close()
        setList = ArrayList()

        Log.i("noOfSets", noOfSets.toString())
        for (i in 1..NO_OF_TOTAL_SETS) { //TODO: Uncomment this when word bank completed
            if (i <= NO_OF_FREE_SETS) {
                setList.add(Set(i, isSetCompleted = false, isSetLocked = false))
            } else {
                setList.add(Set(i, isSetCompleted = false, isSetLocked = true))
            }
        }
        popupDialog = Dialog(this)
        buildSetRv()
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        val inflater = menuInflater
        inflater.inflate(R.menu.options_menu, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.menu_share -> {
                //                Toast.makeText(this, "Share", Toast.LENGTH_SHORT).show();
                val sendIntent = Intent(Intent.ACTION_SEND)
                val appPackageName = packageName //TODO: getPackageName();
                sendIntent.putExtra(Intent.EXTRA_TEXT, "Download this great app at https://play.google.com/store/apps/details?id=$appPackageName")
                sendIntent.type = "text/plain"
                val shareIntent = Intent.createChooser(sendIntent, "Share using")
                startActivity(shareIntent)

                //Share intent
                true
            }
            R.id.menu_rate -> {
                Toast.makeText(this, "Rate Us", Toast.LENGTH_SHORT).show()
                val appPackageName = packageName //TODO: getPackageName();
                try {
                    startActivity(Intent(Intent.ACTION_VIEW, Uri.parse("market://details?id=$appPackageName")))
                } catch (anfe: ActivityNotFoundException) {
                    startActivity(Intent(Intent.ACTION_VIEW, Uri.parse("https://play.google.com/store/apps/details?id=$appPackageName")))
                }

                //Intent to Google Play Store
                true
            }
            R.id.menu_settings -> {
                Toast.makeText(this, "Settings", Toast.LENGTH_SHORT).show()
                //Intent to settings activity
                val intent = Intent(this@MainActivity, SettingsActivity::class.java)
                startActivity(intent)
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    private fun buildSetRv() {
        setRv = findViewById(R.id.setRv)
        setAdapter = SetAdapter(setList)
        setLayoutManager = GridLayoutManager(this, 2)
        setRv.layoutManager = setLayoutManager
        setRv.adapter = setAdapter
        setAdapter.onItemClick = { set, position ->
            clickedSet = position + 1
            mediaPlayer = MediaPlayer.create(this@MainActivity, R.raw.sfx_menu_click)
            if (mediaPlayer.isPlaying) {
                mediaPlayer.release()
            }
            mediaPlayer.start()
            when {
                setList[position].isSetLocked -> {
                    showPopupLocked()
                }
                setList[position].isSetCompleted -> {
                    showPopupCompleted()
                }
                else -> {
                    showPopupPlay()
                }
            }
        }
    }

    private fun showPopupLocked() {
        popupDialog.setContentView(R.layout.popup_locked)
        popupTitle = popupDialog.findViewById(R.id.popupTitle)
        unlockButton = popupDialog.findViewById(R.id.unlockButton)
        val title = "Set $clickedSet locked"
        popupTitle.text = title
        unlockButton.setOnClickListener {
            Toast.makeText(this@MainActivity, "Unlock sets", Toast.LENGTH_SHORT).show()
            //TODO: Show locked message and ask for payment
        }
        val noThanksButton = popupDialog.findViewById<TextView>(R.id.noThanksButton)
        noThanksButton.setOnClickListener {
            popupDialog.dismiss()
        }
        popupDialog.window!!.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        popupDialog.show()
    }

    private fun showPopupCompleted() {
        popupDialog.setContentView(R.layout.popup_completed)
        popupTitle = popupDialog.findViewById(R.id.popupTitle)
        learnButton = popupDialog.findViewById(R.id.learnButton)
        testButton = popupDialog.findViewById(R.id.testButton)
        statsButton = popupDialog.findViewById(R.id.statsButton)
        val title = "Set $clickedSet completed"
        popupTitle.text = title
        learnButton.setOnClickListener { goToActivity(LearnActivity()) }
        testButton.setOnClickListener { goToActivity(TestActivity()) }
        statsButton.setOnClickListener { goToActivity(LearnActivity()) } //TODO: Intent to Stats Activity
        popupDialog.window!!.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        popupDialog.show()
    }

    private fun showPopupPlay() {
        popupDialog.setContentView(R.layout.popup_play)
        popupTitle = popupDialog.findViewById(R.id.popupTitle)
        learnButton = popupDialog.findViewById(R.id.learnButton)
        testButton = popupDialog.findViewById(R.id.testButton)
        val title = "Set $clickedSet"
        popupTitle.text = title
        learnButton.setOnClickListener { goToActivity(LearnActivity()) }
        testButton.setOnClickListener { goToActivity(TestActivity()) }
        popupDialog.window!!.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        popupDialog.show()
    }

    private fun goToActivity(activity: Activity) {
        mediaPlayer = MediaPlayer.create(this@MainActivity, R.raw.sfx_menu_click)
        if (mediaPlayer.isPlaying) {
            mediaPlayer.release()
        }
        mediaPlayer.start()
        popupDialog.dismiss()
        val intent = Intent(this@MainActivity, activity::class.java)
        intent.putExtra("setNumber", clickedSet)
        startActivity(intent)
        setLayoutManager.smoothScrollToPosition(setRv, null, 0)
    }

    companion object {
        lateinit var setList: ArrayList<Set>
        lateinit var setAdapter: SetAdapter
    }
}