package com.teenwolf3301.criminalintent.model

import androidx.lifecycle.*
import com.teenwolf3301.criminalintent.database.CrimeRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.io.File
import java.util.*

class CrimeViewModel() : ViewModel() {

    private val crimeRepository = CrimeRepository.get()
    private val crimeIdLiveData = MutableLiveData<UUID>()

    var crimeLiveData: LiveData<Crime?> =
        Transformations.switchMap(crimeIdLiveData) {
            crimeRepository.getCrime(it)
        }

    fun loadCrime(crimeId: UUID) {
        crimeIdLiveData.value = crimeId
    }

    fun saveCrime(crime: Crime) {
        viewModelScope.launch(Dispatchers.IO) {
            crimeRepository.updateCrime(crime)
        }
    }

    fun getPhotoFile(crime: Crime): File {
        return crimeRepository.getPhotoFile(crime)
    }

    fun deleteCrime(crime: Crime) {
        viewModelScope.launch(Dispatchers.IO) {
            crimeRepository.deleteCrime(crime)
        }
    }
}