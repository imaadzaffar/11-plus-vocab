package com.zafaris.elevenplusvocab.ui.learn

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import com.zafaris.elevenplusvocab.R
import com.zafaris.elevenplusvocab.utils.Word

class CardStackAdapter(private var words: List<Word> = emptyList()) : RecyclerView.Adapter<CardStackAdapter.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        return ViewHolder(inflater.inflate(R.layout.card_learn_word, parent, false))
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = words[position]
        holder.id.text = item.id.toString()
        holder.word.text = item.word
        holder.type.text = item.type
        holder.definition.text = item.meanings[0].definition
        holder.example.text = item.meanings[0].example
        holder.synonyms.text = item.meanings[0].synonyms
        holder.antonyms.text = item.meanings[0].antonyms
//        holder.itemView.setOnClickListener { v ->
//            Toast.makeText(v.context, "${item.id}. ${item.word}", Toast.LENGTH_SHORT).show()
//        }
    }

    override fun getItemCount(): Int {
        return words.size
    }

    fun setWords(words: List<Word>) {
        this.words = words
    }

    fun getWords(): List<Word> {
        return words
    }

    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val id: TextView = view.findViewById(R.id.card_idText)
        val word: TextView = view.findViewById(R.id.card_wordText)
        val type: TextView = view.findViewById(R.id.card_typeText)
        val definition: TextView = view.findViewById(R.id.card_definitionText)
        val example: TextView = view.findViewById(R.id.card_exampleText)
        var synonyms: TextView = view.findViewById(R.id.card_synonymsText)
        var antonyms: TextView = view.findViewById(R.id.card_antonymsText)
    }

}
