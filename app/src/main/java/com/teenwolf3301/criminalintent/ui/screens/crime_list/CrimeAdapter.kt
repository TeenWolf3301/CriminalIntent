package com.teenwolf3301.criminalintent.ui.screens.crime_list

import android.text.format.DateFormat
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.teenwolf3301.criminalintent.Crime
import com.teenwolf3301.criminalintent.databinding.ListItemCrimeBinding

class CrimeAdapter(
    private var crimes: List<Crime>,
    private val callbacks: CrimeListFragment.Callbacks?
) :
    RecyclerView.Adapter<CrimeAdapter.CrimeHolder>() {

    inner class CrimeHolder(
        private val binding: ListItemCrimeBinding,
        private val callbacks: CrimeListFragment.Callbacks?
    ) :
        RecyclerView.ViewHolder(binding.root), View.OnClickListener {
        private lateinit var crime: Crime

        private val title: TextView = binding.crimeTitle
        private val date: TextView = binding.crimeDate
        private val image: ImageView = binding.crimeSolved
//        private val police: Button = binding.crimePoliceButton

        init {
            itemView.setOnClickListener(this)
        }

        fun bind(crime: Crime) {
            this.crime = crime
            title.text = this.crime.title
            date.text = DateFormat.format("EEEE, MMM dd, yyyy", this.crime.date)
            image.visibility = if (this.crime.isSolved) View.VISIBLE else View.GONE
/*          police.visibility =
                if (this.crime.requiresPolice) View.VISIBLE else View.GONE*/
        }

        override fun onClick(v: View?) {
            callbacks?.onCrimeSelected(crime.id)
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CrimeHolder {
        val binding =
            ListItemCrimeBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return CrimeHolder(binding, callbacks)
    }

    override fun onBindViewHolder(holder: CrimeHolder, position: Int) {
        val crime = crimes[position]
        holder.bind(crime)
    }

    override fun getItemCount() = crimes.size
}