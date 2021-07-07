package com.teenwolf3301.criminalintent.di

import android.app.Application
import androidx.room.Room
import com.teenwolf3301.criminalintent.database.CrimeDatabase
import com.teenwolf3301.criminalintent.database.migration_1_2
import com.teenwolf3301.criminalintent.database.migration_2_3
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object AppModule {

    @Provides
    @Singleton
    fun providesDatabase(
        app: Application
    ) = Room.databaseBuilder(app, CrimeDatabase::class.java, "crime-database")
        .addMigrations(migration_1_2)
        .addMigrations(migration_2_3)
        .build()

    @Provides
    fun providesCrimeDao(db: CrimeDatabase) = db.crimeDao()
}