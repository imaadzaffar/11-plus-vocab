package com.zafaris.learnvocab.ui.test

import android.content.Intent
import android.content.res.ColorStateList
import android.media.MediaPlayer
import android.os.Bundle
import android.text.SpannableString
import android.text.Spanned
import android.text.style.UnderlineSpan
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.constraintlayout.widget.ConstraintLayout
import com.muddzdev.styleabletoast.StyleableToast
import com.zafaris.learnvocab.R
import com.zafaris.learnvocab.ui.learn.Meaning
import com.zafaris.`11plusvocab`.ui.main.MainActivity
import com.zafaris.learnvocab.utils.Word
import com.zafaris.`11plusvocab`.utils.WordBankDbAccess
import kotlin.collections.ArrayList

class TestActivity : AppCompatActivity() {
    private lateinit var db: WordBankDbAccess
    private lateinit var testLayout: ConstraintLayout
    private lateinit var finishLayout: ConstraintLayout
    private lateinit var bottomLayout: ConstraintLayout
    private lateinit var wordText: TextView
    private lateinit var typeText: TextView
    private lateinit var exampleText: TextView
    private lateinit var questionText: TextView
    private lateinit var bottomText: TextView
    private lateinit var finishTitle: TextView
    private lateinit var scoreText: TextView
    private lateinit var option1: Button
    private lateinit var option2: Button
    private lateinit var option3: Button
    private lateinit var option4: Button
    private lateinit var nextButton: Button

