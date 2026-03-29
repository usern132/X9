package dk.itu.moapd.x9.s25137.data.datasources

import com.google.firebase.auth.FirebaseAuth
import dk.itu.moapd.x9.s25137.domain.models.User
import javax.inject.Inject

class AuthRemoteDataSource @Inject constructor(
    private val auth: FirebaseAuth
) {
    val currentUser: User?
        get() = auth.currentUser?.let { user -> // if no user is logged in, the safe call (?.) returns null
            User(
                uid = user.uid,
                name = user.displayName,
                email = user.email,
                photoUri = user.photoUrl
            )
        }

    fun signOut() = auth.signOut()
}