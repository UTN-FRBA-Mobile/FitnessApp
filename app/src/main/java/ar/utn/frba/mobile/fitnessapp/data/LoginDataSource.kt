package ar.utn.frba.mobile.fitnessapp.data

import ar.utn.frba.mobile.fitnessapp.data.model.LoggedInUser
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.tasks.await
import java.io.IOException

/**
 * Class that handles authentication w/ login credentials and retrieves user information.
 */
class LoginDataSource {
    private val auth: FirebaseAuth = Firebase.auth
    suspend fun login(username: String, password: String): Result<LoggedInUser> {
        try {
            // TODO: handle loggedInUser authentication
            var result: Result<LoggedInUser> = Result.Error(Exception("Wrong Credencials"))
            auth.signInWithEmailAndPassword(username, password).await()
            val fakeUser = LoggedInUser(java.util.UUID.randomUUID().toString(), "Facu Hump")
            result = Result.Success(fakeUser)
            return result
        } catch (e: Throwable) {
            return Result.Error(IOException("Error logging in", e))
        }
    }

    fun logout() {
        // TODO: revoke authentication
    }
}