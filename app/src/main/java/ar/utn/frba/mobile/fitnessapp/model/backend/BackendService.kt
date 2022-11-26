package ar.utn.frba.mobile.fitnessapp.model.backend

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