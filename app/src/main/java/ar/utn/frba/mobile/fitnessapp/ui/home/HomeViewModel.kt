package ar.utn.frba.mobile.fitnessapp.ui.home

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import ar.utn.frba.mobile.fitnessapp.model.Gym

class HomeViewModel : ViewModel() {
    private val _searchResults = MutableLiveData<List<Gym>>().apply {
        value = emptyList()
    }
    val searchResults: LiveData<List<Gym>> = _searchResults

    init {
        // TODO: Request initial search results
        _searchResults.value = listOf(Gym(name="Aca a la vuelta"), Gym(name="Un gimnasio re copado"))
    }

    fun search(query: String) {
        // TODO: Request gyms using a string
        println("Querying: $query")
        _searchResults.value = listOf(Gym(name="Un gimnasio que busqué yo"))
    }
}