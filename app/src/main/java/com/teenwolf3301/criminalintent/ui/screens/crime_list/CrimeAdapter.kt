package com.teenwolf3301.criminalintent.ui.screens.crime_list

import android.text.format.DateFormat
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.teenwolf3301.criminalintent.databinding.ListItemCrimeBinding
import com.teenwolf3301.criminalintent.model.Crime
import com.teenwolf3301.criminalintent.utility.onCrimeSelected

class CrimeAdapter(private var crimes: List<Crime>) :
    RecyclerView.Adapter<CrimeAdapter.CrimeHolder>() {

    inner class CrimeHolder(binding: ListItemCrimeBinding) :
        RecyclerView.ViewHolder(binding.root) {
        private lateinit var crime: Crime

        private val title: TextView = binding.crimeTitle
        private val date: TextView = binding.crimeDate
        private val image: ImageView = binding.crimeSolved
//        private val police: Button = binding.crimePoliceButton

        fun bind(crime: Crime) {
            this.crime = crime
            title.text = this.crime.title
            date.text = DateFormat.format("EEEE, MMM dd, yyyy", this.crime.date)
            image.visibility = if (this.crime.isSolved) View.VISIBLE else View.GONE
/*          police.visibility =
                if (this.crime.requiresPolice) View.VISIBLE else View.GONE*/
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CrimeHolder {
        val binding =
            ListItemCrimeBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return CrimeHolder(binding)
    }

    override fun onBindViewHolder(holder: CrimeHolder, position: Int) {
        val crime = crimes[position]
        holder.bind(crime)
        holder.itemView.setOnClickListener {
            onCrimeSelected(crime.id)
        }
    }

    override fun getItemCount() = crimes.size
}