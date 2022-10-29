package ar.utn.frba.mobile.fitnessapp.ui.qr

import android.app.Application
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.core.content.ContextCompat
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import java.util.concurrent.ExecutionException

class QRViewModel(application: Application) : AndroidViewModel(application) {
    // TODO: Implement the ViewModel
    private val _text = MutableLiveData<String>().apply {
        value = "QR screen"
    }
    val text: LiveData<String> = _text


    //https://c1ctech.com/android-scanning-barcode-qr-code-using-google-ml-kit-and-camerax/

    private var cameraProviderLiveData: MutableLiveData<ProcessCameraProvider>? = null
    val processCameraProvider: LiveData<ProcessCameraProvider>
        get() {
            if (cameraProviderLiveData == null) {
                cameraProviderLiveData = MutableLiveData()
                val cameraProviderFuture = ProcessCameraProvider.getInstance(getApplication())
                cameraProviderFuture.addListener(
                    Runnable {
                        try {
                            cameraProviderLiveData!!.setValue(cameraProviderFuture.get())
                        } catch (e: ExecutionException) {
                            //Log.e(TAG, "Unhandled exception", e)
                        }
                    },
                    ContextCompat.getMainExecutor(getApplication())
                )
            }
            return cameraProviderLiveData!!
        }

    public fun reset(){
        cameraProviderLiveData = null
    }

    companion object {
        private const val TAG = "CameraXViewModel"
    }
}

