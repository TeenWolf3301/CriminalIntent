package com.teenwolf3301.criminalintent.model

import android.app.Application
import androidx.lifecycle.*
import com.teenwolf3301.criminalintent.database.CrimeRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.util.*

class CrimeDetailViewModel(application: Application) : AndroidViewModel(application) {

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
}