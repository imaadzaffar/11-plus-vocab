package com.zafaris.elevenplusvocab.ui.test

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.button.MaterialButton
import com.zafaris.elevenplusvocab.R
import com.zafaris.elevenplusvocab.data.model.Question

class QuestionsCardStackAdapter(
        private val questions: List<Question> = emptyList(),
        private val listener: OnItemClickListener
) : RecyclerView.Adapter<QuestionsCardStackAdapter.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, itemViewType: Int): ViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        return ViewHolder(inflater.inflate(R.layout.test_card_question, parent, false))
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = questions[position]
        holder.questionNo.text = (position + 1).toString()

        val word = item.word
        val questionTypeString = if (item.questionType == 0) "synonym" else "antonym"

        holder.questionType.text = questionTypeString
        val questionString = "Choose the $questionTypeString for $word"
        holder.questionText.text = questionString
        holder.example.text = item.example

        holder.option1.text = item.option1
        holder.option2.text = item.option2
        holder.option3.text = item.option3
        holder.option4.text = item.option4

        if (item.isAnswered) {
            when (item.answerNo) {
                1 -> {
                    holder.option1.background = ContextCompat.getDrawable(holder.questionNo.context, R.drawable.button_green)
                    holder.option1.setIconResource(R.drawable.ic_icon_correct)
                }
                2 -> {
                    holder.option2.background = ContextCompat.getDrawable(holder.questionNo.context, R.drawable.button_green)
                    holder.option2.setIconResource(R.drawable.ic_icon_correct)
                }
                3 -> {
                    holder.option3.background = ContextCompat.getDrawable(holder.questionNo.context, R.drawable.button_green)
                    holder.option3.setIconResource(R.drawable.ic_icon_correct)
                }
                4 -> {
                    holder.option4.background = ContextCompat.getDrawable(holder.questionNo.context, R.drawable.button_green)
                    holder.option4.setIconResource(R.drawable.ic_icon_correct)
                }
            }
            if (item.userAnswerNo != item.answerNo) {
                when (item.userAnswerNo) {
                    1 -> {
                        holder.option1.background = ContextCompat.getDrawable(holder.questionNo.context, R.drawable.button_red)
                        holder.option1.setIconResource(R.drawable.ic_icon_incorrect)
                    }
                    2 -> {
                        holder.option2.background = ContextCompat.getDrawable(holder.questionNo.context, R.drawable.button_red)
                        holder.option2.setIconResource(R.drawable.ic_icon_incorrect)
                    }
                    3 -> {
                        holder.option3.background = ContextCompat.getDrawable(holder.questionNo.context, R.drawable.button_red)
                        holder.option3.setIconResource(R.drawable.ic_icon_incorrect)
                    }
                    4 -> {
                        holder.option4.background = ContextCompat.getDrawable(holder.questionNo.context, R.drawable.button_red)
                        holder.option4.setIconResource(R.drawable.ic_icon_incorrect)
                    }
                }
            }
        } else {
            holder.option1.background = ContextCompat.getDrawable(holder.questionNo.context, R.drawable.button_blue)
            holder.option1.icon = null
            holder.option2.background = ContextCompat.getDrawable(holder.questionNo.context, R.drawable.button_blue)
            holder.option2.icon = null
            holder.option3.background = ContextCompat.getDrawable(holder.questionNo.context, R.drawable.button_blue)
            holder.option3.icon = null
            holder.option4.background = ContextCompat.getDrawable(holder.questionNo.context, R.drawable.button_blue)
            holder.option4.icon = null
        }
    }

    override fun getItemCount(): Int {
        return questions.size
    }

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView), View.OnClickListener {
        val questionNo: TextView = itemView.findViewById(R.id.card_test_questionNoText)
        val questionType: TextView = itemView.findViewById(R.id.card_test_questionTypeText)
        val questionText: TextView = itemView.findViewById(R.id.card_test_questionText)
        val example: TextView = itemView.findViewById(R.id.card_test_exampleText)
        val option1: MaterialButton = itemView.findViewById(R.id.card_test_option1)
        val option2: MaterialButton = itemView.findViewById(R.id.card_test_option2)
        val option3: MaterialButton = itemView.findViewById(R.id.card_test_option3)
        val option4: MaterialButton = itemView.findViewById(R.id.card_test_option4)

        init {
            option1.setOnClickListener(this)
            option2.setOnClickListener(this)
            option3.setOnClickListener(this)
            option4.setOnClickListener(this)
        }

        override fun onClick(v: View?) {
            val optionNo = v?.tag.toString().toInt()
            listener.onOptionClick(optionNo)
        }
    }

    interface OnItemClickListener {
        fun onOptionClick(userAnswerNo: Int)
    }

}
