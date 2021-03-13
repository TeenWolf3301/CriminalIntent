package com.teenwolf3301.criminalintent.model

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.teenwolf3301.criminalintent.database.CrimeRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class CrimeListViewModel : ViewModel() {

    private val crimeRepository = CrimeRepository.get()
    val crimeListLiveData = crimeRepository.getCrimes()

    fun addCrime(crime: Crime) {
        viewModelScope.launch(Dispatchers.IO) {
            crimeRepository.addCrime(crime)
        }
    }

    fun deleteAllCrimes() {
        viewModelScope.launch(Dispatchers.IO) {
            crimeRepository.deleteAllCrimes()
        }
    }
}