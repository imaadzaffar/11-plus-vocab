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
import com.zafaris.elevenplusvocab.databinding.HomeItemSetBinding
import com.zafaris.elevenplusvocab.ui.home.SetAdapter.SetViewHolder
import com.zafaris.elevenplusvocab.util.SET_SIZE

class SetAdapter (
        private val sets: List<Set> = emptyList(),
        private val listener: OnItemClickListener
) : RecyclerView.Adapter<SetViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SetViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val binding = HomeItemSetBinding.inflate(inflater, parent, false)
        return SetViewHolder(binding)
    }

    override fun onBindViewHolder(holder: SetViewHolder, position: Int) {
        val set = sets[position]
        holder.bind(set)
    }

    override fun getItemCount(): Int {
        return sets.size
    }

    inner class SetViewHolder(private val binding: HomeItemSetBinding) : RecyclerView.ViewHolder(binding.root), View.OnClickListener {
        fun bind(set: Set) {
            val setNoText = "Set ${set.setNo}"
            binding.textSetNo.text = setNoText
            val setWordsText = "Words ${adapterPosition * SET_SIZE + 1} - ${(adapterPosition + 1) * SET_SIZE}"
            binding.textWords.text = setWordsText

            when {
                set.isSetLocked -> {
                    binding.layoutSet.background = ContextCompat.getDrawable(binding.layoutSet.context, R.drawable.home_set_locked)
                    binding.iconSet.setImageResource(R.drawable.ic_icon_locked)
                }
                set.isSetCompleted -> {
                    binding.layoutSet.background = ContextCompat.getDrawable(binding.layoutSet.context, R.drawable.home_set_completed)
                    binding.iconSet.setImageResource(R.drawable.ic_icon_completed)
                }
                else -> {
                    binding.layoutSet.background = ContextCompat.getDrawable(binding.layoutSet.context, R.drawable.home_set_play)
                    binding.iconSet.setImageResource(R.drawable.ic_icon_play)
                }
            }
        }

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