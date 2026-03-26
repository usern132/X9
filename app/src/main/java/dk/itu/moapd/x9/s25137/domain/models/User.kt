package dk.itu.moapd.x9.s25137.domain.models

import android.net.Uri

data class User(
    var uid: String,
    var name: String?,
    var email: String?,
    var photoUrl: Uri? = null
)
