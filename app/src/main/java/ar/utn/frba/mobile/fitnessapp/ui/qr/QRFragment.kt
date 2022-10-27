package ar.utn.frba.mobile.fitnessapp.ui.qr

import android.Manifest
import android.content.pm.PackageManager
import android.graphics.Color
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import android.widget.Toast
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import ar.utn.frba.mobile.fitnessapp.MyPreferences
import ar.utn.frba.mobile.fitnessapp.Permissions
import ar.utn.frba.mobile.fitnessapp.R
import ar.utn.frba.mobile.fitnessapp.databinding.FragmentQrBinding

class QRFragment : Fragment() {

    private var _binding: FragmentQrBinding? = null
    private var showBG: Boolean = true
    private var cameraPermission: Boolean = false

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val qrViewModel =
            ViewModelProvider(this).get(QRViewModel::class.java)

        _binding = FragmentQrBinding.inflate(inflater, container, false)
        val root: View = binding.root

        val textView: TextView = binding.textQr
        qrViewModel.text.observe(viewLifecycleOwner) {
            textView.text = it
        }
        return root
    }

    override fun onStart() {
        super.onStart()
        val qrView = activity?.findViewById<ConstraintLayout>(R.id.qrScreen)

        showBG = MyPreferences.isShowBGsPreferredView(context!!)
        if(showBG){
            qrView?.setBackgroundResource(R.drawable.bg_gymx);
        }

        updatePermissionDisplay()
    }

    private fun updatePermissionDisplay(){
        val textPermissionStatus = activity?.findViewById<TextView>(R.id.cameraPermissionStatus)

        cameraPermission = Permissions.hasPermissions(context!!, Manifest.permission.CAMERA)
        if(cameraPermission){
            textPermissionStatus?.setText("Camera permission: Positivo")
            textPermissionStatus?.setBackgroundColor(Color.GREEN)
        } else{
            textPermissionStatus?.setText("Camera permission: Negativo")
            textPermissionStatus?.setBackgroundColor(Color.RED)
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.buttonCamera.setOnClickListener {
            if(Permissions.checkForPermissions(this, Manifest.permission.CAMERA, 951, "dejame mostrar la camera"))
                launchCamera()
        }
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<String>, grantResults: IntArray) {
        when (requestCode) {
            951 -> {
                if (grantResults.count() > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    // tenemos permiso, continuar con la tarea
                    Toast.makeText(getActivity(), R.string.permissionGranted, Toast.LENGTH_SHORT).show()
                    updatePermissionDisplay()
                    launchCamera()
                }
                else {
                    // Controlar que no nos dieron permiso, por ejemplo mostrando un Toast
                    Toast.makeText(getActivity(), R.string.permissionBlocked, Toast.LENGTH_SHORT).show()
                }
                return
            }
        }

        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    private fun launchCamera() {
        Toast.makeText(getActivity(), "Tenemos permisos, podemos avanzar", Toast.LENGTH_SHORT).show()
    }


}