package com.teenwolf3301.criminalintent.ui.screens.crime_list

import android.text.format.DateFormat
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.teenwolf3301.criminalintent.databinding.ListItemCrimeBinding
import com.teenwolf3301.criminalintent.model.Crime
import com.teenwolf3301.criminalintent.utility.DATE_FORMAT

class CrimeAdapter(private val listener: OnItemClickListener) :
    ListAdapter<Crime, CrimeAdapter.CrimeHolder>(DiffCallback()) {

    interface OnItemClickListener {
        fun onItemClick(crime: Crime)
    }

    inner class CrimeHolder(private val binding: ListItemCrimeBinding) :
        RecyclerView.ViewHolder(binding.root) {

        init {
            binding.apply {
                root.setOnClickListener {
                    val position = adapterPosition
                    if (position != RecyclerView.NO_POSITION) {
                        val crime = getItem(position)
                        listener.onItemClick(crime)
                    }
                }
            }
        }

        fun bind(crime: Crime) {
            binding.apply {
                crimeTitle.text = crime.title
                crimeDate.text = DateFormat.format(DATE_FORMAT, crime.date)
                crimeSolved.visibility = if (crime.isSolved) View.VISIBLE else View.GONE
            }
        }
    }

    class DiffCallback : DiffUtil.ItemCallback<Crime>() {
        override fun areItemsTheSame(oldItem: Crime, newItem: Crime): Boolean =
            oldItem.id == newItem.id

        override fun areContentsTheSame(oldItem: Crime, newItem: Crime): Boolean =
            oldItem == newItem
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CrimeHolder {
        val binding =
            ListItemCrimeBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return CrimeHolder(binding)
    }

    override fun onBindViewHolder(holder: CrimeHolder, position: Int) {
        val crime = getItem(position)
        holder.bind(crime)
    }
}