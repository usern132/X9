package dk.itu.moapd.x9.s25137.data.datasources

import android.net.Uri
import com.google.android.gms.tasks.Task
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import javax.inject.Inject

class ImageRemoteDataSource @Inject constructor(
    private val storage: FirebaseStorage
) {
    fun uploadFile(localUri: Uri, remotePath: String): Task<Uri> {
        val ref: StorageReference = storage.reference.child(remotePath)
        // Put the file and then request the downloadUrl.
        return ref.putFile(localUri).continueWithTask { task ->
            if (!task.isSuccessful) {
                throw (task.exception ?: Exception("Upload failed"))
            }
            ref.downloadUrl
        }
    }

    fun delete(remotePath: String): Task<Void> {
        return storage.reference.child(remotePath).delete()
    }
}