package ar.utn.frba.mobile.fitnessapp.ui.qr

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class QRViewModel : ViewModel() {
    // TODO: Implement the ViewModel
    private val _text = MutableLiveData<String>().apply {
        value = "QR screen"
    }
    val text: LiveData<String> = _text
}