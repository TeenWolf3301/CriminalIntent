package com.teenwolf3301.criminalintent.model

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.teenwolf3301.criminalintent.database.CrimeDao
import com.teenwolf3301.criminalintent.utility.showToast
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.util.*
import javax.inject.Inject

@HiltViewModel
class CrimeViewModel @Inject constructor(
    private val crimeDao: CrimeDao,
    private val state: SavedStateHandle
) : ViewModel() {

    val crime = state.get<Crime>("crime")

    var crimeTitle = state.get<String>("crimeTitle") ?: crime?.title ?: ""
        set(value) {
            field = value
            state.set("crimeTitle", value)
        }

    var crimeDate = state.get<Date>("crimeDate") ?: crime?.date ?: Date()
        set(value) {
            field = value
            state.set("crimeDate", value)
        }

    var crimeSolved = state.get<Boolean>("crimeSolved") ?: crime?.isSolved ?: false
        set(value) {
            field = value
            state.set("crimeSolved", value)
        }

    var crimeSuspect = state.get<String>("crimeSuspect") ?: crime?.suspect ?: ""
        set(value) {
            field = value
            state.set("crimeSuspect", value)
        }

    var crimePhotoFileName = state.get<String>("crimePhotoFileName") ?: crime?.photoFileName
    ?: "IMG_${UUID.randomUUID()}.jpg"

    fun onSaveClick() {
        if (crime != null) {
            val updatedCrime = crime.copy(
                title = crimeTitle,
                date = crimeDate,
                isSolved = crimeSolved,
                suspect = crimeSuspect,
                photoFileName = crimePhotoFileName
            )
            saveCrime(updatedCrime)
            showToast("Crime $crimeTitle updated!")
        } else {
            val newCrime = Crime(
                title = crimeTitle,
                date = crimeDate,
                isSolved = crimeSolved,
                suspect = crimeSuspect,
                photoFileName = crimePhotoFileName
            )
            addNewCrime(newCrime)
            showToast("Crime $crimeTitle created!")
        }
    }

    fun onDeleteClick() {
        if (crime != null) deleteCrime(crime)
    }

    private fun saveCrime(crime: Crime) = viewModelScope.launch(Dispatchers.IO) {
        crimeDao.updateCrime(crime)
    }

    private fun addNewCrime(crime: Crime) = viewModelScope.launch(Dispatchers.IO) {
        crimeDao.addCrime(crime)
    }

    private fun deleteCrime(crime: Crime) = viewModelScope.launch(Dispatchers.IO) {
        crimeDao.deleteCrime(crime)
    }
}