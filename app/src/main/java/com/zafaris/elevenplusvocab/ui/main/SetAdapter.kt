package com.zafaris.elevenplusvocab.ui.main

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.zafaris.elevenplusvocab.R
import com.zafaris.elevenplusvocab.ui.main.SetAdapter.SetViewHolder
import java.util.*

class SetAdapter (private val setsList: ArrayList<Set>) : RecyclerView.Adapter<SetViewHolder>() {

    var onItemClick: ((Set, Int) -> Unit)? = null

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SetViewHolder {
        val itemView = LayoutInflater.from(parent.context).inflate(R.layout.item_set, parent, false)
        return SetViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: SetViewHolder, position: Int) {
        val (setNo, isSetCompleted, isSetLocked) = setsList[position]
        val setNoText = StringBuilder("Set ")
        setNoText.append(setNo)
        holder.mSetNoText.text = setNoText
        val setWordsText = StringBuilder("Words ")
        setWordsText.append(position * MainActivity.setSize + 1)
        setWordsText.append(" - ")
        setWordsText.append((position + 1) * MainActivity.setSize)
        setWordsText.append("")
        holder.mSetWordsText.text = setWordsText

        // Set locked
        when {
            isSetLocked -> {
                holder.mSetBackground.background = ContextCompat.getDrawable(holder.mSetBackground.context, R.drawable.set_locked)
                holder.mSetIcon.setImageResource(R.drawable.ic_icon_locked)
            }
            isSetCompleted -> {
                holder.mSetBackground.background = ContextCompat.getDrawable(holder.mSetBackground.context, R.drawable.set_completed)
                holder.mSetIcon.setImageResource(R.drawable.ic_icon_completed)
            }
            else -> {
                holder.mSetBackground.background = ContextCompat.getDrawable(holder.mSetBackground.context, R.drawable.set_play)
                holder.mSetIcon.setImageResource(R.drawable.ic_icon_play)
            }
        }
    }

    override fun getItemCount(): Int {
        return setsList.size
    }

    inner class SetViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val mSetBackground: LinearLayout = itemView.findViewById(R.id.setBackground)
        val mSetIcon: ImageView = itemView.findViewById(R.id.setIcon)
        val mSetNoText: TextView = itemView.findViewById(R.id.setNoText)
        val mSetWordsText: TextView = itemView.findViewById(R.id.setWordsText)

        init {
            itemView.setOnClickListener {
                onItemClick?.invoke(setsList[adapterPosition], adapterPosition)
            }
        }
    }

}