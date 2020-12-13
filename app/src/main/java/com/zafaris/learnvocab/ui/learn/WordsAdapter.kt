package com.zafaris.learnvocab.ui.learn

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.zafaris.learnvocab.R
import com.zafaris.learnvocab.ui.learn.WordsAdapter.WordsViewHolder
import com.zafaris.learnvocab.utils.Word

class WordsAdapter(private val wordsList: List<Word>) : RecyclerView.Adapter<WordsViewHolder>() {

    var onItemClick: ((Word, Int) -> Unit)? = null

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): WordsViewHolder {
        val itemView = LayoutInflater.from(parent.context).inflate(R.layout.item_word, parent, false)
        return WordsViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: WordsViewHolder, position: Int) {
        val currentWord = wordsList[position]
        holder.idText.text = "${currentWord.id}."
        holder.wordText.text = currentWord.word
        //holder.mWordText.setText(new StringBuilder(String.valueOf(id)).append(". ").append(word));
    }

    override fun getItemCount(): Int {
        return wordsList.size
    }

    inner class WordsViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        var idText: TextView = itemView.findViewById(R.id.word_idText)
        var wordText: TextView = itemView.findViewById(R.id.word_wordText)

        init {
            itemView.setOnClickListener {
                onItemClick?.invoke(wordsList[adapterPosition], adapterPosition)
            }
        }
    }

}