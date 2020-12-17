package com.zafaris.elevenplusvocab.ui.learn

import android.content.Intent
import android.graphics.Rect
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.zafaris.elevenplusvocab.R
import com.zafaris.elevenplusvocab.ui.main.MainActivity
import com.zafaris.elevenplusvocab.ui.main.Set
import com.zafaris.elevenplusvocab.ui.test.TestActivity
import com.zafaris.elevenplusvocab.utils.SET_SIZE
import com.zafaris.elevenplusvocab.utils.Word
import com.zafaris.elevenplusvocab.utils.WordBankDbAccess

class LearnActivity : AppCompatActivity() {
    private lateinit var db: WordBankDbAccess
    private lateinit var wordsList: List<Word>
    private lateinit var currentMeanings: List<Meaning>
    private lateinit var currentWord: Word

    private var setNumber = 0
    private var wordNumber = 0
    private var wordsPreview = false
    private var firstWord = false

    private lateinit var wordsLayout: ConstraintLayout
    private lateinit var meaningsLayout: ConstraintLayout
    private lateinit var finishLayout: ConstraintLayout
    private lateinit var wordsRv: RecyclerView
    private lateinit var meaningsRv: RecyclerView
    private lateinit var wordText: TextView
    private lateinit var typeText: TextView
    private lateinit var bottomText: TextView
    private lateinit var finishTitle: TextView
    private lateinit var nextButton: ImageView
    private lateinit var previousButton: ImageView

