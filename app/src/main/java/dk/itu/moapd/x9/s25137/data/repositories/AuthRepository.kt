package dk.itu.moapd.x9.s25137.data.repositories

import com.google.firebase.auth.FirebaseAuth
import dk.itu.moapd.x9.s25137.domain.models.User

class AuthRepository {
    private val _currentUser = FirebaseAuth.getInstance().currentUser
    val currentUser: User?
        get() = if (_currentUser == null) null
        else User(
            name = _currentUser.displayName,
            email = _currentUser.email,
            photoUrl = _currentUser.photoUrl
        )

    fun signOut() = FirebaseAuth.getInstance().signOut()

}