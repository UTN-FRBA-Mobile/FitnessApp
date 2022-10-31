package ar.utn.frba.mobile.fitnessapp.ui.qr

import android.Manifest
import android.content.pm.PackageManager
import android.graphics.Color
import android.media.MediaPlayer
import android.os.Bundle
import android.util.Size
import android.view.LayoutInflater
import android.view.Surface.ROTATION_0
import android.view.Surface.ROTATION_90
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.annotation.Dimension
import androidx.camera.core.*
import androidx.camera.core.ImageAnalysis.STRATEGY_KEEP_ONLY_LATEST
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
import com.google.mlkit.vision.barcode.Barcode
import com.google.mlkit.vision.barcode.BarcodeScanner
import com.google.mlkit.vision.barcode.BarcodeScannerOptions
import com.google.mlkit.vision.barcode.BarcodeScanning
import com.google.mlkit.vision.common.InputImage
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
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
    private var previewUseCase: Preview? = null

    private var offsetX: Float = 0F
    private var offsetY: Float = 0F
    private var relation: Float = 1F

    var mediaPlayer: MediaPlayer? = null

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val qrViewModel = ViewModelProvider(this).get(QRViewModel::class.java)

        _binding = FragmentQrBinding.inflate(inflater, container, false)
        val root: View = binding.root

        val textView: TextView = binding.cameraInfo
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

        cameraProviderFuture = ProcessCameraProvider.getInstance(requireContext())
        cameraProvider = cameraProviderFuture.get()

        updatePermissionDisplay()
        setDropdown()


    }

    override fun onResume() {
        super.onResume()
        val guyPNG = activity?.findViewById<ImageView>(R.id.guyPNG)

        //sub half of the image's width and height to the offset, to center the image

        //val dpi = context!!.resources.displayMetrics.density
        val size = context!!.resources.getDimension(R.dimen.image_size)
        offsetX -= size/2
        offsetY -= size/2


        //add the absolute start position of previewView to the Y offset (to account for layout_marginTop="36dp")
        /*val posScreen: IntArray = intArrayOf(0, 0)
        previewView.getLocationOnScreen(posScreen)
        val posWindow: IntArray = intArrayOf(0, 0)
        previewView.getLocationInWindow(posWindow)*/

        offsetX += previewView.x
        offsetY += previewView.y

        /*Toast.makeText(getActivity(), offsetY.toString(), Toast.LENGTH_SHORT).show()
        guyPNG?.setX(offsetX)
        guyPNG?.setY(offsetY)
        guyPNG?.setVisibility(View.VISIBLE)*/

    }

    private fun setDropdown() {
        val size: Int = cameraProvider!!.availableCameraInfos.size

        val dropdown: Spinner? = activity?.findViewById(R.id.spinner)
        val items = Array(size){"$it"}
        val adapter: ArrayAdapter<String> = ArrayAdapter<String>(context!!, android.R.layout.simple_spinner_dropdown_item, items)
        dropdown?.adapter = adapter
    }

    private fun updatePermissionDisplay(){
        val textPermissionStatus = activity?.findViewById<TextView>(R.id.resultBox)

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

        closeCamera()

        val availableCameraInfos: List<CameraInfo> = cameraProvider!!.availableCameraInfos
        cameraSelector = availableCameraInfos[cameraID].cameraSelector

        //setCameraProviderListener(cameraID)

        ViewModelProvider(requireActivity())[QRViewModel::class.java]
            .processCameraProvider
            .observe(requireActivity()) { provider: ProcessCameraProvider? ->
                cameraProvider = provider
                bindPreviewUseCase()
                bindAnalyseUseCase()
            }
    }

    private fun closeCamera() {
        cameraProvider!!.unbindAll()
    }


    // ----------------------------------------------------------------------------------------------------------------------------------------------------------------------

    private fun bindPreviewUseCase() {
        if (previewUseCase != null) {
            cameraProvider!!.unbind(previewUseCase)
        }

        previewUseCase = Preview.Builder()
            .setTargetRotation(previewView.display.rotation)
            .build()

        //Attach the PreviewView surface provider to the preview use case.
        previewUseCase!!.setSurfaceProvider(previewView.surfaceProvider)

        try {
            cameraProvider!!.bindToLifecycle(
                this,
                cameraSelector!!,
                previewUseCase
            )
        } catch (illegalStateException: IllegalStateException) {
            //Log.e(TAG, illegalStateException.message ?: "IllegalStateException")
        } catch (illegalArgumentException: IllegalArgumentException) {
            //Log.e(TAG, illegalArgumentException.message ?: "IllegalArgumentException")
        }
    }

    private fun bindAnalyseUseCase() {
        // Note that if you know which format of barcode your app is dealing with, detection will be faster
        val options = BarcodeScannerOptions.Builder().setBarcodeFormats(Barcode.FORMAT_ALL_FORMATS).build()

        val barcodeScanner: BarcodeScanner = BarcodeScanning.getClient(options)

        val analysisUseCase = ImageAnalysis.Builder().setTargetRotation(previewView.display.rotation)
            .setBackpressureStrategy(STRATEGY_KEEP_ONLY_LATEST) //waits for imageProxy.close() to process next image
            .setTargetResolution(Size(480, 640))
            .build()


        // Initialize our background executor
        val cameraExecutor = Executors.newSingleThreadExecutor() //quien cierra thread?

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
            val info = analysisUseCase.resolutionInfo
            //Toast.makeText(getActivity(), info?.toString(), Toast.LENGTH_SHORT).show()
            readFirstFrame(info!!.rotationDegrees, info.resolution.width, info.resolution.height)

        } catch (illegalStateException: IllegalStateException) {
            //Log.e(TAG, illegalStateException.message ?: "IllegalStateException")
        } catch (illegalArgumentException: IllegalArgumentException) {
            //Log.e(TAG, illegalArgumentException.message ?: "IllegalArgumentException")
        }
    }

    private fun readFirstFrame(rotation: Int, frameWidth: Int, frameHeight: Int){
        val cameraInfoBox = activity?.findViewById<TextView>(R.id.camera_info)

        val width: Int
        val height: Int
        if(rotation == 90 || rotation == 270){
            //todo para 270 sale la x espejada ._.
            width = frameHeight
            height = frameWidth
        } else{
            //rotation 0 or 180
            width = frameWidth
            height = frameHeight
        }

        relation = previewView.width.toFloat() / width.toFloat()

        val imageRatio: Float = (width.toFloat()/height.toFloat())
        var previewRatio: Float = (previewView.width.toFloat()/previewView.height.toFloat())
        val imageProxySize: String
        val previewViewSize: String
        var newHeight = previewView.height

        //fix when imageProxy ratio ≠ previewView aspect ratio
        if(imageRatio != previewRatio){
            val viewWidth = previewView.width.toFloat()

            newHeight = (viewWidth/imageRatio).toInt()
            //Toast.makeText(getActivity(), newHeight.toString(), Toast.LENGTH_SHORT).show()
            previewView.layoutParams.height = newHeight

            previewRatio = (previewView.width.toFloat()/newHeight.toFloat())
        }

        imageProxySize = "camera: " + width.toString() + "x" + height.toString() + " " + (imageRatio*100).toInt().toString()+ ". "
        previewViewSize = "preview: " + previewView.width.toString() + "x" + newHeight.toString() + " " + (previewRatio*100).toInt().toString() + ". "
        val rot = "$rotation "

        cameraInfoBox?.setText(rot + imageProxySize + previewViewSize)  //crashea si es muy largo el string ????

    }

    private fun processImageProxy(barcodeScanner: BarcodeScanner, imageProxy: ImageProxy) {
        val inputImage = InputImage.fromMediaImage(imageProxy.image!!, imageProxy.imageInfo.rotationDegrees)
        val guyPNG = activity?.findViewById<ImageView>(R.id.guyPNG)
        val resultBox = activity?.findViewById<TextView>(R.id.resultBox)

        //todo crashes when switching orientation, and when starting the camera in horizontal mode

        barcodeScanner.process(inputImage)
            .addOnSuccessListener { barcodes ->
                barcodes.forEach { barcode ->
                    val bounds = barcode.boundingBox
                    val corners = barcode.cornerPoints

                    val rawValue = barcode.rawValue
                    //tvScannedData.text = barcode.rawValue
                    resultBox?.setText("QR result: " + barcode.rawValue)
                    resultBox?.setBackgroundColor(Color.BLACK)

                    //Toast.makeText(getActivity(), "puntos: " + corners[0].x + "." + corners[0].y + " " + corners[3].x + "." + corners[3].y, Toast.LENGTH_SHORT).show()

                    val centerX = (corners[0].x.toFloat() + corners[1].x.toFloat())/2 * relation
                    val centerY = (corners[0].y.toFloat() + corners[2].y.toFloat())/2 * relation

                    guyPNG?.setX(centerX + offsetX)
                    guyPNG?.setY(centerY + offsetY);

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
                /*CoroutineScope(Dispatchers.IO).launch {
                    delay(3000 - System.currentTimeMillis())
                    guyPNG?.setVisibility(View.INVISIBLE)
                    imageProxy.close()
                }*/
            }
    }







    // ------------------------------------------------------------------------------------------ for camera preview without scanner
    // https://stackoverflow.com/questions/60301296/how-to-use-camerax-with-previewview
    private fun setCameraProviderListener(cameraID: Int) {
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
}