    private lateinit var wordsAdapter: WordsAdapter
    private lateinit var meaningAdapter: MeaningAdapter
    private lateinit var wordsLayoutManager: RecyclerView.LayoutManager
    private lateinit var meaningLayoutManager: RecyclerView.LayoutManager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_learn)
        val intent = intent
        setNumber = intent.getIntExtra("setNumber", 0)

        // change toolbar colour and status bar colour
        val toolbar = findViewById<Toolbar>(R.id.toolbar)
        toolbar.context.setTheme(R.style.ToolbarThemeDark)
        toolbar.setBackgroundColor(getColor(R.color.colorBlue))
        toolbar.setTitleTextColor(getColor(R.color.textOnDark))
        window.statusBarColor = getColor(R.color.colorBlueStatus)
        setSupportActionBar(toolbar)
        supportActionBar?.title = "Learn: Set $setNumber"
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        setNumber = 1 //TODO: Delete this once all sets have been added
        wordNumber = 0
        wordsPreview = true
        firstWord = false

        wordsLayout = findViewById(R.id.learn_wordsLayout)
        wordsLayout.visibility = View.VISIBLE
        meaningsLayout = findViewById(R.id.learn_meaningsLayout)
        meaningsLayout.visibility = View.GONE
        finishLayout = findViewById(R.id.learn_finishLayout)
        finishLayout.visibility = View.GONE

        wordText = findViewById(R.id.learn_wordText)
        typeText = findViewById(R.id.learn_typeText)
        bottomText = findViewById(R.id.learn_bottomText)
        finishTitle = findViewById(R.id.learn_finishTitle)
        finishTitle.text = "Finished Set $setNumber"
        nextButton = findViewById(R.id.learn_nextButton)
        previousButton = findViewById(R.id.learn_previousButton)

        // open database and get wordsList
        db = WordBankDbAccess.getInstance(applicationContext)
        db.open()
        wordsList = db.getWordsList(setNumber)
        db.close()
        currentWord = wordsList[wordNumber]
        currentMeanings = currentWord.meanings
        wordText.text = currentWord.word
        typeText.text = currentWord.type
        bottomText.visibility = View.INVISIBLE
        previousButton.visibility = View.INVISIBLE
        buildWordsRv()
        buildMeaningsRv()
    }

    private fun buildWordsRv() {
        wordsRv = findViewById(R.id.learn_wordsRv)
        wordsRv.setHasFixedSize(true)
        wordsLayoutManager = LinearLayoutManager(this)
        wordsAdapter = WordsAdapter(wordsList)
        wordsAdapter.onItemClick = { word, position -> goToWordsRv(position) }
        wordsRv.layoutManager = wordsLayoutManager
        wordsRv.adapter = wordsAdapter
        wordsRv.addItemDecoration(
                object : DividerItemDecoration(applicationContext, VERTICAL) {
                    override fun getItemOffsets(outRect: Rect, view: View, parent: RecyclerView, state: RecyclerView.State) {
                        val position = parent.getChildAdapterPosition(view)
                        // hide the divider for the last child
                        if (position == parent.adapter!!.itemCount - 1) {
                            outRect.setEmpty()
                        } else {
                            super.getItemOffsets(outRect, view, parent, state)
                        }
                    }
                }
        )
    }

    private fun goToWordsRv(position: Int) {
        wordNumber = position
        wordsPreview = false
        if (position == 0) {
            firstWord = true
        }
        wordsLayoutManager.smoothScrollToPosition(wordsRv, null, 0)
        bottomText.visibility = View.VISIBLE
        meaningsLayout.visibility = View.VISIBLE
        wordsLayout.visibility = View.GONE
        previousButton.visibility = View.VISIBLE
        Log.i("Learn - Word number", wordNumber.toString())
        currentWord = wordsList[wordNumber]
        wordText.text = currentWord.word
        typeText.text = currentWord.type
        updateMeaningsRv()
        bottomText.text = StringBuilder((wordNumber + 1).toString()).append(" / ").append(SET_SIZE)
    }

    private fun buildMeaningsRv() {
        meaningsRv = findViewById(R.id.learn_meaningsRv)
        meaningsRv.setHasFixedSize(true)
        meaningLayoutManager = LinearLayoutManager(this)
        meaningAdapter = MeaningAdapter(this, currentMeanings)
        meaningsRv.layoutManager = meaningLayoutManager
        meaningsRv.adapter = meaningAdapter
        meaningsRv.addItemDecoration(
                object : DividerItemDecoration(applicationContext, VERTICAL) {
                    override fun getItemOffsets(outRect: Rect, view: View, parent: RecyclerView, state: RecyclerView.State) {
                        // hide the divider for the last child
                        if (parent.getChildAdapterPosition(view) == state.itemCount - 1) {
                            outRect.setEmpty()
                        } else {
                            super.getItemOffsets(outRect, view, parent, state)
                        }
                    }
                }
        )
    }

    private fun updateMeaningsRv() {
        meaningAdapter.updateList(currentWord.meanings)
        for (i in currentWord.meanings.indices) {
            val (definition, example, synonyms, antonyms) = currentWord.meanings[i]
            Log.i("Learn - Meaning", definition)
            Log.i("Learn - Meaning", example)
            Log.i("Learn - Meaning", synonyms)
            Log.i("Learn - Meaning", antonyms)
        }
    }

    fun nextWord(view: View?) {
        if (wordsPreview) {
            wordNumber = 0
            wordsPreview = false
            firstWord = true
            wordsLayoutManager.smoothScrollToPosition(wordsRv, null, 0)
            bottomText.visibility = View.VISIBLE
            bottomText.text = StringBuilder("1 / ").append(SET_SIZE)
            wordsLayout.visibility = View.GONE
            meaningsLayout.visibility = View.VISIBLE
            previousButton.visibility = View.VISIBLE
        } else if (wordNumber == SET_SIZE - 1) {
            wordNumber++
            bottomText.visibility = View.INVISIBLE
            nextButton.visibility = View.INVISIBLE
            meaningsLayout.visibility = View.GONE
            finishLayout.visibility = View.VISIBLE
        } else {
            wordNumber++
            Log.i("Learn - Word number", wordNumber.toString())
            currentWord = wordsList[wordNumber]
            wordText.text = currentWord.word
            typeText.text = currentWord.type
            updateMeaningsRv()
            bottomText.text = StringBuilder((wordNumber + 1).toString()).append(" / ").append(SET_SIZE)
            if (firstWord) {
                firstWord = false
            }
        }
        Log.i("wordNumber", wordNumber.toString())
    }

    fun previousWord(view: View?) {
        if (firstWord) {
            wordsPreview = true
            firstWord = false
            wordsLayoutManager.smoothScrollToPosition(wordsRv, null, 0)
            bottomText.visibility = View.INVISIBLE
            wordsLayout.visibility = View.VISIBLE
            meaningsLayout.visibility = View.GONE
            previousButton.visibility = View.GONE
        } else if (wordNumber == SET_SIZE) {
            wordNumber--
            bottomText.visibility = View.VISIBLE
            nextButton.visibility = View.VISIBLE
            finishLayout.visibility = View.GONE
            meaningsLayout.visibility = View.VISIBLE
        } else {
            wordNumber--
            Log.i("Learn - Word number", wordNumber.toString())
            currentWord = wordsList[wordNumber]
            wordText.text = currentWord.word
            typeText.text = currentWord.type
            updateMeaningsRv()
            bottomText.text = StringBuilder((wordNumber + 1).toString()).append(" / ").append(SET_SIZE)
            if (wordNumber == 0) {
                firstWord = true
                //previousButton.setEnabled(false);
            }
        }
        Log.i("wordNumber", wordNumber.toString())
    }

    fun backToWordsList(view: View?) {
        wordsPreview = true
        firstWord = false
        bottomText.visibility = View.INVISIBLE
        wordsLayout.visibility = View.VISIBLE
        meaningsLayout.visibility = View.GONE
        previousButton.visibility = View.INVISIBLE
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
}