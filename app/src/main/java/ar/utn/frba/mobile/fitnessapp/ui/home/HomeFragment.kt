package ar.utn.frba.mobile.fitnessapp.ui.home

import android.os.Bundle
import android.view.KeyEvent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.os.bundleOf
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.NavController
import androidx.navigation.fragment.findNavController
import ar.utn.frba.mobile.fitnessapp.MyPreferences
import ar.utn.frba.mobile.fitnessapp.R
import ar.utn.frba.mobile.fitnessapp.databinding.FragmentHomeBinding
import ar.utn.frba.mobile.fitnessapp.model.Gym

class HomeFragment : Fragment() {
    private val viewModel: HomeViewModel by viewModels()

    // This property is only valid between onCreateView and
    // onDestroyView.
    private var _binding: FragmentHomeBinding? = null
    private val binding get() = _binding!!

    private lateinit var navController: NavController

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        navController = findNavController()
    }

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
                val action = HomeFragmentDirections.actionNavigationHomeToClassesFragment(gym)
                navController.navigate(action)
            }
        }
    }

    override fun onStart() {
        super.onStart()
        val showBG = MyPreferences.isShowBGsPreferredView(requireContext())
        if(showBG){
            activity?.findViewById<ConstraintLayout>(R.id.homeScreen)?.setBackgroundResource(R.drawable.bg_yogax)
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}