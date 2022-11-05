package ar.utn.frba.mobile.fitnessapp.ui.home

import android.net.Uri
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import ar.utn.frba.mobile.fitnessapp.model.Gym

class HomeViewModel : ViewModel() {
    private val _searchResults = MutableLiveData<ArrayList<Gym>>().apply {
        value = arrayListOf()
    }
    val searchResults: LiveData<ArrayList<Gym>> = _searchResults

    init {
        search()
    }

    fun search(query: String = "") {
        // TODO: Request gyms using a string
        if (query == "") {
            _searchResults.value = arrayListOf(Gym(avatar="", name="Aca a la vuelta",           address="A la vuelta te dije salame"),
                                               Gym(avatar="", name="Un gimnasio re copado",     address="Aca nomas, re tranca"))
        } else {
            _searchResults.value = arrayListOf(Gym(avatar="", name="Un gimnasio que busqué yo", address="Me invito un amigo, yo ke se"))
        }
    }
}