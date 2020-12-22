package com.zafaris.elevenplusvocab.ui.test

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
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.recyclerview.widget.DefaultItemAnimator
import com.muddzdev.styleabletoast.StyleableToast
import com.yuyakaido.android.cardstackview.*
import com.zafaris.elevenplusvocab.R
import com.zafaris.elevenplusvocab.ui.learn.Meaning
import com.zafaris.elevenplusvocab.ui.main.MainActivity
import com.zafaris.elevenplusvocab.utils.*

class TestActivity : AppCompatActivity(), CardStackListener, QuestionsCardStackAdapter.OnItemClickListener {
    private lateinit var db: WordBankDbAccess
    private lateinit var wordsList: List<Word>
    private var setNumber = 0
    private var score = 0
    private var questionNo = 0
    private var answerWord = ""
    private var answeredState = false
    private var finishedState = false
    private lateinit var answerIndexList: MutableList<Int>
    private lateinit var questionsList: MutableList<Question>
    private lateinit var randomWord: Word
    private lateinit var randomMeaning: Meaning

    private val cardStackView by lazy { findViewById<CardStackView>(R.id.test_card_stack_view) }
    private val manager by lazy { CardStackLayoutManager(this, this) }
    private lateinit var adapter: QuestionsCardStackAdapter

    private val backButton by lazy { findViewById<View>(R.id.test_back_button) }
    private val nextButton by lazy { findViewById<View>(R.id.test_next_button) }
    private lateinit var scoreDialog: Dialog

