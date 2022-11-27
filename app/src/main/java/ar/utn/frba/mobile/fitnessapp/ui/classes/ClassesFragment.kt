package ar.utn.frba.mobile.fitnessapp.ui.classes

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import android.widget.ListView
import android.widget.Toast
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import ar.utn.frba.mobile.fitnessapp.MyPreferences
import ar.utn.frba.mobile.fitnessapp.R
import ar.utn.frba.mobile.fitnessapp.databinding.FragmentClassesBinding
import ar.utn.frba.mobile.fitnessapp.model.GymClass
import ar.utn.frba.mobile.fitnessapp.model.backend.BackendService
import ar.utn.frba.mobile.fitnessapp.model.backend.call
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class ClassesFragment : Fragment() {
    // This property is only valid between onCreateView and
    // onDestroyView.
    private var _binding: FragmentClassesBinding? = null
    private val binding get() = _binding!!
    private val backend: BackendService = BackendService.create()

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

        backend.classes(gymId = args.gym.id).call(
            onResponse = { _, response ->
                val classes = response.body()!!

                val adapter = GymClassAdapter(requireContext(), ArrayList(classes))
                val classList: ListView = binding.classList
                classList.adapter = adapter

                classList.setOnItemClickListener { parent, _, position, _ ->
                    val gymClass = parent.getItemAtPosition(position) as GymClass
                    val action = ClassesFragmentDirections.actionClassesFragmentToAcceptDialogFragment(gymClass)
                    findNavController().navigate(action)
                }
            },

            onFailure = { _, t ->
                Toast.makeText(requireContext(), "An error occurred when attempting to communicate with the server", Toast.LENGTH_LONG).show()
                Log.println(Log.WARN, "[CLASSES_FRAGMENT][GET_CLASSES]", t.toString())
            }
        )
    }

    override fun onStart() {
        super.onStart()
        val showBG = MyPreferences.isShowBGsPreferredView(requireContext())
        if(showBG){
            activity?.findViewById<FrameLayout>(R.id.availableClasses)?.setBackgroundResource(R.drawable.bg_yogax)
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}