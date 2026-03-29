package dk.itu.moapd.x9.s25137.data.repositories

import dk.itu.moapd.x9.s25137.data.datasources.AuthRemoteDataSource
import dk.itu.moapd.x9.s25137.domain.models.User
import javax.inject.Inject

class AuthRepository @Inject constructor(
    private val authRemoteDataSource: AuthRemoteDataSource
) {
    val currentUser: User?
        get() = authRemoteDataSource.currentUser

    fun logOut() = authRemoteDataSource.signOut()

}