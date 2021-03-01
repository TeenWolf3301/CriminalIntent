package com.teenwolf3301.criminalintent.ui.screens.crime_list

import androidx.recyclerview.widget.DiffUtil
import com.teenwolf3301.criminalintent.model.Crime

class CrimeItemCallback : DiffUtil.ItemCallback<Crime>() {

    override fun areItemsTheSame(oldItem: Crime, newItem: Crime): Boolean {
        return oldItem.id == newItem.id
    }


    override fun areContentsTheSame(oldItem: Crime, newItem: Crime): Boolean {
        return oldItem == newItem
    }
}