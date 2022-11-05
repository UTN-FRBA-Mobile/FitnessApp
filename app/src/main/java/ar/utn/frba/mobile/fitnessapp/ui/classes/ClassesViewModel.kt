package ar.utn.frba.mobile.fitnessapp.ui.classes

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import ar.utn.frba.mobile.fitnessapp.model.Gym
import ar.utn.frba.mobile.fitnessapp.model.GymClass

class ClassesViewModel(private val gym: Gym) : ViewModel() {
    private val _gymClasses = MutableLiveData<ArrayList<GymClass>>().apply {
        value = arrayListOf()
    }
    val gymClasses: LiveData<ArrayList<GymClass>> = _gymClasses

    init {
        // TODO request class data.
        _gymClasses.value = arrayListOf(GymClass(id = 1,
                                                 type = "Clase 1",
                                                 professor = "Pepe",
                                                 startDate = "10 JUL 2022 - 10:30 am",
                                                 endDate = "10 JUL 2022 - 12:00 am",
                                                 people = 10,
                                                 maxCapacity = 15),
                                        GymClass(id = 1,
                                                 type = "Clase 2",
                                                 professor = "Carlos",
                                                 startDate = "10 JUL 2022 - 10:30 am",
                                                 endDate = "10 JUL 2022 - 12:00 am",
                                                 people = 15,
                                                 maxCapacity = 15))
    }
}


@Suppress("UNCHECKED_CAST")
class ClassesViewModelFactory(private val gym: Gym) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return ClassesViewModel(gym) as T
    }

}