package com.teenwolf3301.criminalintent.ui.screens.crime_list

import android.text.format.DateFormat
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.teenwolf3301.criminalintent.databinding.ListItemCrimeBinding
import com.teenwolf3301.criminalintent.model.Crime
import com.teenwolf3301.criminalintent.utility.DATE_FORMAT
import com.teenwolf3301.criminalintent.utility.onCrimeSelected

class CrimeAdapter() :
    ListAdapter<Crime, CrimeAdapter.CrimeHolder>(CrimeItemCallback()) {

    inner class CrimeHolder(binding: ListItemCrimeBinding) :
        RecyclerView.ViewHolder(binding.root) {
        private lateinit var crime: Crime

        private val title: TextView = binding.crimeTitle
        private val date: TextView = binding.crimeDate
        private val image: ImageView = binding.crimeSolved

        fun bind(crime: Crime) {
            this.crime = crime
            title.text = this.crime.title
            date.text = DateFormat.format(DATE_FORMAT, this.crime.date)
            image.visibility = if (this.crime.isSolved) View.VISIBLE else View.GONE
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CrimeHolder {
        val binding =
            ListItemCrimeBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return CrimeHolder(binding)
    }

    override fun onBindViewHolder(holder: CrimeHolder, position: Int) {
        val crime = getItem(position)
        holder.bind(crime)
        holder.itemView.setOnClickListener {
            onCrimeSelected(crime.id)
        }
    }
}