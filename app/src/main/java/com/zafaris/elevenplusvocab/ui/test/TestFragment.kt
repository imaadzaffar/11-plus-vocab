package com.zafaris.elevenplusvocab.ui.test

import android.app.Dialog
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.media.MediaPlayer
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.AccelerateInterpolator
import android.view.animation.DecelerateInterpolator
import android.view.animation.LinearInterpolator
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.DefaultItemAnimator
import com.yuyakaido.android.cardstackview.*
import com.zafaris.elevenplusvocab.R
import com.zafaris.elevenplusvocab.databinding.FragmentTestBinding
import com.zafaris.elevenplusvocab.databinding.TestDialogScoreBinding
import com.zafaris.elevenplusvocab.util.NO_OF_QUESTIONS

class TestFragment : Fragment(), CardStackListener, QuestionsCardStackAdapter.OnItemClickListener {
    private val args: TestFragmentArgs by navArgs()
    private var _binding: FragmentTestBinding? = null
    private var _scoreDialogBinding: TestDialogScoreBinding? = null
    private val binding get() = _binding!!
    private val scoreDialogBinding get() = _scoreDialogBinding!!

    private val model: TestViewModel by viewModels()

    private lateinit var scoreDialog: Dialog
    private lateinit var mediaPlayer: MediaPlayer

    private lateinit var manager: CardStackLayoutManager
    private lateinit var adapter: QuestionsCardStackAdapter

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        _binding = FragmentTestBinding.inflate(inflater, container, false)
        val view =  binding.root

        _scoreDialogBinding = TestDialogScoreBinding.inflate(inflater)

        scoreDialog = Dialog(requireContext())

        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        model.setNo = args.setNo
        model.getWords()
        model.generateAllQuestions()
        Log.d("TestVM - answeredState", model.answeredState.toString())
        Log.d("TestVM - completedState", model.completedState.toString())

        setupCardStackView()
        setupButtons()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
        _scoreDialogBinding = null
    }

    private fun setupCardStackView() {
        adapter = QuestionsCardStackAdapter(model.questionsList, this)
        manager = CardStackLayoutManager(context, this)

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
        binding.cardsQuestions.layoutManager = manager
        binding.cardsQuestions.adapter = adapter
        binding.cardsQuestions.itemAnimator.apply {
            if (this is DefaultItemAnimator) {
                supportsChangeAnimations = false
            }
        }
    }

    override fun onOptionClick(userAnswerNo: Int) {
        if (!model.answeredState && !model.completedState) {
            model.answeredState = true
            val currentQuestion = model.questionsList[model.questionNo]
            currentQuestion.userAnswerNo = userAnswerNo
            currentQuestion.isAnswered = true
            adapter.notifyItemChanged(model.questionNo)

            if (userAnswerNo == currentQuestion.answerNo) {
                playSound(R.raw.sfx_correct)
                model.score++
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

        binding.buttonBack.setOnClickListener { backButtonClick() }

        val swipeSetting = SwipeAnimationSetting.Builder()
                .setDirection(Direction.Left)
                .setDuration(Duration.Normal.duration)
                .setInterpolator(AccelerateInterpolator())
                .build()
        manager.setSwipeAnimationSetting(swipeSetting)

        binding.buttonNext.setOnClickListener { nextButtonClick() }
    }

    private fun showScoreDialog() {
        scoreDialog.setContentView(scoreDialogBinding.root)
        scoreDialogBinding.titleDialog.text = "Score for Set ${model.setNo}"
        scoreDialogBinding.textScore.text = "${model.score} / $NO_OF_QUESTIONS"

        scoreDialogBinding.buttonViewQuestions.setOnClickListener { viewQuestionsButtonClick() }
        scoreDialogBinding.buttonHome.setOnClickListener { navigateAction("home") }

        scoreDialog.window!!.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        scoreDialog.show()
    }

    private fun backButtonClick() {
        playSound(R.raw.sfx_click_button_2)

        binding.cardsQuestions.rewind()
        model.questionNo = manager.topPosition
        Log.d("questionNo", model.questionNo.toString())
        if (manager.topPosition == 0) {
            binding.buttonBack.visibility = View.INVISIBLE
        }
    }

    private fun nextButtonClick() {
        if (model.completedState || model.answeredState) {
            playSound(R.raw.sfx_click_button_2)

            model.answeredState = false
            // completed test
            if (manager.topPosition == model.questionsList.size - 1) {
                onTestComplete()
            } else {
                binding.cardsQuestions.swipe()
                model.questionNo = manager.topPosition + 1
                if (manager.topPosition == 0 && model.completedState) {
                    binding.buttonBack.visibility = View.VISIBLE
                }
            }
            Log.d("questionNo", model.questionNo.toString())
        }
    }

    private fun onTestComplete() {
        model.completedState = true
        binding.buttonBack.visibility = View.VISIBLE
        showScoreDialog()
    }

    private fun viewQuestionsButtonClick() {
        scoreDialog.dismiss()
        manager.smoothScrollToPosition(binding.cardsQuestions, null, 0)
        binding.buttonBack.visibility = View.INVISIBLE
    }

    private fun navigateAction(destination: String) {
        playSound(R.raw.sfx_click_button)
        scoreDialog.dismiss()

        if (destination == "home") {
            findNavController().navigate(R.id.action_global_home)
        } else {
            val action = when (destination) {
                "stats" -> TestFragmentDirections.actionTestFragmentToStatsFragment(model.setNo)
                else -> throw IllegalArgumentException("Invalid destination")
            }
            findNavController().navigate(action)
        }
    }

    private fun playSound(resourceId: Int) {
        mediaPlayer = MediaPlayer.create(context, resourceId)
        if (mediaPlayer.isPlaying) {
            mediaPlayer.release()
        }
        mediaPlayer.start()
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
        val textView = view?.findViewById<TextView>(R.id.text_question)
        Log.d("CardStackView", "onCardAppeared: ($position) ${textView?.text}")
    }

    override fun onCardDisappeared(view: View?, position: Int) {
        val textView = view?.findViewById<TextView>(R.id.text_question)
        Log.d("CardStackView", "onCardDisappeared: ($position) ${textView?.text}")
    }
}