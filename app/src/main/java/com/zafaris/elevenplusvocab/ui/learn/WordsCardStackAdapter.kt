package com.zafaris.elevenplusvocab.ui.learn

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.TextView
import android.widget.Toast
import androidx.cardview.widget.CardView
import androidx.recyclerview.widget.RecyclerView
import com.zafaris.elevenplusvocab.R
import com.zafaris.elevenplusvocab.data.model.Word
import com.zafaris.elevenplusvocab.databinding.LearnCardWordBinding

class WordsCardStackAdapter(
        private val words: List<Word> = emptyList()
) : RecyclerView.Adapter<WordsCardStackAdapter.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val binding = LearnCardWordBinding.inflate(inflater, parent, false)
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val word = words[position]
        holder.bind(word)
    }

    override fun getItemCount(): Int {
        return words.size
    }

    class ViewHolder(private val binding: LearnCardWordBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(word: Word) {
            binding.textId.text = word.id.toString()
            binding.textWord.text = word.word
            binding.textType.text = word.type

            binding.buttonAudio.setOnClickListener { v ->
                //TODO: Add audio sound for word
                Toast.makeText(v.context, "Play audio for word", Toast.LENGTH_SHORT).show()
            }

            binding.textDefinition.text = word.meanings[0].definition
            binding.textExample.text = word.meanings[0].example

            val synonyms = word.meanings[0].synonyms
            if (synonyms == "N/A") {
                binding.cardSynonyms.visibility = View.INVISIBLE
            } else {
                val size = synonyms.split(", ").size
                binding.cardSynonyms.visibility = View.VISIBLE
                binding.titleSynonyms.text = "Synonyms ($size):"
                binding.textSyononyms.text = word.meanings[0].synonyms
            }
            val antonyms = word.meanings[0].antonyms
            if (antonyms == "N/A") {
                binding.cardAntonyms.visibility = View.INVISIBLE
            } else {
                val size = antonyms.split(", ").size
                binding.cardAntonyms.visibility = View.VISIBLE
                binding.titleAntonyms.text = "Antonyms ($size):"
                binding.textAntonyms.text = word.meanings[0].antonyms
            }
        }
    }

}
