package com.teenwolf3301.criminalintent.model

import androidx.lifecycle.ViewModel
import com.teenwolf3301.criminalintent.database.CrimeRepository

class CrimeListViewModel : ViewModel() {

    private val crimeRepository = CrimeRepository.get()
    val crimeListLiveData = crimeRepository.getCrimes()
}