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
        // TODO: Request initial search results
        _searchResults.value = arrayListOf(Gym(avatar=Uri.EMPTY, name="Aca a la vuelta", address="A la vuelta te dije salame"),
                                           Gym(avatar=Uri.EMPTY, name="Un gimnasio re copado", address="Aca nomas, re tranca"))
    }

    fun search(query: String) {
        // TODO: Request gyms using a string
        println("Querying: $query")
        _searchResults.value = arrayListOf(Gym(avatar=Uri.EMPTY, name="Un gimnasio que busqué yo", address="Me invito un amigo, yo ke se"))
    }
}