package ar.utn.frba.mobile.fitnessapp.ui.home

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import ar.utn.frba.mobile.fitnessapp.model.Gym
import ar.utn.frba.mobile.fitnessapp.model.GymClass
import ar.utn.frba.mobile.fitnessapp.model.Location
import ar.utn.frba.mobile.fitnessapp.model.asLocatable

class HomeViewModel : ViewModel() {
    private val _searchResults = MutableLiveData<ArrayList<Gym>>().apply {
        value = arrayListOf()
    }
    val searchResults: LiveData<ArrayList<Gym>> = _searchResults

    fun search(query: String = "", location: android.location.Location? = null) {
        // TODO: Request gyms using a string
        val exampleClasses = arrayListOf(
            GymClass(id = 1,
                     gymId = 1,
                     type = "Clase 1",
                     professor = "Pepe",
                     startDate = "10 JUL 2022 - 10:30 am",
                     endDate = "10 JUL 2022 - 12:00 am",
                     people = 10,
                     maxCapacity = 15),
            GymClass(id = 2,
                     gymId = 2,
                     type = "Clase 2",
                     professor = "Carlos",
                     startDate = "10 JUL 2022 - 10:30 am",
                     endDate = "10 JUL 2022 - 12:00 am",
                     people = 15,
                     maxCapacity = 15)
        )

        var queryResults = listOf(Gym(id=1,
                                      avatar="",
                                      name="Un gimnasio que busqué yo",
                                      location=Location(latitude=1.0, longitude=1.0),
                                      classes=exampleClasses))
        if (query == "") {
            queryResults = listOf(Gym(id=2,
                                      avatar="",
                                      name="Aca a la vuelta",
                                      location=Location(latitude=1.01, longitude=1.0),
                                      classes=exampleClasses),
                                  Gym(id=3,
                                      avatar="",
                                      name="Un gimnasio re copado",
                                      location=Location(latitude=1.0, longitude=1.0),
                                      classes=exampleClasses))
        }

        var results = queryResults
        if (location != null) {
            results = queryResults.sortedBy { it.distance(location.asLocatable()) }
        }

        _searchResults.value = ArrayList(results)
    }
}