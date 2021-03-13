package com.teenwolf3301.criminalintent.database

import androidx.lifecycle.LiveData
import androidx.room.*
import com.teenwolf3301.criminalintent.model.Crime
import java.util.*

@Dao
interface CrimeDao {

    @Query("SELECT * FROM crime")
    fun getCrimes(): LiveData<List<Crime>>

    @Query("SELECT * FROM crime WHERE id=(:id)")
    fun getCrime(id: UUID): LiveData<Crime?>

    @Update
    suspend fun updateCrime(crime: Crime)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun addCrime(crime: Crime): Long

    @Delete
    suspend fun deleteCrime(crime: Crime)

    @Query("DELETE FROM crime")
    suspend fun deleteAllCrimes()
}