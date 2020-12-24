package com.zafaris.elevenplusvocab.ui.home

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.zafaris.elevenplusvocab.R
import com.zafaris.elevenplusvocab.data.model.Set
import com.zafaris.elevenplusvocab.ui.home.SetAdapter.SetViewHolder
import com.zafaris.elevenplusvocab.util.SET_SIZE

class SetAdapter (
        private val sets: List<Set> = emptyList(),
        private val listener: OnItemClickListener
) : RecyclerView.Adapter<SetViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SetViewHolder {
        val itemView = LayoutInflater.from(parent.context).inflate(R.layout.home_item_set, parent, false)
        return SetViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: SetViewHolder, position: Int) {
        val (setNo, isSetCompleted, isSetLocked) = sets[position]
        val setNoText = "Set $setNo"
        holder.setNo.text = setNoText
        val setWordsText = "Words ${position * SET_SIZE + 1} - ${(position + 1) * SET_SIZE}"
        holder.setWords.text = setWordsText

        when {
            isSetLocked -> {
                holder.background.background = ContextCompat.getDrawable(holder.background.context, R.drawable.home_set_locked)
                holder.icon.setImageResource(R.drawable.ic_icon_locked)
            }
            isSetCompleted -> {
                holder.background.background = ContextCompat.getDrawable(holder.background.context, R.drawable.home_set_completed)
                holder.icon.setImageResource(R.drawable.ic_icon_completed)
            }
            else -> {
                holder.background.background = ContextCompat.getDrawable(holder.background.context, R.drawable.home_set_play)
                holder.icon.setImageResource(R.drawable.ic_icon_play)
            }
        }
    }

    override fun getItemCount(): Int {
        return sets.size
    }

    inner class SetViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView), View.OnClickListener {
        val background: LinearLayout = itemView.findViewById(R.id.setBackground)
        val icon: ImageView = itemView.findViewById(R.id.setIcon)
        val setNo: TextView = itemView.findViewById(R.id.setNoText)
        val setWords: TextView = itemView.findViewById(R.id.setWordsText)

        init {
            itemView.setOnClickListener(this)
        }

        override fun onClick(v: View?) {
            listener.onItemSetClick(sets[adapterPosition])
        }
    }

    interface OnItemClickListener {
        fun onItemSetClick(set: Set)
    }

}