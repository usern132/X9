package dk.itu.moapd.x9.s25137.data.datasources

import com.google.firebase.Firebase
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.database
import dk.itu.moapd.x9.s25137.domain.models.Report
import io.github.cdimascio.dotenv.dotenv

/* Code adapted from the MOAPD 2026 subject repository, found at https://github.com/fabricionarcizo/moapd2026/.
 * Its original license is attached below.

 * MIT License
 *
 * Copyright (c) 2026 Fabricio Batista Narcizo
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy of this software and
 * associated documentation files (the "Software"), to deal in the Software without restriction,
 * including without limitation the rights to use, copy, modify, merge, publish, distribute,
 * sublicense, and/or sell copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all copies or
 * substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED, INCLUDING BUT
 * NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND
 * NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM,
 * DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
 */

class ReportRemoteDataSource(
    private val root: DatabaseReference = Firebase.database(DATABASE_URL).reference
) {
    companion object {
        val DATABASE_URL: String = dotenv {
            directory = "/assets"
            filename = "env"
        }["DATABASE_URL"]
        private const val PATH_USERS = "users"
    }

    fun reportsQuery(userId: String) =
        root
            .child(PATH_USERS)
            .child(userId)
            .orderByChild("timestamp")

    fun insert(userId: String, report: Report) {
        fun newChild(): DatabaseReference = root
            .child(PATH_USERS)
            .child(userId)
            .push()

        val newChildKey = newChild().key ?: return
        root
            .child(PATH_USERS)
            .child(userId)
            .child(newChildKey)
            .setValue(report)
    }

    fun update(userId: String, key: String, report: Report) {
        root
            .child(PATH_USERS)
            .child(userId)
            .child(key)
            .setValue(report)
    }

    fun delete(userId: String, key: String) {
        root
            .child(PATH_USERS)
            .child(userId)
            .child(key)
            .removeValue()
    }
}