    private lateinit var mediaPlayer: MediaPlayer

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_test)

        val intent = intent
        setNumber = intent.getIntExtra("setNumber", 0)

        setupToolbar()

        getWords()
        generateAllQuestions()

        setupCardStackView()
        setupButtons()
    }

    private fun setupToolbar() {
        val toolbar = findViewById<Toolbar>(R.id.toolbar)
        toolbar.context.setTheme(R.style.ToolbarThemeDark)
        toolbar.setBackgroundColor(getColor(R.color.colorRed))
        toolbar.setTitleTextColor(getColor(R.color.textOnDark))
        window.statusBarColor = getColor(R.color.colorRedStatus)
        setSupportActionBar(toolbar)
        supportActionBar!!.title = "Test: Set $setNumber"
        supportActionBar!!.setDisplayHomeAsUpEnabled(true)
    }

    private fun setupCardStackView() {
        adapter = QuestionsCardStackAdapter(questionsList, this)

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

    override fun onOptionClick(userAnswerNo: Int) {
        if (!answeredState && !finishedState) {
            answeredState = true
            val currentQuestion = questionsList[questionNo]
            currentQuestion.userAnswerNo = userAnswerNo
            currentQuestion.isAnswered = true
            adapter.notifyItemChanged(questionNo)

            if (userAnswerNo == currentQuestion.answerNo) {
                playSound(R.raw.sfx_correct)
                score++
            } else {
                playSound(R.raw.sfx_incorrect)
            }
        }
    }

    private fun setupButtons() {
        val rewindSetting = RewindAnimationSetting.Builder()
                .setDirection(Direction.Left)
                .setDuration(Duration.Normal.duration)
                .setInterpolator(DecelerateInterpolator())
                .build()
        manager.setRewindAnimationSetting(rewindSetting)

        backButton.setOnClickListener { backButtonClick() }

        val swipeSetting = SwipeAnimationSetting.Builder()
                .setDirection(Direction.Left)
                .setDuration(Duration.Normal.duration)
                .setInterpolator(AccelerateInterpolator())
                .build()
        manager.setSwipeAnimationSetting(swipeSetting)

        nextButton.setOnClickListener { nextButtonClick() }
    }

    private fun showScoreDialog() {
        scoreDialog = Dialog(this)

        scoreDialog.setContentView(R.layout.popup_score_test)
        val scoreTitle = scoreDialog.findViewById<TextView>(R.id.test_scoreTitle)
        scoreTitle.text = "Score for Set $setNumber"
        val scoreText = scoreDialog.findViewById<TextView>(R.id.test_scoreText)
        scoreText.text = "$score / $NO_OF_QUESTIONS"

        val homeButton = scoreDialog.findViewById<Button>(R.id.test_homeButton)
        homeButton.setOnClickListener { goToHome() }

        scoreDialog.window!!.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        scoreDialog.show()
    }

    private fun backButtonClick() {
        playSound(R.raw.sfx_menu_click)

        cardStackView.rewind()
        questionNo = manager.topPosition
        Log.d("questionNo", questionNo.toString())
        if (manager.topPosition == 0) {
            backButton.visibility = View.INVISIBLE
        }
    }

    private fun nextButtonClick() {
        playSound(R.raw.sfx_menu_click)

        if (!answeredState && !finishedState) {
            //StyleableToast.makeText(this@TestActivity, "Please select an answer", R.style.errorToast).show()
        } else {
            answeredState = false
            // finished test
            if (manager.topPosition == questionsList.size - 1) {
                finishedState = true
                backButton.visibility = View.VISIBLE
                showScoreDialog()
            } else {
                cardStackView.swipe()
                questionNo = manager.topPosition + 1
                if (manager.topPosition == 0 && finishedState) {
                    backButton.visibility = View.VISIBLE
                }
            }
            Log.d("questionNo", questionNo.toString())
        }
    }

    private fun generateAnswerIndexList(): MutableList<Int> { // end = 25, size = 10
        val tmpList: MutableList<Int> = ArrayList()
        for (i in 0 until SET_SIZE) {
            tmpList.add(i)
        }
        tmpList.shuffle()
        return tmpList.subList(0, NO_OF_QUESTIONS)
    }

    private fun generateQuestionTypeList(): List<Int> {
        val randomList: MutableList<Int> = ArrayList()
        for (i in 0 until NO_OF_QUESTIONS) {
            randomList.add((0..1).random())
        }
        return randomList
    }

    private fun getWords() {
        db = WordBankDbAccess.getInstance(applicationContext)
        db.open()
        wordsList = db.getWordsList(setNumber)
        db.close()
    }

    private fun generateAllQuestions() {
        questionsList = ArrayList()

        var tmpQuestionNo = 1
        answerIndexList = generateAnswerIndexList()
        val questionTypeList = generateQuestionTypeList()

        // for each word index in random list of indexes, generate a question
        for (answerIndex in answerIndexList) {
            val questionType = questionTypeList[tmpQuestionNo - 1]
            Log.d("Test - tmpQuestionNo", tmpQuestionNo.toString())
            val answerNo = 1 + (Math.random() * NO_OF_OPTIONS).toInt()
            Log.d("Test - answerNo", answerNo.toString())

            // generate correct answer
            answerWord = generateAnswer(tmpQuestionNo, questionType)
            Log.d("Test - word", randomWord.word)
            Log.d("Test - type", randomWord.type)
            Log.d("Test - example", randomMeaning.example)
            Log.d("Test - questionType", questionType.toString())
            val optionsList = generateOptionsList(answerIndex, answerNo, questionType)
            Log.d("Test - option1", optionsList[0])
            Log.d("Test - option2", optionsList[1])
            Log.d("Test - option3", optionsList[2])
            Log.d("Test - option4", optionsList[3])

            //TODO: save indices to a list and generate unique words
            val tmpQuestion = Question(
                    randomWord.word,
                    randomWord.type,
                    randomMeaning.example,
                    questionType,
                    optionsList[0],
                    optionsList[1],
                    optionsList[2],
                    optionsList[3],
                    answerNo,
                    0,
                    false
            )
            questionsList.add(tmpQuestion)
            tmpQuestionNo++
        }
    }

    private fun generateAnswer(questionNo: Int, questionType: Int): String {
        var answer = "N/A"
        var answerIndex = answerIndexList[questionNo - 1]
        var emptyWord = true
        while (emptyWord) {
            val tmpWord = wordsList[answerIndex]
            val tmpMeaningList = tmpWord.meanings
            var tmpMeaning: Meaning
            tmpMeaning = if (tmpMeaningList.size > 1) {
                val tmpMeaningIndex = (Math.random() * tmpMeaningList.size).toInt()
                tmpMeaningList[tmpMeaningIndex]
            } else {
                tmpMeaningList[0]
            }

            answer = if (questionType == 0) {
                val synonymsList: Array<String> = tmpMeaning.synonyms.split(", ").toTypedArray()
                val tmpSynonymIndex = (Math.random() * synonymsList.size).toInt()
                synonymsList[tmpSynonymIndex]
            } else {
                val antonymsList: Array<String> = tmpMeaning.antonyms.split(", ").toTypedArray()
                val tmpAntonymIndex = (Math.random() * antonymsList.size).toInt()
                antonymsList[tmpAntonymIndex]
            }
            Log.d("Test - answer", answer)

            // check if answer
            if (answer == "N/A") {
                do {
                    answerIndex = (Math.random() * SET_SIZE).toInt()
                    Log.d("Test - answerIndex loop", answerIndexList.contains(answerIndex).toString())
                } while (answerIndexList.contains(answerIndex))
                answerIndexList[questionNo - 1] = answerIndex
            } else {
                randomWord = tmpWord
                randomMeaning = tmpMeaning
                emptyWord = false
            }
        }
        Log.d("Test - answer escaped", answer)
        return answer
    }

    private fun generateOptionsList(answerIndex: Int, answerNo: Int, questionType: Int): List<String> {
        val tmpWordsList: MutableList<String> = ArrayList()
        var word = "N/A"
        for (i in 1..NO_OF_OPTIONS) {
            var uniqueWord = false

            while (!uniqueWord) {

                // answer number
                if (i == answerNo) {
                    word = answerWord
                    uniqueWord = true
                    Log.d("Test - correct word", uniqueWord.toString())
                } else {
                    word = generateOption(answerIndex, questionType)

                    // checks if word is unique, breaks while loop
                    if (!tmpWordsList.contains(word)) {
                        uniqueWord = true
                    } else {
                        word = "N/A"
                    }
                    Log.d("Test - generateOption", uniqueWord.toString())
                }
            }

            Log.d("Test - Word", word)
            tmpWordsList.add(word)
        }
        return tmpWordsList
    }

    private fun generateOption(answerIndex: Int, questionType: Int): String {
        var option = "N/A"
        var tmpList: Array<String>
        var emptyWord = true
        while (emptyWord) {
            var tmpWordIndex: Int

            // generate random index, that is not the same as the answer index
            do {
                tmpWordIndex = (Math.random() * SET_SIZE).toInt()
            } while (tmpWordIndex == answerIndex)
            val tmpMeanings = wordsList[tmpWordIndex].meanings
            var tmpMeaning: Meaning

            tmpMeaning = if (tmpMeanings.size > 1) {
                val tmpMeaningIndex = (Math.random() * tmpMeanings.size).toInt()
                tmpMeanings[tmpMeaningIndex]
            } else {
                tmpMeanings[0]
            }

            tmpList =
                // synonym
                if (questionType == 0) {
                    tmpMeaning.synonyms.split(", ").toTypedArray()
                // antonym
                } else {
                    tmpMeaning.antonyms.split(", ").toTypedArray()
                }
            val tmpOptionIndex = (Math.random() * tmpList.size).toInt()
            option = tmpList[tmpOptionIndex]
            if (option != "N/A") {
                emptyWord = false
            }
        }
        return option
    }

    private fun playSound(resourceId: Int) {
        mediaPlayer = MediaPlayer.create(this@TestActivity, resourceId)
        if (mediaPlayer.isPlaying) {
            mediaPlayer.release()
        }
        mediaPlayer.start()
    }

    private fun goToHome() {
        val intent = Intent(this@TestActivity, MainActivity::class.java)
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
        val textView = view?.findViewById<TextView>(R.id.card_test_questionText)
        Log.d("CardStackView", "onCardAppeared: ($position) ${textView?.text}")
    }

    override fun onCardDisappeared(view: View?, position: Int) {
        val textView = view?.findViewById<TextView>(R.id.card_test_questionText)
        Log.d("CardStackView", "onCardDisappeared: ($position) ${textView?.text}")
    }
}