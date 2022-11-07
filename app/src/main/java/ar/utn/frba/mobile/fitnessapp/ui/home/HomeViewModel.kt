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

    fun search(location: android.location.Location, query: String = "") {
        // TODO: Request gyms using a string
        val exampleClasses = arrayListOf(
            GymClass(id = 1,
                     type = "Clase 1",
                     professor = "Pepe",
                     startDate = "10 JUL 2022 - 10:30 am",
                     endDate = "10 JUL 2022 - 12:00 am",
                     people = 10,
                     maxCapacity = 15),
            GymClass(id = 2,
                     type = "Clase 2",
                     professor = "Carlos",
                     startDate = "10 JUL 2022 - 10:30 am",
                     endDate = "10 JUL 2022 - 12:00 am",
                     people = 15,
                     maxCapacity = 15)
        )

        var queryResults = arrayListOf(Gym(id=1,
                                           avatar="",
                                           name="Un gimnasio que busqué yo",
                                           location=Location(latitude=1.0, longitude=1.0),
                                           classes=exampleClasses))
        if (query == "") {
            queryResults = arrayListOf(Gym(id=2,
                                           avatar="",
                                           name="Aca a la vuelta",
                                           location=Location(latitude=1.0, longitude=1.0),
                                           classes=exampleClasses),
                                       Gym(id=3,
                                           avatar="",
                                           name="Un gimnasio re copado",
                                           location=Location(latitude=1.0, longitude=1.0),
                                           classes=exampleClasses))
        }

        _searchResults.value = ArrayList(queryResults.sortedBy { it.distance(location.asLocatable()) })
    }
}