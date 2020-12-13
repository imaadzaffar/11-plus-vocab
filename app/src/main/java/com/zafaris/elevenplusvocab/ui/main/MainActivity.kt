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
import com.zafaris.elevenplusvocab.utils.WordBankDbAccess
import java.util.*

class MainActivity : AppCompatActivity() {

    private lateinit var popupDialog: Dialog
    private lateinit var popupTitle: TextView
    private lateinit var learnButton: Button
    private lateinit var testButton: Button
    private lateinit var statsButton: Button
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
        val prefs = getSharedPreferences("com.zafaris.learnvocab", Context.MODE_PRIVATE)

        // first time
        if (prefs.getBoolean("firstTime", true)) {
            Log.i("firstTime",
                    prefs.getBoolean("firstTime", true).toString())
            prefs.edit().putInt("setSize", setSize).apply()
            prefs.edit().putBoolean("firstTime", false).apply()
            // not first time
        } else {
            Log.i("firstTime",
                    prefs.getBoolean("firstTime", true).toString())
            setSize = prefs.getInt("setSize", 1)
        }

        //SharedPreferences settingsPref = PreferenceManager.getDefaultSharedPreferences(this);
        //Boolean switchPref = settingsPref.getBoolean(SettingsActivity.KEY_PREF_REMINDER_SWITCH, false);
        //Toast.makeText(this, switchPref.toString(), Toast.LENGTH_SHORT).show();
        db = WordBankDbAccess.getInstance(applicationContext)
        db.open()
        val noOfSets = db.getNoOfSets(setSize)
        db.close()
        setList = ArrayList()
        //setList.clear();
        Log.i("noOfSets", noOfSets.toString())
        for (i in 1..25) { //TODO: Uncomment this when word bank completed
            //for (int i = 1; i <= noOfSets; i++) {
            if (i <= 2) {
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
        learnButton = popupDialog.findViewById(R.id.learnButton)
        testButton = popupDialog.findViewById(R.id.testButton)
        val title = StringBuilder("Set ")
        title.append(clickedSet)
        title.append(" locked")
        popupTitle.setText(title)
        learnButton.setOnClickListener(View.OnClickListener {
            Toast.makeText(this@MainActivity, "Set is locked!", Toast.LENGTH_SHORT).show()
            //TODO: Show locked message and ask for payment
        })
        testButton.setOnClickListener(View.OnClickListener { Toast.makeText(this@MainActivity, "Set is locked!", Toast.LENGTH_SHORT).show() })
        popupDialog.window!!.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        popupDialog.show()
    }

    private fun showPopupCompleted() {
        popupDialog.setContentView(R.layout.popup_completed)
        popupTitle = popupDialog.findViewById(R.id.popupTitle)
        learnButton = popupDialog.findViewById(R.id.learnButton)
        testButton = popupDialog.findViewById(R.id.testButton)
        statsButton = popupDialog.findViewById(R.id.statsButton)
        val title = StringBuilder("Set ")
        title.append(clickedSet)
        title.append(" completed")
        popupTitle.text = title
        learnButton.setOnClickListener(View.OnClickListener { goToActivity(LearnActivity()) })
        testButton.setOnClickListener(View.OnClickListener { goToActivity(TestActivity()) })
        statsButton.setOnClickListener(View.OnClickListener { goToActivity(LearnActivity()) }) //TODO: Intent to Stats Activity
        popupDialog.window!!.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        popupDialog.show()
    }

    private fun showPopupPlay() {
        popupDialog.setContentView(R.layout.popup_play)
        popupTitle = popupDialog.findViewById(R.id.popupTitle)
        learnButton = popupDialog.findViewById(R.id.learnButton)
        testButton = popupDialog.findViewById(R.id.testButton)
        val title = StringBuilder("Set ")
        title.append(clickedSet)
        popupTitle.text = title
        learnButton.setOnClickListener(View.OnClickListener { goToActivity(LearnActivity()) })
        testButton.setOnClickListener(View.OnClickListener { goToActivity(TestActivity()) })
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
        var setSize: Int = 25
        lateinit var setList: ArrayList<Set>
        lateinit var setAdapter: SetAdapter
    }
}