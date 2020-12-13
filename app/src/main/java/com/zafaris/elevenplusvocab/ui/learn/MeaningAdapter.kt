package com.zafaris.elevenplusvocab.ui.learn

import android.app.Activity
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.zafaris.elevenplusvocab.R
import com.zafaris.elevenplusvocab.ui.learn.MeaningAdapter.MeaningViewHolder

class MeaningAdapter (val activity: Activity, private var meaningsList: List<Meaning>) : RecyclerView.Adapter<MeaningViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MeaningViewHolder {
        val itemView = LayoutInflater.from(parent.context).inflate(R.layout.item_meaning, parent, false)
        return MeaningViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: MeaningViewHolder, position: Int) {
        val (definition, example, synonyms, antonyms) = meaningsList[position]
        holder.definitionText?.text = definition
        holder.exampleText?.text = example
        holder.synonymsTitle?.visibility = View.VISIBLE
        holder.synonymsText?.visibility = View.VISIBLE
        holder.antonymsTitle?.visibility = View.VISIBLE
        holder.antonymsText?.visibility = View.VISIBLE
        Log.i("Adapter - Synonyms", synonyms)
        if (synonyms == "N/A") { //TODO: make text equal to None when N/A
            holder.synonymsTitle?.visibility = View.GONE
            holder.synonymsText?.visibility = View.GONE
            /*holder.mSynonymsTitle.setText("Synonyms");
            holder.mSynonymsText.setText("(None)");*/
        } else {
            val synonymsList = synonyms.split(", ").toTypedArray()
            holder.synonymsTitle?.text = "Synonyms (${synonymsList.size}):" //TODO: append no. of synonyms and antonyms
            holder.synonymsText?.text = synonyms
        }
        Log.i("Adapter - Antonyms", antonyms)
        if (antonyms == "N/A") {
            holder.antonymsTitle?.visibility = View.GONE
            holder.antonymsText?.visibility = View.GONE
            /*holder.mAntonymsTitle.setText("Antonyms");
            holder.mAntonymsText.setText("(None)");*/
        } else {
            val antonymsList = antonyms.split(", ").toTypedArray()
            holder.antonymsTitle?.text = "Antonyms (${antonymsList.size}):"
            holder.antonymsText?.text = antonyms
        }
    }

    fun updateList(meaningsList: List<Meaning>) {
        this.meaningsList = meaningsList
        notifyDataSetChanged()
    }

    override fun getItemCount(): Int {
        return meaningsList.size
    }

    inner class MeaningViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        var definitionText: TextView? = itemView.findViewById(R.id.meaning_definitionText)
        var exampleText: TextView? = itemView.findViewById(R.id.meaning_exampleText)
        var synonymsTitle: TextView? = itemView.findViewById(R.id.meaning_synonymsTitle)
        var synonymsText: TextView? = itemView.findViewById(R.id.meaning_synonymsText)
        var antonymsTitle: TextView? = itemView.findViewById(R.id.meaning_antonymsTitle)
        var antonymsText: TextView? = itemView.findViewById(R.id.meaning_antonymsText)
    }

}