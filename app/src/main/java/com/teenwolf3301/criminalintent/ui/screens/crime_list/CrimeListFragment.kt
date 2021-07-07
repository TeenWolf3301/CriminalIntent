package com.teenwolf3301.criminalintent.ui.screens.crime_list

import android.app.AlertDialog
import android.os.Bundle
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.teenwolf3301.criminalintent.R
import com.teenwolf3301.criminalintent.databinding.FragmentCrimeListBinding
import com.teenwolf3301.criminalintent.model.Crime
import com.teenwolf3301.criminalintent.model.CrimeListViewModel
import com.teenwolf3301.criminalintent.model.CrimeListViewModel.ListEvent.NavigateToAddItemScreen
import com.teenwolf3301.criminalintent.model.CrimeListViewModel.ListEvent.NavigateToEditItemScreen
import com.teenwolf3301.criminalintent.utility.showToast
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collect

@AndroidEntryPoint
class CrimeListFragment : Fragment(R.layout.fragment_crime_list), CrimeAdapter.OnItemClickListener {

    private lateinit var crimeListRecyclerView: RecyclerView

    private val crimeListViewModel: CrimeListViewModel by viewModels()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val binding = FragmentCrimeListBinding.bind(view)
        val listAdapter = CrimeAdapter(this)

        crimeListRecyclerView = binding.crimeListRecyclerView
        crimeListRecyclerView.apply {
            adapter = listAdapter
            layoutManager = LinearLayoutManager(context)
            setHasFixedSize(true)
        }

        crimeListViewModel.crimeListLiveData.observe(viewLifecycleOwner) { crimes ->
            crimes?.let {
                if (crimes.isEmpty()) {
                    binding.crimeListRecyclerView.visibility = View.GONE
                    binding.emptyListText.visibility = View.VISIBLE
                } else {
                    binding.crimeListRecyclerView.visibility = View.VISIBLE
                    binding.emptyListText.visibility = View.GONE
                    listAdapter.submitList(crimes)
                }
            }
        }

        viewLifecycleOwner.lifecycleScope.launchWhenStarted {
            crimeListViewModel.listEvent.collect { event ->
                when (event) {
                    is NavigateToAddItemScreen -> {
                        val action =
                            CrimeListFragmentDirections.actionCrimeListFragmentToCrimeFragment(
                                null,
                                "New Crime"
                            )
                        findNavController().navigate(action)
                    }
                    is NavigateToEditItemScreen -> {
                        val action =
                            CrimeListFragmentDirections.actionCrimeListFragmentToCrimeFragment(
                                event.crime,
                                event.crime.title
                            )
                        findNavController().navigate(action)
                    }
                }
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
                crimeListViewModel.onAddNewCrimeClick()
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
        val filesDir = requireActivity().applicationContext.filesDir
        builder.apply {
            setPositiveButton("Yes") { _, _ ->
                crimeListViewModel.deleteAllCrimes(filesDir)
                showToast("Successfully removed all crimes")
            }
            setNegativeButton("No") { _, _ -> }
            setTitle("Delete everything?")
            setMessage("Are you sure you want to delete everything?")
            create().show()
        }
    }

    override fun onItemClick(crime: Crime) {
        crimeListViewModel.onAddNewCrimeClick(crime)
    }
}