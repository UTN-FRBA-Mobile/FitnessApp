package ar.utn.frba.mobile.fitnessapp.model.backend

import ar.utn.frba.mobile.fitnessapp.model.Gym
import ar.utn.frba.mobile.fitnessapp.model.GymClass
import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Path

interface APIService {
    @GET("gyms")
    fun gyms(): Call<List<Gym>>

    @POST("gyms/search")
    fun search(@Body gymQuery: GymQuery): Call<List<Gym>>

    @GET("gyms/{id}/classes")
    fun classes(@Path("id") gymId: Int): Call<List<GymClass>>

    @GET("gyms/{id}/image")
    fun image(@Path("id") gymId: Int): Call<String>

    @POST("gyms/{id}/classes/{classId}/reserve")
    fun reserve(@Body userId: BookingBody, @Path("id") gymId: Int, @Path("classId") classId: Int): Call<Unit>

    @POST("gyms/{id}/classes/{classId}/unbook")
    fun unbook(@Body userId: BookingBody, @Path("id") gymId: Int, @Path("classId") classId: Int): Call<Unit>
}