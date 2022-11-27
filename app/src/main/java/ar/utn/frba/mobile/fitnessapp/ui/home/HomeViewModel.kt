package ar.utn.frba.mobile.fitnessapp.ui.home

import android.widget.Toast
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import ar.utn.frba.mobile.fitnessapp.model.*
import ar.utn.frba.mobile.fitnessapp.model.backend.BackendService
import ar.utn.frba.mobile.fitnessapp.model.backend.GymQuery
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class HomeViewModel : ViewModel() {
    private val _searchResults = MutableLiveData<ArrayList<Gym>>().apply {
        value = arrayListOf()
    }
    val searchResults: LiveData<ArrayList<Gym>> = _searchResults

    private val backend: BackendService = BackendService.create()

    fun search(query: String = "", location: Location? = null, onFailure: (Call<List<Gym>>, t: Throwable) -> Unit) {
        val searchQuery = GymQuery(name         = query,
                                   nearPoint    = location,
                                   searchRadius = 100_000_000.0)   // Por defecto uso 100_000 km

        backend.search(searchQuery).enqueue(gymsRequestCallback(location, onFailure))
    }

    private fun gymsRequestCallback(location: Location?, onFailure: (Call<List<Gym>>, t: Throwable) -> Unit): Callback<List<Gym>> {
        return object : Callback<List<Gym>> {
            override fun onResponse(call: Call<List<Gym>>, response: Response<List<Gym>>) {
                val gyms = response.body()!!

                var results = gyms
                if (location != null) {
                    results = gyms.sortedBy { it.distance(location) }
                }

                _searchResults.value = ArrayList(results)
            }

            override fun onFailure(call: Call<List<Gym>>, t: Throwable) {
                onFailure(call, t)
                _searchResults.value = arrayListOf()
            }
        }
    }
}