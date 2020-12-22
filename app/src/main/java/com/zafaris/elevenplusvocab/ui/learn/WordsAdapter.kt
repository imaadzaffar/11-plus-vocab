package com.zafaris.elevenplusvocab.ui.learn

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.zafaris.elevenplusvocab.R
import com.zafaris.elevenplusvocab.ui.learn.WordsAdapter.WordsViewHolder
import com.zafaris.elevenplusvocab.data.model.Word

class WordsAdapter(private val words: List<Word>) : RecyclerView.Adapter<WordsViewHolder>() {

    var onItemClick: ((Word, Int) -> Unit)? = null

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): WordsViewHolder {
        val itemView = LayoutInflater.from(parent.context).inflate(R.layout.wordslist_item_word, parent, false)
        return WordsViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: WordsViewHolder, position: Int) {
        val item = words[position]
        holder.idText.text = "${item.id}."
        holder.wordText.text = item.word
    }

    override fun getItemCount(): Int {
        return words.size
    }

    inner class WordsViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        var idText: TextView = itemView.findViewById(R.id.word_idText)
        var wordText: TextView = itemView.findViewById(R.id.word_wordText)

        init {
            itemView.setOnClickListener {
                onItemClick?.invoke(words[adapterPosition], adapterPosition)
            }
        }
    }

}