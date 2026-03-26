package dk.itu.moapd.x9.s25137.data.repositories

import dk.itu.moapd.x9.s25137.data.datasources.AuthRemoteDataSource
import dk.itu.moapd.x9.s25137.domain.models.User

class AuthRepository(
    private val authRemoteDataSource: AuthRemoteDataSource = AuthRemoteDataSource()
) {
    val currentUser: User?
        get() = authRemoteDataSource.currentUser

    fun signOut() = authRemoteDataSource.signOut()

}