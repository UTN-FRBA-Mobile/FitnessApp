package ar.utn.frba.mobile.fitnessapp.model.backend

import android.text.method.BaseKeyListener
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class BackendService private constructor(private val service: APIService) : APIService by service {
    companion object {
        val baseUrl: String get() = "https://utn-fitness-api.fly.dev/api/v1/"

        fun create(): BackendService {
            val service: APIService = Retrofit.Builder()
                .baseUrl(baseUrl)
                .addConverterFactory(GsonConverterFactory.create())
                .build()
                .create(APIService::class.java)

            return BackendService(service)
        }
    }
}

fun <T> Call<T>.call(onResponse: (Call<T>, Response<T>) -> Unit,
                     onFailure: (Call<T>, t: Throwable) -> Unit) {
    enqueue(object : Callback<T> {
        override fun onResponse(call: Call<T>, response: Response<T>) {
            onResponse(call, response)
        }

        override fun onFailure(call: Call<T>, t: Throwable) {
            onFailure(call, t)
        }
    })
}