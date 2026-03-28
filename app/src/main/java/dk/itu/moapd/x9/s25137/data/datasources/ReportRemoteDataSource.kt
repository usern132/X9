package dk.itu.moapd.x9.s25137.data.datasources

import com.google.firebase.Firebase
import com.google.firebase.database.DatabaseError
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
        private const val PATH_REPORTS = "reports"
    }

    private fun reportsReference(): DatabaseReference = root
        .child(PATH_REPORTS)

    private fun reportReference(key: String): DatabaseReference = reportsReference()
        .child(key)

    fun getAllQuery() =
        reportsReference()
            .orderByChild("timestamp")

    fun insert(report: Report, onComplete: (DatabaseError?) -> Unit): String? {
        val newChild = reportsReference().push()
        newChild.setValue(report) { error, _ ->
            onComplete(error)
        }
        return newChild.key
    }

    fun update(report: Report, onComplete: (DatabaseError?) -> Unit) {
        val key = report.key ?: return
        reportReference(key).setValue(report) { error, _ ->
            onComplete(error)
        }
    }

    fun delete(key: String, onComplete: (DatabaseError?) -> Unit) {
        reportReference(key).removeValue { error, _ ->
            onComplete(error)
        }
    }
}
