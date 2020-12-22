package com.zafaris.elevenplusvocab.ui.test

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import com.zafaris.elevenplusvocab.R

class QuestionsCardStackAdapter(private var questions: List<Question> = emptyList()) : RecyclerView.Adapter<QuestionsCardStackAdapter.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, itemViewType: Int): ViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        return ViewHolder(inflater.inflate(R.layout.card_test_question, parent, false))
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

        holder.option1.setOnClickListener { v -> optionClick(v, 1) }
        holder.option1.text = item.option1
        holder.option2.setOnClickListener { v -> optionClick(v, 2) }
        holder.option2.text = item.option2
        holder.option3.setOnClickListener { v -> optionClick(v, 3) }
        holder.option3.text = item.option3
        holder.option4.setOnClickListener { v -> optionClick(v, 4) }
        holder.option4.text = item.option4
    }

    private fun optionClick(v: View, optionNo: Int) {
        Toast.makeText(v.context, "Option $optionNo clicked", Toast.LENGTH_SHORT).show()
    }

    override fun getItemCount(): Int {
        return questions.size
    }

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val questionNo: TextView = itemView.findViewById(R.id.card_test_questionNoText)
        val questionType: TextView = itemView.findViewById(R.id.card_test_questionTypeText)
        val questionText: TextView = itemView.findViewById(R.id.card_test_questionText)
        val example: TextView = itemView.findViewById(R.id.card_test_exampleText)
        val option1: Button = itemView.findViewById(R.id.card_test_option1)
        val option2: Button = itemView.findViewById(R.id.card_test_option2)
        val option3: Button = itemView.findViewById(R.id.card_test_option3)
        val option4: Button = itemView.findViewById(R.id.card_test_option4)
    }

}
