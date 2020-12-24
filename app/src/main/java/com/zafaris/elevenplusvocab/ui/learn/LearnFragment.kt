package com.zafaris.elevenplusvocab.ui.learn

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
import android.widget.Button
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.DefaultItemAnimator
import com.yuyakaido.android.cardstackview.*
import com.zafaris.elevenplusvocab.R
import com.zafaris.elevenplusvocab.data.model.Word
import com.zafaris.elevenplusvocab.util.WordBankDbAccess

class LearnFragment : Fragment(), CardStackListener {
    private val args: LearnFragmentArgs by navArgs()

    private lateinit var db: WordBankDbAccess
    private lateinit var wordsList: List<Word>
    private var setNo = 0

    private lateinit var cardStackView: CardStackView
    private lateinit var manager: CardStackLayoutManager
    private val adapter by lazy { WordsCardStackAdapter(getWords()) }

    private lateinit var backButton: View
    private lateinit var nextButton: View
    private lateinit var finishDialog: Dialog

    private lateinit var mediaPlayer: MediaPlayer

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view =  inflater.inflate(R.layout.fragment_learn, container, false)

        cardStackView = view.findViewById(R.id.learn_card_stack_view)
        backButton = view.findViewById(R.id.learn_back_button)
        nextButton = view.findViewById(R.id.learn_next_button)

        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        setNo = args.setNo

        setupCardStackView()
        setupButtons()
    }

    private fun setupCardStackView() {
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
        cardStackView.layoutManager = manager
        cardStackView.adapter = adapter
        cardStackView.itemAnimator.apply {
            if (this is DefaultItemAnimator) {
                supportsChangeAnimations = false
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

    private fun getWords(): List<Word> {
        // open database and get wordsList
        db = WordBankDbAccess.getInstance(requireActivity().applicationContext)
        db.open()
        wordsList = db.getWordsList(setNo)
        db.close()
        return wordsList
    }

    private fun showFinishDialog() {
        finishDialog = Dialog(requireContext())

        finishDialog.setContentView(R.layout.learn_dialog_finish)
        val finishTitle = finishDialog.findViewById<TextView>(R.id.learn_finishTitle)
        finishTitle.text = "Finished Set $setNo"

        val testButton = finishDialog.findViewById<Button>(R.id.learn_testButton)
        testButton.setOnClickListener { navigateAction("test") }
        val homeButton = finishDialog.findViewById<Button>(R.id.learn_homeButton)
        homeButton.setOnClickListener { navigateAction("home") }

        finishDialog.window!!.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        finishDialog.show()
    }

    private fun navigateAction(destination: String) {
        playButtonClickSound()
        finishDialog.dismiss()

        if (destination == "home") {
            findNavController().navigate(R.id.action_to_homeFragment)
        } else {
            val action = when (destination) {
                "test" -> LearnFragmentDirections.actionLearnFragmentToTestFragment(setNo)
                else -> throw IllegalArgumentException("Invalid destination")
            }
            findNavController().navigate(action)
        }
    }

    private fun playButtonClickSound() {
        mediaPlayer = MediaPlayer.create(context, R.raw.sfx_menu_click)
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
        val textView = view?.findViewById<TextView>(R.id.card_wordText)
        Log.d("CardStackView", "onCardAppeared: ($position) ${textView?.text}")
    }

    override fun onCardDisappeared(view: View?, position: Int) {
        val textView = view?.findViewById<TextView>(R.id.card_wordText)
        Log.d("CardStackView", "onCardDisappeared: ($position) ${textView?.text}")
    }
}