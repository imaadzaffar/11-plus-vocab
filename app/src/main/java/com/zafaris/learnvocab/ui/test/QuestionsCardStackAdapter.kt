package com.zafaris.learnvocab.ui.test

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.zafaris.learnvocab.R
import com.zafaris.learnvocab.data.model.Question
import com.zafaris.learnvocab.databinding.TestCardQuestionBinding

class QuestionsCardStackAdapter(
        private val questions: List<Question> = emptyList(),
        private val listener: OnItemClickListener
) : RecyclerView.Adapter<QuestionsCardStackAdapter.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, itemViewType: Int): ViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val binding = TestCardQuestionBinding.inflate(inflater, parent, false)
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val question = questions[position]
        holder.bind(question)
    }

    override fun getItemCount(): Int {
        return questions.size
    }

    inner class ViewHolder(private val binding: TestCardQuestionBinding) : RecyclerView.ViewHolder(binding.root), View.OnClickListener {
        fun bind(question: Question) {
            binding.textNumber.text = (adapterPosition + 1).toString()

            val word = question.word
            val questionTypeString = if (question.questionType == 0) "synonym" else "antonym"

            binding.textType.text = questionTypeString
            val questionString = "Choose the $questionTypeString for $word"
            binding.textQuestion.text = questionString
            binding.textExample.text = question.example

            binding.buttonOption1.text = question.option1
            binding.buttonOption2.text = question.option2
            binding.buttonOption3.text = question.option3
            binding.buttonOption4.text = question.option4

            binding.buttonOption1.setOnClickListener(this)
            binding.buttonOption2.setOnClickListener(this)
            binding.buttonOption3.setOnClickListener(this)
            binding.buttonOption4.setOnClickListener(this)

            if (question.isAnswered) {
                when (question.answerNo) {
                    1 -> {
                        binding.buttonOption1.background = ContextCompat.getDrawable(binding.textQuestion.context, R.drawable.button_green)
                        binding.buttonOption1.setIconResource(R.drawable.ic_icon_correct)
                    }
                    2 -> {
                        binding.buttonOption2.background = ContextCompat.getDrawable(binding.textQuestion.context, R.drawable.button_green)
                        binding.buttonOption2.setIconResource(R.drawable.ic_icon_correct)
                    }
                    3 -> {
                        binding.buttonOption3.background = ContextCompat.getDrawable(binding.textQuestion.context, R.drawable.button_green)
                        binding.buttonOption3.setIconResource(R.drawable.ic_icon_correct)
                    }
                    4 -> {
                        binding.buttonOption4.background = ContextCompat.getDrawable(binding.textQuestion.context, R.drawable.button_green)
                        binding.buttonOption4.setIconResource(R.drawable.ic_icon_correct)
                    }
                }
                if (question.userAnswerNo != question.answerNo) {
                    when (question.userAnswerNo) {
                        1 -> {
                            binding.buttonOption1.background = ContextCompat.getDrawable(binding.textQuestion.context, R.drawable.button_red)
                            binding.buttonOption1.setIconResource(R.drawable.ic_icon_incorrect)
                        }
                        2 -> {
                            binding.buttonOption2.background = ContextCompat.getDrawable(binding.textQuestion.context, R.drawable.button_red)
                            binding.buttonOption2.setIconResource(R.drawable.ic_icon_incorrect)
                        }
                        3 -> {
                            binding.buttonOption3.background = ContextCompat.getDrawable(binding.textQuestion.context, R.drawable.button_red)
                            binding.buttonOption3.setIconResource(R.drawable.ic_icon_incorrect)
                        }
                        4 -> {
                            binding.buttonOption4.background = ContextCompat.getDrawable(binding.textQuestion.context, R.drawable.button_red)
                            binding.buttonOption4.setIconResource(R.drawable.ic_icon_incorrect)
                        }
                    }
                }
            } else {
                binding.buttonOption1.background = ContextCompat.getDrawable(binding.textQuestion.context, R.drawable.button_blue)
                binding.buttonOption1.icon = null
                binding.buttonOption2.background = ContextCompat.getDrawable(binding.textQuestion.context, R.drawable.button_blue)
                binding.buttonOption2.icon = null
                binding.buttonOption3.background = ContextCompat.getDrawable(binding.textQuestion.context, R.drawable.button_blue)
                binding.buttonOption3.icon = null
                binding.buttonOption4.background = ContextCompat.getDrawable(binding.textQuestion.context, R.drawable.button_blue)
                binding.buttonOption4.icon = null
            }
        }

        override fun onClick(v: View?) {
            val buttonOptionNo = v?.tag.toString().toInt()
            listener.onOptionClick(buttonOptionNo)
        }
    }

    interface OnItemClickListener {
        fun onOptionClick(userAnswerNo: Int)
    }

}
