package ar.utn.frba.mobile.fitnessapp.ui.qr

import android.Manifest
import android.content.pm.PackageManager
import android.graphics.Color
import android.media.MediaPlayer
import android.os.Bundle
import android.util.Size
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.camera.core.*
import androidx.camera.core.ImageAnalysis.STRATEGY_KEEP_ONLY_LATEST
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.camera.view.PreviewView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModelProvider
import ar.utn.frba.mobile.fitnessapp.MyPreferences
import ar.utn.frba.mobile.fitnessapp.Permissions
import ar.utn.frba.mobile.fitnessapp.R
import ar.utn.frba.mobile.fitnessapp.databinding.FragmentQrBinding
import com.google.common.util.concurrent.ListenableFuture
import com.google.mlkit.vision.barcode.Barcode
import com.google.mlkit.vision.barcode.BarcodeScanner
import com.google.mlkit.vision.barcode.BarcodeScannerOptions
import com.google.mlkit.vision.barcode.BarcodeScanning
import com.google.mlkit.vision.common.InputImage
import java.util.concurrent.ExecutionException
import java.util.concurrent.Executors
import kotlin.concurrent.thread


class QRFragment : Fragment() {

    private var _binding: FragmentQrBinding? = null
    private var showBG: Boolean = true
    private var cameraPermission: Boolean = false
    private lateinit var cameraProviderFuture : ListenableFuture<ProcessCameraProvider>
    private lateinit var previewView: PreviewView

    private var cameraProvider: ProcessCameraProvider? = null
    private var cameraSelector: CameraSelector? = null

    var mediaPlayer: MediaPlayer? = null

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
        binding.buttonScan.setOnClickListener {
            if(Permissions.checkForPermissions(this, Manifest.permission.CAMERA, 951, "dejame mostrar la camera")){
                launchCamera()
            }
        }
        binding.buttonClose.setOnClickListener{
            closeCamera()
        }

        previewView = activity?.findViewById(R.id.previewView)!!
        mediaPlayer = MediaPlayer.create(activity, R.raw.s1600)

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
        val cameraProvider = cameraProviderFuture.get()     //todo cambiar esta llamada que aparece 4 veces
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
        cameraSelector = availableCameraInfos[cameraID].cameraSelector
        val preview = Preview.Builder().build()
        preview.setSurfaceProvider(previewView.surfaceProvider)
        val viewPort = previewView.viewPort
        if (viewPort != null) {
            val useCaseGroup = UseCaseGroup.Builder()
                .addUseCase(preview)
                .setViewPort(viewPort)
                .build()
            cameraProvider.unbindAll()
            val camera = cameraProvider.bindToLifecycle(this, cameraSelector!!, useCaseGroup)
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

        //previewView
        setCameraProviderListener(cameraID)
        //analyzer
        processCameraProvider.observe(requireActivity()) { provider: ProcessCameraProvider? ->
                cameraProvider = provider
                bindAnalyseUseCase()
            }
    }

    private fun closeCamera() {
        val cameraProviderFuture = ProcessCameraProvider.getInstance(requireContext())
        val cameraProvider = cameraProviderFuture.get()
        cameraProvider.unbindAll()
        //todo cerrar thread de analysis?, hacer que vuelva a funcionar el scanner si se abre la camara una segunda vez
    }


    private fun bindAnalyseUseCase() {
        // Note that if you know which format of barcode your app is dealing with, detection will be faster
        val options = BarcodeScannerOptions.Builder().setBarcodeFormats(Barcode.FORMAT_ALL_FORMATS).build()

        val barcodeScanner: BarcodeScanner = BarcodeScanning.getClient(options)

        val analysisUseCase = ImageAnalysis.Builder().setTargetRotation(previewView.display.rotation)
            .setBackpressureStrategy(STRATEGY_KEEP_ONLY_LATEST) //waits for imageProxy.close() to process next image
            .setTargetResolution(Size(360, 480))    //supuestamente mejora el rendimiento, pero no se si hace algo porque la preview no lo tiene puesto
            .build()

        // Initialize our background executor
        val cameraExecutor = Executors.newSingleThreadExecutor()

        analysisUseCase.setAnalyzer(
            cameraExecutor,
            ImageAnalysis.Analyzer { imageProxy ->
                processImageProxy(barcodeScanner, imageProxy)
            }
        )

        try {
            cameraProvider!!.bindToLifecycle(
                this,
                cameraSelector!!,
                analysisUseCase
            )
        } catch (illegalStateException: IllegalStateException) {
            //Log.e(TAG, illegalStateException.message ?: "IllegalStateException")
        } catch (illegalArgumentException: IllegalArgumentException) {
            //Log.e(TAG, illegalArgumentException.message ?: "IllegalArgumentException")
        }
    }

    private fun processImageProxy(barcodeScanner: BarcodeScanner, imageProxy: ImageProxy) {
        val inputImage = InputImage.fromMediaImage(imageProxy.image!!, imageProxy.imageInfo.rotationDegrees)
        val guyPNG = activity?.findViewById<ImageView>(R.id.guyPNG)
        val text_qr = activity?.findViewById<TextView>(R.id.text_qr)

        barcodeScanner.process(inputImage)
            .addOnSuccessListener { barcodes ->
                barcodes.forEach { barcode ->
                    val bounds = barcode.boundingBox
                    val corners = barcode.cornerPoints

                    val rawValue = barcode.rawValue
                    //tvScannedData.text = barcode.rawValue
                    text_qr?.setText("QR result: " + barcode.rawValue)

                    //Toast.makeText(getActivity(), "puntos: " + corners[0].x + "." + corners[0].y + " " + corners[3].x + "." + corners[3].y, Toast.LENGTH_SHORT).show()

                    val centerX = (corners[0].x + corners[1].x)/2
                    val centerY = (corners[0].y + corners[2].y)/2

                    //todo adaptar las coordenadas a la posicion absoluta de la pantalla
                    guyPNG?.setX(centerX.toFloat())
                    guyPNG?.setY(centerY.toFloat());

                    guyPNG?.setVisibility(View.VISIBLE);  // make image visible
                    mediaPlayer?.start()

                    val valueType = barcode.valueType

                }
            }
            .addOnFailureListener {
                //Log.e(TAG, it.message ?: it.toString())
            }
            .addOnCompleteListener {
                //Once the image being analyzed
                //closed it by calling ImageProxy.close()
                thread {
                    Thread.sleep(3_000) //wait 3 seconds to scan next image
                    guyPNG?.setVisibility(View.INVISIBLE)
                    imageProxy.close()
                }
            }
    }

    private var cameraProviderLiveData: MutableLiveData<ProcessCameraProvider>? = null
    val processCameraProvider: LiveData<ProcessCameraProvider>
        get() {
            if (cameraProviderLiveData == null) {
                cameraProviderLiveData = MutableLiveData()
                val cameraProviderFuture = ProcessCameraProvider.getInstance(requireContext())
                cameraProviderFuture.addListener(
                    Runnable {
                        try {
                            cameraProviderLiveData!!.setValue(cameraProviderFuture.get())
                        } catch (e: ExecutionException) {
                            //Log.e(TAG, "Unhandled exception", e)
                        }
                    },
                    ContextCompat.getMainExecutor(requireContext())
                )
            }
            return cameraProviderLiveData!!
        }

}