package ar.utn.frba.mobile.fitnessapp.ui.home

import android.content.Context
import android.os.Bundle
import android.view.KeyEvent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
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

        val searchbar = binding.searchbar
        val searchButton = binding.searchButton

        // Enter Key in searchbar raises the searchButton's onClick event.
        searchbar.setOnKeyListener(View.OnKeyListener { v, keyCode, event ->
            if(v.isFocused && keyCode == KeyEvent.KEYCODE_ENTER && event.action == KeyEvent.ACTION_UP) {
                searchButton.performClick()
                return@OnKeyListener true
            }

            false
        })

        searchButton.setOnClickListener {
            val query: String = searchbar.text.toString()
            viewModel.search(query)
        }

        viewModel.searchResults.observe(viewLifecycleOwner) { arrayList ->
            val adapter = GymSearchResultAdapter(requireContext(), arrayList)
            val resultList = binding.resultList
            resultList.adapter = adapter

            resultList.setOnItemClickListener { parent, _, position, _ ->
                val gym: Gym = parent.getItemAtPosition(position) as Gym
                Toast.makeText(context, "Cliquié el gimnasio ${gym.name}!", Toast.LENGTH_LONG).show()
            }
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