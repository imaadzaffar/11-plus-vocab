package com.zafaris.elevenplusvocab.ui.wordslist

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.zafaris.elevenplusvocab.R
import com.zafaris.elevenplusvocab.data.model.Set
import com.zafaris.elevenplusvocab.data.model.Word

class WordsListAdapter(
        private val itemsList: List<Any> = emptyList(),
        private val listener: OnItemClickListener
) : RecyclerView.Adapter<WordsListAdapter.BaseViewHolder<*>>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BaseViewHolder<*> {
        return when (viewType) {
            TYPE_WORD -> {
                val itemView = LayoutInflater.from(parent.context).inflate(R.layout.wordslist_item_word, parent, false)
                WordViewHolder(itemView)
            }
            TYPE_SET -> {
                val itemView = LayoutInflater.from(parent.context).inflate(R.layout.wordslist_item_set, parent, false)
                SetViewHolder(itemView)
            }
            else -> throw IllegalArgumentException("Invalid view type")
        }
    }

    override fun onBindViewHolder(holder: BaseViewHolder<*>, position: Int) {
        val item = itemsList[position]
        when (holder) {
            is WordViewHolder -> {
                holder.bind(item as Word)
            }
            is SetViewHolder -> {
                holder.bind(item as Set)
            }
            else -> throw IllegalArgumentException()
        }
    }

    override fun getItemViewType(position: Int): Int {
        val item = itemsList[position]
        return when (item) {
            is Word -> TYPE_WORD
            is Set -> TYPE_SET
            else -> throw IllegalArgumentException("Invalid type of data at position $position")
        }
    }

    override fun getItemCount(): Int {
        return itemsList.size
    }

    abstract class BaseViewHolder<T>(itemView: View): RecyclerView.ViewHolder(itemView) {
        abstract fun bind(item: T)
    }

    inner class WordViewHolder(itemView: View) : BaseViewHolder<Word>(itemView), View.OnClickListener {

        override fun bind(item: Word) {
            val idText: TextView = itemView.findViewById(R.id.text_id)
            val wordText: TextView = itemView.findViewById(R.id.text_word)
            idText.text = "${item.id}."
            wordText.text = item.word
        }

        init {
            itemView.setOnClickListener(this)
        }

        override fun onClick(v: View?) {
            listener.onItemWordClick(itemsList[adapterPosition] as Word, adapterPosition)
        }
    }
    
    inner class SetViewHolder(itemView: View) : BaseViewHolder<Set>(itemView), View.OnClickListener {

        override fun bind(item: Set) {
            val set: TextView = itemView.findViewById(R.id.title_set)
            set.text = "Set ${item.setNo}"

            val layout: ConstraintLayout = itemView.findViewById(R.id.layout_set)
            val icon: ImageView = itemView.findViewById(R.id.icon_set)

            when {
                item.isSetLocked -> {
                    layout.background = ContextCompat.getDrawable(layout.context, R.drawable.wordslist_set_locked)
                    icon.setImageResource(R.drawable.ic_icon_locked)
                }
                item.isSetCompleted -> {
                    layout.background = ContextCompat.getDrawable(layout.context, R.drawable.wordslist_set_completed)
                    icon.setImageResource(R.drawable.ic_icon_completed)
                }
                else -> {
                    layout.background = ContextCompat.getDrawable(layout.context, R.drawable.wordslist_set_play)
                    icon.setImageResource(R.drawable.ic_icon_play)
                }
            }
        }

        init {
            itemView.setOnClickListener(this)
        }
        
        override fun onClick(v: View?) {
            listener.onItemSetClick(itemsList[adapterPosition] as Set, adapterPosition)
        }

    }

    interface OnItemClickListener {
        fun onItemWordClick(word: Word, position: Int)
        fun onItemSetClick(set: Set, position: Int)
    }

    companion object {
        private const val TYPE_SET = 0
        private const val TYPE_WORD = 1
    }
}