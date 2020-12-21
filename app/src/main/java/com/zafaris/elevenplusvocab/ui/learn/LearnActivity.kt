package com.zafaris.elevenplusvocab.ui.learn

import android.app.Dialog
import android.content.Intent
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.media.MediaPlayer
import android.os.Bundle
import android.util.Log
import android.view.View
import android.view.animation.AccelerateInterpolator
import android.view.animation.DecelerateInterpolator
import android.view.animation.LinearInterpolator
import android.widget.Button
import android.widget.ImageButton
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.recyclerview.widget.DefaultItemAnimator
import com.yuyakaido.android.cardstackview.*
import com.zafaris.elevenplusvocab.R
import com.zafaris.elevenplusvocab.ui.main.MainActivity
import com.zafaris.elevenplusvocab.ui.main.Set
import com.zafaris.elevenplusvocab.ui.test.TestActivity
import com.zafaris.elevenplusvocab.utils.Word
import com.zafaris.elevenplusvocab.utils.WordBankDbAccess

class LearnActivity : AppCompatActivity(), CardStackListener {
    private lateinit var db: WordBankDbAccess
    private lateinit var wordsList: List<Word>
    private var setNumber = 0

    private val cardStackView by lazy { findViewById<CardStackView>(R.id.learn_card_stack_view) }
    private val manager by lazy { CardStackLayoutManager(this, this) }
    private val adapter by lazy { CardStackAdapter(getWords()) }

    private val backButton by lazy { findViewById<View>(R.id.learn_back_button) }
    private val nextButton by lazy { findViewById<View>(R.id.learn_next_button) }

    private lateinit var finishDialog: Dialog

    private lateinit var mediaPlayer: MediaPlayer

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_learn)

        val intent = intent
        setNumber = intent.getIntExtra("setNumber", 0)

        setupToolbar()
        setupCardStackView()
        setupButtons()
    }

    private fun setupToolbar() {
        val toolbar = findViewById<Toolbar>(R.id.toolbar)
        toolbar.context.setTheme(R.style.ToolbarThemeDark)
        toolbar.setBackgroundColor(getColor(R.color.colorBlue))
        toolbar.setTitleTextColor(getColor(R.color.textOnDark))
        window.statusBarColor = getColor(R.color.colorBlueStatus)
        setSupportActionBar(toolbar)
        supportActionBar?.title = "Learn: Set $setNumber"
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
    }

    private fun setupCardStackView() {
        initialize()
    }

    private fun setupButtons() {
        val rewindSetting = RewindAnimationSetting.Builder()
                .setDirection(Direction.Left)
                .setDuration(Duration.Normal.duration)
                .setInterpolator(DecelerateInterpolator())
                .build()
        manager.setRewindAnimationSetting(rewindSetting)

        backButton.setOnClickListener {
            playButtonClickSound()
            cardStackView.rewind()
            if (manager.topPosition == 0) {
                backButton.visibility = View.INVISIBLE
            }
        }

        val swipeSetting = SwipeAnimationSetting.Builder()
                .setDirection(Direction.Left)
                .setDuration(Duration.Normal.duration)
                .setInterpolator(AccelerateInterpolator())
                .build()
        manager.setSwipeAnimationSetting(swipeSetting)

        nextButton.setOnClickListener {
            playButtonClickSound()
            if (manager.topPosition == wordsList.size - 1) {
                showFinishDialog()
            } else {
                cardStackView.swipe()
                if (manager.topPosition == 0) {
                    backButton.visibility = View.VISIBLE
                }
            }

        }
    }

    private fun initialize() {
        manager.setStackFrom(StackFrom.Right)
        manager.setVisibleCount(3)
        manager.setTranslationInterval(8.0f)
        manager.setScaleInterval(0.95f)
        manager.setSwipeThreshold(0.3f)
        manager.setMaxDegree(20.0f)
        manager.setDirections(Direction.HORIZONTAL)
        manager.setCanScrollHorizontal(true)
        manager.setCanScrollVertical(false)
        manager.setSwipeableMethod(SwipeableMethod.Automatic)
        manager.setOverlayInterpolator(LinearInterpolator())
        cardStackView.layoutManager = manager
        cardStackView.adapter = adapter
        cardStackView.itemAnimator.apply {
            if (this is DefaultItemAnimator) {
                supportsChangeAnimations = false
            }
        }
    }

    private fun getWords(): List<Word> {
        // open database and get wordsList
        db = WordBankDbAccess.getInstance(applicationContext)
        db.open()
        wordsList = db.getWordsList(setNumber)
        db.close()
        return wordsList
    }

    private fun showFinishDialog() {
        finishDialog = Dialog(this)

        finishDialog.setContentView(R.layout.popup_finish_learn)
        val finishTitle = finishDialog.findViewById<TextView>(R.id.learn_finishTitle)
        finishTitle.text = "Finished Set $setNumber!"

        val testButton = finishDialog.findViewById<Button>(R.id.learn_testButton)
        testButton.setOnClickListener {
            val intent = Intent(this@LearnActivity, TestActivity::class.java)
            startActivity(intent)
        }
        val homeButton = finishDialog.findViewById<Button>(R.id.learn_homeButton)
        homeButton.setOnClickListener {
            val intent = Intent(this@LearnActivity, MainActivity::class.java)
            startActivity(intent)
        }
        finishDialog.window!!.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        finishDialog.show()
    }

    private fun playButtonClickSound() {
        mediaPlayer = MediaPlayer.create(this@LearnActivity, R.raw.sfx_menu_click)
        if (mediaPlayer.isPlaying) {
            mediaPlayer.release()
        }
        mediaPlayer.start()
    }

    fun goToTest(view: View?) {
        val intent = Intent(this@LearnActivity, TestActivity::class.java)
        intent.putExtra("setNumber", setNumber)
        startActivity(intent)
    }

    fun goToHome(view: View?) {
        MainActivity.setList[setNumber - 1] = Set(setNumber, isSetCompleted = true, isSetLocked = false)
        MainActivity.setAdapter.notifyDataSetChanged()
        val intent = Intent(this@LearnActivity, MainActivity::class.java)
        intent.putExtra("setNumber", setNumber)
        startActivity(intent)
    }

    override fun onCardDragging(direction: Direction?, ratio: Float) {
        Log.d("CardStackView", "onCardDragging: d = ${direction?.name}, r = $ratio")
    }

    override fun onCardSwiped(direction: Direction?) {
        Log.d("CardStackView", "onCardSwiped: p = ${manager.topPosition}, d = $direction")
    }

    override fun onCardRewound() {
        Log.d("CardStackView", "onCardRewound: ${manager.topPosition}")
    }

    override fun onCardCanceled() {
        Log.d("CardStackView", "onCardCanceled: ${manager.topPosition}")
    }

    override fun onCardAppeared(view: View?, position: Int) {
        val textView = view?.findViewById<TextView>(R.id.card_wordText)
        Log.d("CardStackView", "onCardAppeared: ($position) ${textView?.text}")
    }

    override fun onCardDisappeared(view: View?, position: Int) {
        val textView = view?.findViewById<TextView>(R.id.card_wordText)
        Log.d("CardStackView", "onCardDisappeared: ($position) ${textView?.text}")
    }
}