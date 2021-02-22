package com.teenwolf3301.criminalintent

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.teenwolf3301.criminalintent.databinding.ActivityMainBinding
import com.teenwolf3301.criminalintent.ui.screens.crime_list.CrimeListFragment
import com.teenwolf3301.criminalintent.utility.APP_ACTIVITY

class MainActivity : AppCompatActivity() {

    private lateinit var mBinding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mBinding = ActivityMainBinding.inflate(layoutInflater)
        val view = mBinding.root
        setContentView(view)
        APP_ACTIVITY = this

        val currentFragment = supportFragmentManager.findFragmentById(R.id.frame_container)

        if (currentFragment == null) {
            val fragment = CrimeListFragment()
            supportFragmentManager
                .beginTransaction()
                .add(R.id.frame_container, fragment)
                .commit()
        }
    }
}