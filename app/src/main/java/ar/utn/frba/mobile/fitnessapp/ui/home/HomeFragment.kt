package ar.utn.frba.mobile.fitnessapp.ui.home

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.ViewModelProvider
import ar.utn.frba.mobile.fitnessapp.MyPreferences
import ar.utn.frba.mobile.fitnessapp.R
import ar.utn.frba.mobile.fitnessapp.databinding.FragmentHomeBinding
import ar.utn.frba.mobile.fitnessapp.model.Gym
import com.google.android.material.textfield.TextInputEditText

class HomeFragment : Fragment() {
    private var showBG: Boolean = true

    private val viewModel: HomeViewModel by viewModels()

    // This property is only valid between onCreateView and
    // onDestroyView.
    private var _binding: FragmentHomeBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentHomeBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.searchButton.setOnClickListener {
            val searchbar: EditText = binding.searchbar
            val query: String = searchbar.text.toString()
            viewModel.search(query)
        }

        val fragmentContext = context
        viewModel.searchResults.observe(viewLifecycleOwner) {
            val adapter = GymSearchResultAdapter(fragmentContext!!, it)
            binding.resultList.adapter = adapter
        }
    }

    //override fun onStart() {
    //    super.onStart()
    //    showBG = MyPreferences.isShowBGsPreferredView(context!!)
    //    if(showBG){
    //        activity?.findViewById<ConstraintLayout>(R.id.homeScreen)?.setBackgroundResource(R.drawable.bg_yogax);
    //    }
    //}

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}