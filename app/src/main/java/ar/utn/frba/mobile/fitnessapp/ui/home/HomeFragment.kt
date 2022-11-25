package ar.utn.frba.mobile.fitnessapp.ui.home

import android.Manifest
import android.app.Activity
import android.os.Bundle
import android.view.KeyEvent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.NavController
import androidx.navigation.fragment.findNavController
import ar.utn.frba.mobile.fitnessapp.MyPreferences
import ar.utn.frba.mobile.fitnessapp.Permissions
import ar.utn.frba.mobile.fitnessapp.R
import ar.utn.frba.mobile.fitnessapp.databinding.FragmentHomeBinding
import ar.utn.frba.mobile.fitnessapp.model.Gym
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.android.gms.location.Priority

class HomeFragment : Fragment() {
    private val viewModel: HomeViewModel by viewModels()

    // This property is only valid between onCreateView and
    // onDestroyView.
    private var _binding: FragmentHomeBinding? = null
    private val binding get() = _binding!!

    private lateinit var navController: NavController
    private lateinit var fusedLocationClient: FusedLocationProviderClient

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        navController = findNavController()
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this.requireActivity())
        viewModel.search()
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

        var location: android.location.Location? = null
        if (checkLocationPermissions()) {
            location = lastLocation()
        }

        // Enter Key in searchbar raises the searchButton's onClick event.
        searchbar.setOnKeyListener(View.OnKeyListener { v, keyCode, event ->
            if(v.isFocused && keyCode == KeyEvent.KEYCODE_ENTER && event.action == KeyEvent.ACTION_UP) {
                searchButton.performClick()
                return@OnKeyListener true
            }

            false
        })

        searchButton.setOnClickListener { it ->
            hideKeyBoard(it)
            val query: String = searchbar.text.toString()
            viewModel.search(query, location)
        }

        viewModel.searchResults.observe(viewLifecycleOwner) { arrayList ->
            val adapter = GymSearchResultAdapter(requireContext(), location, arrayList)
            val resultList = binding.resultList
            resultList.adapter = adapter

            resultList.setOnItemClickListener { parent, _, position, _ ->
                val gym: Gym = parent.getItemAtPosition(position) as Gym
                val action = HomeFragmentDirections.actionNavigationHomeToDetailsFragment(gym)
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

    private fun lastLocation(): android.location.Location? {
        var location: android.location.Location? = null

        fusedLocationClient.getCurrentLocation(Priority.PRIORITY_BALANCED_POWER_ACCURACY, null).addOnSuccessListener {
            println(it)
            location = it
        }

        return location
    }

    private fun hideKeyBoard(view: View) {
        val inputManager = context?.getSystemService(Activity.INPUT_METHOD_SERVICE) as InputMethodManager
        inputManager.hideSoftInputFromWindow(view.windowToken, 0)
    }

    private fun checkLocationPermissions(): Boolean {
        return Permissions.checkForPermissions(
            this,
            Manifest.permission.ACCESS_COARSE_LOCATION,
            100,
            "Fitness App necesita poder acceder a tu ubicacion para poder arrojarte mejores resultados de búsqueda."
        )
    }
}