package ar.utn.frba.mobile.fitnessapp.ui.qr

import android.Manifest
import android.content.pm.PackageManager
import android.graphics.Color
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.Spinner
import android.widget.TextView
import android.widget.Toast
import androidx.camera.core.*
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.camera.view.PreviewView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import ar.utn.frba.mobile.fitnessapp.MyPreferences
import ar.utn.frba.mobile.fitnessapp.Permissions
import ar.utn.frba.mobile.fitnessapp.R
import ar.utn.frba.mobile.fitnessapp.databinding.FragmentQrBinding
import com.google.common.util.concurrent.ListenableFuture
import java.util.concurrent.ExecutionException


class QRFragment : Fragment() {

    private var _binding: FragmentQrBinding? = null
    private var showBG: Boolean = true
    private var cameraPermission: Boolean = false
    private lateinit var cameraProviderFuture : ListenableFuture<ProcessCameraProvider>
    private lateinit var previewView: PreviewView

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

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.buttonCamera.setOnClickListener {
            if(Permissions.checkForPermissions(this, Manifest.permission.CAMERA, 951, "dejame mostrar la camera"))
                launchCamera()
        }
        binding.buttonClose.setOnClickListener{
            closeCamera()
        }
        binding.buttonScan.setOnClickListener{
            scanQR()
        }

        previewView = activity?.findViewById(R.id.previewView)!!;

    }

    override fun onStart() {
        super.onStart()
        val qrView = activity?.findViewById<ConstraintLayout>(R.id.qrScreen)

        showBG = MyPreferences.isShowBGsPreferredView(context!!)
        if(showBG){
            qrView?.setBackgroundResource(R.drawable.bg_gymx);
        }

        updatePermissionDisplay()


        //dropdown
        val cameraProviderFuture = ProcessCameraProvider.getInstance(requireContext())
        val cameraProvider = cameraProviderFuture.get()
        val size: Int = cameraProvider.availableCameraInfos.size

        val dropdown: Spinner? = activity?.findViewById(R.id.spinner)
        val items = Array(size){"$it"}
        val adapter: ArrayAdapter<String> = ArrayAdapter<String>(context!!, android.R.layout.simple_spinner_dropdown_item, items)
        dropdown?.adapter = adapter

    }

    private fun setCameraProviderListener(cameraID: Int) {
        val cameraProviderFuture = ProcessCameraProvider.getInstance(requireContext())
        cameraProviderFuture.addListener({
            try {
                val cameraProvider = cameraProviderFuture.get()
                bindPreview(cameraProvider, cameraID)
            } catch (e: ExecutionException) {
                // No errors need to be handled for this Future
                // This should never be reached
                e.printStackTrace()
            } catch (e: InterruptedException) {
                e.printStackTrace()
            }
        }, ContextCompat.getMainExecutor(requireContext()))
    }

    private fun bindPreview(cameraProvider: ProcessCameraProvider, cameraID: Int) {
        val availableCameraInfos: List<CameraInfo> = cameraProvider.availableCameraInfos

        //val cameraSelector = CameraSelector.Builder().requireLensFacing(CameraSelector.LENS_FACING_BACK).build()
        val cameraSelector = availableCameraInfos[cameraID].cameraSelector
        val preview = Preview.Builder().build()
        preview.setSurfaceProvider(previewView.surfaceProvider)
        val viewPort = previewView.viewPort
        if (viewPort != null) {
            val useCaseGroup = UseCaseGroup.Builder()
                .addUseCase(preview)
                .setViewPort(viewPort)
                .build()
            cameraProvider.unbindAll()
            val camera = cameraProvider.bindToLifecycle(this, cameraSelector, useCaseGroup)
            val cameraControl: CameraControl = camera.getCameraControl()
            cameraControl.setLinearZoom(0.3.toFloat())
        }
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
        val mySpinner = activity?.findViewById<Spinner>(R.id.spinner)
        val cameraID: Int = Integer.parseInt(mySpinner?.getSelectedItem().toString())

        setCameraProviderListener(cameraID)
    }

    private fun closeCamera() {
        val cameraProviderFuture = ProcessCameraProvider.getInstance(requireContext())
        val cameraProvider = cameraProviderFuture.get()
        cameraProvider.unbindAll()
    }

    private fun scanQR() {
        Toast.makeText(getActivity(), "Not yet implemented", Toast.LENGTH_SHORT).show()
    }

}