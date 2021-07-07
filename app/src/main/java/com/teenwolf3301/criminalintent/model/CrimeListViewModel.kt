package com.teenwolf3301.criminalintent.model

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.teenwolf3301.criminalintent.database.CrimeDao
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch
import java.io.File
import javax.inject.Inject

@HiltViewModel
class CrimeListViewModel @Inject constructor(private val crimeDao: CrimeDao) : ViewModel() {

    private val listEventChannel = Channel<ListEvent>()

    val listEvent = listEventChannel.receiveAsFlow()
    val crimeListLiveData = crimeDao.getCrimes()

    fun onAddNewCrimeClick() = viewModelScope.launch(Dispatchers.IO) {
        listEventChannel.send(ListEvent.NavigateToAddItemScreen)
    }

    fun onAddNewCrimeClick(crime: Crime) = viewModelScope.launch(Dispatchers.IO) {
        listEventChannel.send(ListEvent.NavigateToEditItemScreen(crime))
    }

    fun deleteAllCrimes(filesDir: File) = viewModelScope.launch(Dispatchers.IO) {
        crimeDao.deleteAllCrimes()
        filesDir.listFiles()?.forEach { it.delete() }
    }

    sealed class ListEvent {
        object NavigateToAddItemScreen : ListEvent()
        data class NavigateToEditItemScreen(val crime: Crime) : ListEvent()
    }

}