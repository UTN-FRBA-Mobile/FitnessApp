package ar.utn.frba.mobile.fitnessapp.ui.bookings

import androidx.lifecycle.ViewModelProvider
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import ar.utn.frba.mobile.fitnessapp.R
import ar.utn.frba.mobile.fitnessapp.databinding.FragmentBookingsBinding

class BookingsFragment : Fragment() {

    companion object {
        fun newInstance() = BookingsFragment()
    }

    private var _binding : FragmentBookingsBinding? = null
    private val binding get() = _binding!!

    private lateinit var viewModel: BookingsViewModel

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,savedInstanceState: Bundle?): View? {
        _binding = FragmentBookingsBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        viewModel = ViewModelProvider(this).get(BookingsViewModel::class.java)
        // TODO: Use the ViewModel
    }

}