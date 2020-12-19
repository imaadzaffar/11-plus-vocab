package com.zafaris.elevenplusvocab.ui.learn

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.view.animation.AccelerateInterpolator
import android.view.animation.DecelerateInterpolator
import android.view.animation.LinearInterpolator
import android.widget.TextView
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
import java.util.ArrayList

class LearnActivity : AppCompatActivity(), CardStackListener {
    private lateinit var db: WordBankDbAccess
    private lateinit var wordsList: List<Word>
    private var setNumber = 0

    private val cardStackView by lazy { findViewById<CardStackView>(R.id.learn_card_stack_view) }
    private val manager by lazy { CardStackLayoutManager(this, this) }
    private val adapter by lazy { CardStackAdapter(createWords()) }

    private val finishLayout by lazy { findViewById<ConstraintLayout>(R.id.learn_finishLayout) }
    private val finishTitle by lazy { findViewById<TextView>(R.id.learn_finishTitle) }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_learn)
        val intent = intent
        setNumber = intent.getIntExtra("setNumber", 0)
        toolbar()

//        finishLayout.visibility = View.GONE
        finishTitle.text = "Finished Set $setNumber"

        // open database and get wordsList
        db = WordBankDbAccess.getInstance(applicationContext)
        db.open()
        wordsList = db.getWordsList(setNumber)
        db.close()

        setupCardStackView()
        setupButtons()
    }

    private fun toolbar() {
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
        val backButton = findViewById<View>(R.id.learn_back_button)
        backButton.setOnClickListener {
            val rewindSetting = RewindAnimationSetting.Builder()
                    .setDirection(Direction.Left)
                    .setDuration(Duration.Normal.duration)
                    .setInterpolator(DecelerateInterpolator())
                    .build()
            manager.setRewindAnimationSetting(rewindSetting)
            cardStackView.rewind()
        }

        val nextButton = findViewById<View>(R.id.learn_next_button)
        nextButton.setOnClickListener {
            val swipeSetting = SwipeAnimationSetting.Builder()
                    .setDirection(Direction.Left)
                    .setDuration(Duration.Normal.duration)
                    .setInterpolator(AccelerateInterpolator())
                    .build()
            manager.setSwipeAnimationSetting(swipeSetting)
            cardStackView.swipe()
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

    private fun createWords(): List<Word> {
        val testWord1 = Word(id = 1, set = 1, word = "test", type = "noun", meanings = listOf(Meaning(definition = "This is the definition of test", example = "This is an example sentence for test", synonyms = "example, example, example", antonyms = "example, example, example")))
        val testWord2 = Word(id = 2, set = 1, word = "test", type = "noun", meanings = listOf(Meaning(definition = "This is the definition of test", example = "This is an example sentence for test", synonyms = "example, example, example", antonyms = "example, example, example")))
        val testWord3 = Word(id = 3, set = 1, word = "test", type = "noun", meanings = listOf(Meaning(definition = "This is the definition of test", example = "This is an example sentence for test", synonyms = "example, example, example", antonyms = "example, example, example")))

        val words = ArrayList<Word>()
        words.add(testWord1)
        words.add(testWord2)
        words.add(testWord3)
        return words
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

        if (position == wordsList.size - 1) {
            cardStackView.visibility = View.VISIBLE
            Log.d("CardStackView Visibile", "VISIBLE")
        }
    }

    override fun onCardDisappeared(view: View?, position: Int) {
        val textView = view?.findViewById<TextView>(R.id.card_wordText)
        Log.d("CardStackView", "onCardDisappeared: ($position) ${textView?.text}")

        if (position == wordsList.size - 1) {
            cardStackView.visibility = View.GONE
            Log.d("CardStackView Visibile", "GONE")
        }
    }
}