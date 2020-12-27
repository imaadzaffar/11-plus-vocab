package com.zafaris.elevenplusvocab.ui.learn

import android.app.Dialog
import android.content.Context
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.media.MediaPlayer
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.Menu
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
import com.yuyakaido.android.cardstackview.CardStackLayoutManager
import com.yuyakaido.android.cardstackview.CardStackListener
import com.yuyakaido.android.cardstackview.Direction
import com.yuyakaido.android.cardstackview.Duration
import com.yuyakaido.android.cardstackview.RewindAnimationSetting
import com.yuyakaido.android.cardstackview.StackFrom
import com.yuyakaido.android.cardstackview.SwipeAnimationSetting
import com.yuyakaido.android.cardstackview.SwipeableMethod
import com.zafaris.elevenplusvocab.R
import com.zafaris.elevenplusvocab.databinding.FragmentLearnBinding
import com.zafaris.elevenplusvocab.databinding.LearnDialogFinishBinding

class LearnFragment : Fragment(), CardStackListener {
    private val args: LearnFragmentArgs by navArgs()
    private var _binding: FragmentLearnBinding? = null
    private var _finishDialogBinding: LearnDialogFinishBinding? = null
    private val binding get() = _binding!!
    private val finishDialogBinding get() = _finishDialogBinding!!

    private val model: LearnViewModel by viewModels()

    private lateinit var finishDialog: Dialog
    private lateinit var mediaPlayer: MediaPlayer

    private lateinit var manager: CardStackLayoutManager
    private lateinit var adapter: WordsCardStackAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setHasOptionsMenu(true)
    }

    override fun onPrepareOptionsMenu(menu: Menu) {
        super.onPrepareOptionsMenu(menu)
        menu.clear()
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        _binding = FragmentLearnBinding.inflate(inflater, container, false)
        val view =  binding.root

        _finishDialogBinding = LearnDialogFinishBinding.inflate(inflater)

        finishDialog = Dialog(requireContext())

        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        model.setNo = args.setNo
        model.getWords()

        setupCardStackView()
        setupButtons()
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        model.setNo = args.setNo
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    private fun setupCardStackView() {
        adapter = WordsCardStackAdapter(model.wordsList)
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
        binding.cardsWords.layoutManager = manager
        binding.cardsWords.adapter = adapter
        binding.cardsWords.itemAnimator.apply {
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

        binding.buttonBack.setOnClickListener {
            playSound(R.raw.sfx_click_button_2)
            binding.cardsWords.rewind()
            if (manager.topPosition == 0) {
                binding.buttonBack.visibility = View.INVISIBLE
            }
        }

        val swipeSetting = SwipeAnimationSetting.Builder()
                .setDirection(Direction.Left)
                .setDuration(Duration.Normal.duration)
                .setInterpolator(AccelerateInterpolator())
                .build()
        manager.setSwipeAnimationSetting(swipeSetting)

        binding.buttonNext.setOnClickListener {
            playSound(R.raw.sfx_click_button_2)
            if (manager.topPosition == model.getWordsListSize() - 1) {
                showFinishDialog()
            } else {
                binding.cardsWords.swipe()
                if (manager.topPosition == 0) {
                    binding.buttonBack.visibility = View.VISIBLE
                }
            }
        }
    }


    private fun showFinishDialog() {
        finishDialog.setContentView(finishDialogBinding.root)
        finishDialogBinding.titleDialog.text = "Finished Set ${model.setNo}"

        finishDialogBinding.buttonTest.setOnClickListener { navigateAction("test") }
        finishDialogBinding.buttonHome.setOnClickListener { navigateAction("home") }

        finishDialog.window!!.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        finishDialog.show()
    }

    private fun navigateAction(destination: String) {
        playSound(R.raw.sfx_click_button)
        finishDialog.dismiss()

        if (destination == "home") {
            findNavController().navigate(R.id.action_global_home)
        } else {
            val action = when (destination) {
                "test" -> LearnFragmentDirections.actionLearnFragmentToTestFragment(model.setNo)
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
        val textView = view?.findViewById<TextView>(R.id.text_word)
        Log.d("CardStackView", "onCardAppeared: ($position) ${textView?.text}")
    }

    override fun onCardDisappeared(view: View?, position: Int) {
        val textView = view?.findViewById<TextView>(R.id.text_word)
        Log.d("CardStackView", "onCardDisappeared: ($position) ${textView?.text}")
    }
}