package dk.itu.moapd.x9.s25137.data.datasources

import com.google.firebase.auth.FirebaseAuth
import dk.itu.moapd.x9.s25137.domain.models.User

class AuthRemoteDataSource(
    private val auth: FirebaseAuth = FirebaseAuth.getInstance()
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