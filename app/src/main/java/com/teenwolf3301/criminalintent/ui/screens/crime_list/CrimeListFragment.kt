package com.teenwolf3301.criminalintent.ui.screens.crime_list

import android.app.AlertDialog
import android.os.Bundle
import android.view.*
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.teenwolf3301.criminalintent.R
import com.teenwolf3301.criminalintent.databinding.FragmentCrimeListBinding
import com.teenwolf3301.criminalintent.model.Crime
import com.teenwolf3301.criminalintent.model.CrimeListViewModel
import com.teenwolf3301.criminalintent.utility.APP_ACTIVITY
import com.teenwolf3301.criminalintent.utility.onCrimeSelected
import com.teenwolf3301.criminalintent.utility.showToast

class CrimeListFragment : Fragment() {

    private lateinit var crimeListRecyclerView: RecyclerView

    private var _binding: FragmentCrimeListBinding? = null
    private var adapter: CrimeAdapter? = CrimeAdapter()

    private val binding get() = _binding!!
    private val crimeListViewModel: CrimeListViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentCrimeListBinding.inflate(layoutInflater, container, false)
        val view = binding.root

        crimeListRecyclerView = binding.crimeListRecyclerView
        crimeListRecyclerView.apply {
            layoutManager = LinearLayoutManager(context)
            crimeListRecyclerView.adapter = adapter
        }

        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        APP_ACTIVITY.title = "Crime List"

        crimeListViewModel.crimeListLiveData.observe(viewLifecycleOwner) { crimes ->
            crimes?.let {
                updateUI(crimes)
            }
        }

        setHasOptionsMenu(true)
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        super.onCreateOptionsMenu(menu, inflater)
        inflater.inflate(R.menu.fragment_list_menu, menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.new_crime -> {
                createNewCrime()
                true
            }
            R.id.delete_all_crimes -> {
                deleteAllCrimes()
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    private fun deleteAllCrimes() {
        val builder = AlertDialog.Builder(requireContext())
        builder.setPositiveButton("Yes") { _, _ ->
            crimeListViewModel.deleteAllCrimes()
            showToast("Successfully removed all crimes")
        }
        builder.setNegativeButton("No") { _, _ -> }
        builder.setTitle("Delete everything?")
        builder.setMessage("Are you sure you want to delete everything?")
        builder.create().show()
    }

    private fun createNewCrime() {
        val crime = Crime()
        crimeListViewModel.addCrime(crime)
        onCrimeSelected(crime.id)
    }

    private fun updateUI(crimes: List<Crime>) {
        if (crimes.isEmpty()) {
            binding.crimeListRecyclerView.visibility = View.GONE
            binding.emptyListText.visibility = View.VISIBLE
        } else {
            binding.crimeListRecyclerView.visibility = View.VISIBLE
            binding.emptyListText.visibility = View.GONE
            adapter!!.submitList(crimes)
            crimeListRecyclerView.adapter = adapter
        }

    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}