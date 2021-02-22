package com.teenwolf3301.criminalintent.ui.screens.crime_list

import android.os.Bundle
import android.view.*
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.teenwolf3301.criminalintent.R
import com.teenwolf3301.criminalintent.databinding.FragmentCrimeListBinding
import com.teenwolf3301.criminalintent.model.Crime
import com.teenwolf3301.criminalintent.model.CrimeListViewModel
import com.teenwolf3301.criminalintent.onCrimeSelected

class CrimeListFragment : Fragment() {

    private lateinit var crimeListRecyclerView: RecyclerView

    private var _binding: FragmentCrimeListBinding? = null
    private var adapter: CrimeAdapter? = CrimeAdapter(emptyList())

    private val binding get() = _binding!!
    private val crimeListViewModel: CrimeListViewModel by lazy {
        ViewModelProvider(this).get(CrimeListViewModel::class.java)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setHasOptionsMenu(true)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentCrimeListBinding.inflate(layoutInflater, container, false)
        val view = binding.root

        crimeListRecyclerView = binding.crimeListRecyclerView
        crimeListRecyclerView.layoutManager = LinearLayoutManager(context)
        crimeListRecyclerView.adapter = adapter

        return view
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        super.onCreateOptionsMenu(menu, inflater)
        inflater.inflate(R.menu.fragment_list_menu, menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.new_crime -> {
                val crime = Crime()
                crimeListViewModel.addCrime(crime)
                onCrimeSelected(crime.id)
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    override fun onStart() {
        super.onStart()
        crimeListViewModel.crimeListLiveData.observe(
            viewLifecycleOwner,
            { crimes ->
                crimes?.let {
                    updateUI(crimes)
                }
            }
        )
    }

    private fun updateUI(crimes: List<Crime>) {
        adapter = CrimeAdapter(crimes)
        crimeListRecyclerView.adapter = adapter
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    companion object {
        fun newInstance(): CrimeListFragment {
            return CrimeListFragment()
        }
    }
}