    private lateinit var defaultTextColor: ColorStateList
    private lateinit var mediaPlayer: MediaPlayer
    private lateinit var wordsList: List<Word>
    private lateinit var questionsList: MutableList<Question>
    private lateinit var currentQuestion: Question
    private var setNumber = 0
    private var questionNumber = 0
    private var questionCountTotal = 0
    private var optionCountTotal = 0
    private var answerWord: String = ""
    private lateinit var randomWord: Word
    private lateinit var randomMeaning: Meaning
    private var score = 0
    private var selectedAnswer = 0
    private lateinit var answerIndexList: MutableList<Int>
    private var answeredState = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_test)
        val intent = intent
        setNumber = intent.getIntExtra("setNumber", 0)
        val toolbar = findViewById<Toolbar>(R.id.toolbar)
        toolbar.context.setTheme(R.style.ToolbarThemeDark)
        toolbar.setBackgroundColor(getColor(R.color.colorRed))
        toolbar.setTitleTextColor(getColor(R.color.textOnDark))
        window.statusBarColor = getColor(R.color.colorRedStatus)
        setSupportActionBar(toolbar)
        supportActionBar!!.title = "Test: Set $setNumber"
        //getSupportActionBar().setSubtitle(new StringBuilder("Set ").append(setNumber));
        supportActionBar!!.setDisplayHomeAsUpEnabled(true)
        setNumber = 1 //TODO: Delete this once all sets have been added
        testLayout = findViewById(R.id.test_testLayout)
        testLayout.visibility = View.VISIBLE
        finishLayout = findViewById(R.id.test_finishLayout)
        finishLayout.visibility = View.GONE
        bottomLayout = findViewById(R.id.test_bottomLayout)
        bottomLayout.visibility = View.VISIBLE
        wordText = findViewById(R.id.test_wordText)
        typeText = findViewById(R.id.test_typeText)
        exampleText = findViewById(R.id.test_exampleText)
        bottomText = findViewById(R.id.test_bottomText)
        questionText = findViewById(R.id.test_questionText)
        finishTitle = findViewById(R.id.test_finishTitle)
        finishTitle.text = "Results for Set $setNumber"
        scoreText = findViewById(R.id.test_scoreText)
        nextButton = findViewById(R.id.test_nextButton)
        option1 = findViewById(R.id.test_option1)
        option2 = findViewById(R.id.test_option2)
        option3 = findViewById(R.id.test_option3)
        option4 = findViewById(R.id.test_option4)
        defaultTextColor = option1.textColors
        questionCountTotal = 10
        optionCountTotal = 4
        questionNumber = 0
        selectedAnswer = 0
        answeredState = false
        generateAllQuestions()
        currentQuestion = questionsList[0]
        showNextQuestion()
        nextButton.setOnClickListener(View.OnClickListener {
            if (!answeredState) {
                if (selectedAnswer != 0) {
                    checkAnswer()
                } else {
                    Toast.makeText(this@TestActivity, "Please select an answer", Toast.LENGTH_SHORT).show()
                    StyleableToast.makeText(this@TestActivity, "Please select an answer", R.style.errorToast).show()
                }
            } else {
                showNextQuestion()
            }
        })
    }

    private fun showNextQuestion() {
        option1.setTextColor(defaultTextColor)
        option2.setTextColor(defaultTextColor)
        option3.setTextColor(defaultTextColor)
        option4.setTextColor(defaultTextColor)
        option1.background = getDrawable(R.drawable.bg_answer_button)
        option2.background = getDrawable(R.drawable.bg_answer_button)
        option3.background = getDrawable(R.drawable.bg_answer_button)
        option4.background = getDrawable(R.drawable.bg_answer_button)
        if (questionNumber < questionCountTotal) {
            currentQuestion = questionsList[questionNumber]
            selectedAnswer = 0
            Log.i("Test - questionNumber", questionNumber.toString())
            Log.i("Test - word", currentQuestion.word)
            Log.i("Test - type", currentQuestion.type)
            Log.i("Test - example", currentQuestion.example)
            val word = currentQuestion.word
            wordText.text = word
            typeText.text = currentQuestion.type
            val exampleString = currentQuestion.example
            val exampleSentence = SpannableString(exampleString)
            val underlineSpan = UnderlineSpan()
            val startIndex = exampleString.indexOf(word)
            val endIndex = startIndex + word.length
            exampleSentence.setSpan(underlineSpan, startIndex, endIndex, Spanned.SPAN_EXCLUSIVE_INCLUSIVE)
            exampleText.text = exampleSentence

            // set synonym text
            if (currentQuestion.questionType == 0) {
                questionText.text = "Choose the most similar word:"

                // set antonym text
            } else {
                questionText.text = "Choose the opposite word:"
            }
            option1.text = currentQuestion!!.option1
            option2.text = currentQuestion!!.option2
            option3.text = currentQuestion!!.option3
            option4.text = currentQuestion!!.option4
            bottomText.text = "${(questionNumber + 1)} / $questionCountTotal"
            answeredState = false
            nextButton.text = "Check"
            questionNumber++
        } else {
            scoreText.text = "$score / $questionCountTotal"
            testLayout.visibility = View.GONE
            finishLayout.visibility = View.VISIBLE
            bottomLayout.visibility = View.GONE
        }
    }

    fun optionClick(view: View) {
        if (!answeredState) {
            option1.setTextColor(defaultTextColor)
            option2.setTextColor(defaultTextColor)
            option3.setTextColor(defaultTextColor)
            option4.setTextColor(defaultTextColor)
            option1.background = getDrawable(R.drawable.bg_answer_button)
            option2.background = getDrawable(R.drawable.bg_answer_button)
            option3.background = getDrawable(R.drawable.bg_answer_button)
            option4.background = getDrawable(R.drawable.bg_answer_button)
            val selectedButton = view as Button
            val tag = Integer.valueOf(selectedButton.tag.toString())
            if (tag != selectedAnswer) {
                selectedAnswer = tag
                selectedButton.background = getDrawable(R.drawable.bg_answer_selected)
            } else {
                selectedAnswer = 0
                selectedButton.background = getDrawable(R.drawable.bg_answer_button)
            }
        }
    }

    private fun checkAnswer() {
        answeredState = true
        if (selectedAnswer == currentQuestion.answerNo) {
            score++
            mediaPlayer = MediaPlayer.create(this, R.raw.sfx_correct)
            if (mediaPlayer.isPlaying) {
                mediaPlayer.release()
            }
            mediaPlayer.start()
        } else {
            val stringId = "test_option$selectedAnswer"
            val intId = resources.getIdentifier(stringId, "id", "com.zafaris.learnvocab")
            val incorrect = findViewById<Button>(intId)
            incorrect.background = getDrawable(R.drawable.bg_answer_incorrect)
            incorrect.setTextColor(getColor(R.color.colorIncorrectDark))
            mediaPlayer = MediaPlayer.create(this, R.raw.sfx_incorrect)
            if (mediaPlayer.isPlaying) {
                mediaPlayer.release()
            }
            mediaPlayer.start()
        }
        showSolution()
    }

    private fun showSolution() {
        when (currentQuestion.answerNo) {
            1 -> {
                option1.setTextColor(getColor(R.color.colorCorrectDark))
                option1.background = getDrawable(R.drawable.bg_answer_correct)
            }
            2 -> {
                option2.setTextColor(getColor(R.color.colorCorrectDark))
                option2.background = getDrawable(R.drawable.bg_answer_correct)
            }
            3 -> {
                option3.setTextColor(getColor(R.color.colorCorrectDark))
                option3.background = getDrawable(R.drawable.bg_answer_correct)
            }
            4 -> {
                option4.setTextColor(getColor(R.color.colorCorrectDark))
                option4.background = getDrawable(R.drawable.bg_answer_correct)
            }
        }
        if (questionNumber < questionCountTotal) {
            nextButton.text = "Next"
        } else {
            nextButton.text = "Finish"
        }
    }

    private fun shuffleRandomList(end: Int, size: Int): MutableList<Int> { // end = 25, size = 10
        val tmpList: MutableList<Int> = ArrayList()
        for (i in 0 until end) {
            tmpList.add(i)
        }
        tmpList.shuffle()
        return tmpList.subList(0, size)
    }

    private fun generateRandomList(size: Int, min: Int, max: Int): MutableList<Int> {
        val randomList: MutableList<Int> = ArrayList()
        for (i in 0 until size) {
            val randomNumber = min + (Math.random() * (max - min + 1)).toInt()
            randomList.add(randomNumber)
        }
        return randomList
    }

    private fun generateAllQuestions() {
        questionsList = ArrayList()
        db = WordBankDbAccess.getInstance(applicationContext)
        db.open()
        wordsList = db.getWordsList(setNumber)
        db.close()
        var tmpQuestionNo = 1
        answerIndexList = shuffleRandomList(MainActivity.setSize, questionCountTotal) // generate random list of indexes for words in set eg. [3, 15, 7, 23, 12]
        val questionTypeList = generateRandomList(questionCountTotal, 0, 1)

        // generate list for either synonym or antonym
        //List<Integer> randomTypes = generateRandomList(optionsCountTotal, 0,  1);

        // for each word index in random list of indexes, generate a question
        for (answerIndex in answerIndexList) {
            val questionType = questionTypeList[tmpQuestionNo - 1]
            Log.i("Test - questionNo", tmpQuestionNo.toString())
            val answerNo = 1 + (Math.random() * optionCountTotal).toInt()
            Log.i("Test - answerNo", answerNo.toString())

            // generate correct answer
            answerWord = generateAnswer(tmpQuestionNo, questionType)
            Log.i("Test - word", randomWord.word)
            Log.i("Test - type", randomWord.type)
            Log.i("Test - example", randomMeaning.example)
            Log.i("Test - questionType", questionType.toString())
            val optionsList = generateOptionsList(questionType, answerNo)
            Log.i("Test - option1", optionsList[0])
            Log.i("Test - option2", optionsList[1])
            Log.i("Test - option3", optionsList[2])
            Log.i("Test - option4", optionsList[3])

            //TODO: save indexes to a list and generate unique words
            val tmpQuestion = Question(
                    randomWord.word,
                    randomWord.type,
                    randomMeaning.example,
                    questionType,
                    optionsList[0],
                    optionsList[1],
                    optionsList[2],
                    optionsList[3],
                    answerNo
            )
            questionsList.add(tmpQuestion)
            tmpQuestionNo++
        }
    }

    private fun generateOptionsList(questionType: Int, answerNo: Int): List<String> {
        val tmpWordsList: MutableList<String> = ArrayList()
        var word: String = "N/A"
        for (i in 1 until (optionCountTotal + 1)) {
            var uniqueWord = false
            while (!uniqueWord) {

                // question type is synonym
                if (questionType == 0) {

                    // correct synonym
                    if (i == answerNo) {
                        word = answerWord
                        uniqueWord = true
                        Log.i("Test - correct Synonym", uniqueWord.toString())
                    } else {
                        word = generateSynonym()
                        // checks: if word is unique, break while loop
                        if (!tmpWordsList.contains(word)) {
                            uniqueWord = true
                        } else {
                            word = "N/A"
                        }
                        Log.i("Test - generateSynonym", uniqueWord.toString())
                    }
                } else {

                    // correct antonym
                    if (i == answerNo) {
                        word = answerWord
                        uniqueWord = true
                        Log.i("Test - correct Antonym", uniqueWord.toString())
                    } else {
                        word = generateAntonym()
                        // checks: if word is unique, break while loop
                        if (!tmpWordsList.contains(word)) {
                            uniqueWord = true
                        } else {
                            word = "N/A"
                        }
                        Log.i("Test - generateAntonym", uniqueWord.toString())
                    }
                }
            }
            Log.i("Test - Word", word)
            tmpWordsList.add(word)
        }
        return tmpWordsList
    }

    /*public List<String> generateWordsListOld (int questionNo, int questionType, Meaning meaning, int answerNo) {
        List<Integer> typesList = generateRandomList(questionCountTotal, 0, 1);
        Integer[] answerIndexList = shuffleRandomList(MainActivity.setSize, questionCountTotal); // generate random list of indexes for words in set eg. [3, 15, 7, 23, 12]

        List<String> tmpWordsList = new ArrayList<>();
        String answerWord;

        // generate correct answer
        // TODO: Add answer index in words list to answerIndexList[questionNo - 1] = answerIndex
        //       only if !answerWord.equals("N/A")
        //       else generate new answer index and check again
        //       in while loop -
        //       answerWord = "N/A"
        //       while (answerWord.equals("N/A")) {}
        if (questionType == 0) {
            answerWord = generateSynonym(questionNo, true, meaning);
        } else {
            answerWord = generateAntonym(questionNo, true, meaning);
        }

        for (int i = 0; i < optionCountTotal; i++) {
            String word = "";
            boolean uniqueWord = false;

            while (!uniqueWord) {

                // question type is synonym
                if (questionType == 0) {

                    // correct synonym
                    if (i + 1 == answerNo) {
                        word = answerWord;
                        uniqueWord = true;
                    }

                    // generate random synonym
                    else {
                        word = generateSynonym(questionNo, false, meaning);
                        // checks: if word is unique, break while loop
                        if (!tmpWordsList.contains(word) && !word.equals(answerWord) && !word.equals("N/A")) {
                            uniqueWord = true;
                        } else {
                            word = "";
                        }
                    }
                }

                // question type is antonym
                else {

                    // correct antonym
                    if (i + 1 == answerNo) {
                        word = answerWord;
                        uniqueWord = true;
                    }

                    // generate random antonym
                    else {
                        word = generateAntonym(questionNo, false, meaning);
                        // checks: if word is unique, break while loop
                        if (!tmpWordsList.contains(word) && !word.equals(answerWord) && !word.equals("N/A")) {
                            uniqueWord = true;
                        } else {
                            word = "";
                        }
                    }
                }
            }

            Log.i("Test - Word", word);
            tmpWordsList.add(word);
        }

        return tmpWordsList;
    }*/

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

            // synonym
            answer = if (questionType == 0) {
                val synonymsList: Array<String> = tmpMeaning.synonyms.split(", ").toTypedArray()
                val tmpSynonymIndex = (Math.random() * synonymsList.size).toInt()
                synonymsList[tmpSynonymIndex]
            } else {
                val antonymsList: Array<String> = tmpMeaning.antonyms.split(", ").toTypedArray()
                val tmpAntonymIndex = (Math.random() * antonymsList.size).toInt()
                antonymsList[tmpAntonymIndex]
            }
            Log.i("Test - answerWord", answer)

            // check if answer
            if (answer == "N/A") {
                do {
                    answerIndex = (Math.random() * MainActivity.setSize).toInt()
                    Log.i("Test - answerIndex loop", answerIndexList.contains(answerIndex).toString())
                } while (answerIndexList.contains(answerIndex))
                answerIndexList[questionNo - 1] = answerIndex
            } else {
                randomWord = tmpWord
                randomMeaning = tmpMeaning
                emptyWord = false
            }
        }
        Log.i("Test - answer escaped", answer)
        return answer
    }

    private fun generateSynonym(): String {
        var synonym = "N/A"
        var synonymsList: Array<String>
        var emptyWord = true
        while (emptyWord) {
            var tmpWordIndex: Int

            // generate random index, that is not the same as the answer index
            do {
                tmpWordIndex = (Math.random() * MainActivity.setSize).toInt()
            } while (answerIndexList.contains(tmpWordIndex))
            val tmpMeanings = wordsList[tmpWordIndex].meanings
            var tmpMeaning: Meaning
            tmpMeaning = if (tmpMeanings.size > 1) {
                val tmpMeaningIndex = (Math.random() * tmpMeanings.size).toInt()
                tmpMeanings[tmpMeaningIndex]
            } else {
                tmpMeanings[0]
            }
            synonymsList = tmpMeaning.synonyms.split(", ").toTypedArray()
            val tmpSynonymIndex = (Math.random() * synonymsList.size).toInt()
            synonym = synonymsList[tmpSynonymIndex]
            if (synonym != "N/A") {
                emptyWord = false
            }
        }
        return synonym
    }

    private fun generateAntonym(): String {
        var antonym = "N/A"
        var antonymsList: Array<String>
        var emptyWord = true
        while (emptyWord) {
            var tmpWordIndex: Int

            // generate random index, that is not the same as the answer index
            do {
                tmpWordIndex = (Math.random() * MainActivity.setSize).toInt()
            } while (answerIndexList.contains(tmpWordIndex))
            val tmpMeanings = wordsList[tmpWordIndex].meanings
            var tmpMeaning: Meaning
            tmpMeaning = if (tmpMeanings.size > 1) {
                val tmpMeaningIndex = (Math.random() * tmpMeanings.size).toInt()
                tmpMeanings[tmpMeaningIndex]
            } else {
                tmpMeanings[0]
            }
            antonymsList = tmpMeaning.antonyms.split(", ").toTypedArray()
            val tmpAntonymIndex = (Math.random() * antonymsList.size).toInt()
            antonym = antonymsList[tmpAntonymIndex]
            if (antonym != "N/A") {
                emptyWord = false
            }
        }
        return antonym
    }

    fun goToHome(view: View?) {
        val intent = Intent(this@TestActivity, MainActivity::class.java)
        intent.putExtra("setNumber", setNumber)
        startActivity(intent)
    }
}