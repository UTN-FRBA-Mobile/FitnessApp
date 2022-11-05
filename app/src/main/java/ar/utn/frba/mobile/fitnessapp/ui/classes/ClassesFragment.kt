package ar.utn.frba.mobile.fitnessapp.ui.classes

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.navArgs
import ar.utn.frba.mobile.fitnessapp.MyPreferences
import ar.utn.frba.mobile.fitnessapp.R
import ar.utn.frba.mobile.fitnessapp.databinding.FragmentClassesBinding

class ClassesFragment : Fragment() {
    private val viewModel: ClassesViewModel by viewModels()

    // This property is only valid between onCreateView and
    // onDestroyView.
    private var _binding: FragmentClassesBinding? = null
    private val binding get() = _binding!!
    private val args: ClassesFragmentArgs by navArgs()


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentClassesBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val msg = "Classes de ${args.gym.name}"
        binding.gymText.text = msg
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